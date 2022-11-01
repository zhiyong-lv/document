# default logging in spring boot 
## spring commons-logging
spring提供了一个最小化的common logging，主要作用是实现了通用的Log查找方法。
这个类的使用方法和apache的common logging相同。

其工作流程如下：
> This implementation does not support Commons Logging's original provider detection. 
> It rather only checks for the presence of the Log4j 2.x API and the SLF4J 1.7 API 
> in the Spring Framework classpath, falling back to java.util.logging if none of the 
> two is available
> 

通过上面的可以看出，这个包和apache版本的不同之处是，他不支持原来的provider方式检测，而是通过在CLASSPATH
内查找log4j或者slf4j。如果都没有找到，那么就是用哪个java.util.logging

默认情况下，使用的是SLF4J。

## SLF4J
这也是一个门面框架，而不是具体的日志系统实现。
首先，会对目前使用的LoggingFactory进行初始化，而这里初始化的方式是使用静态的方式来进行的，而不是通过classloader
的方式进行的。

### 源码分析
一般获取log的方式如下
```java
Logger logger = LoggerFactory.getLogger(name);
```

而在slf4j里面的getLogger方法，会在第一次调用时尝试对loggingFactory进行初始化。
- 第一次执行的时候INITIALIZATION_STATE的值是UNINITIALIZED
- 之后执行成功后，为SUCCESSFUL_INITIALIZATION
```java
public static ILoggerFactory getILoggerFactory() {
    if (INITIALIZATION_STATE == UNINITIALIZED) {
        synchronized (LoggerFactory.class) {
            if (INITIALIZATION_STATE == UNINITIALIZED) {
                INITIALIZATION_STATE = ONGOING_INITIALIZATION;
                performInitialization();
            }
        }
    }
    switch (INITIALIZATION_STATE) {
    case SUCCESSFUL_INITIALIZATION:
        return StaticLoggerBinder.getSingleton().getLoggerFactory();
    case NOP_FALLBACK_INITIALIZATION:
        return NOP_FALLBACK_FACTORY;
    case FAILED_INITIALIZATION:
        throw new IllegalStateException(UNSUCCESSFUL_INIT_MSG);
    case ONGOING_INITIALIZATION:
        // support re-entrant behavior.
        // See also http://jira.qos.ch/browse/SLF4J-97
        return SUBST_FACTORY;
    }
    throw new IllegalStateException("Unreachable code");
}
```

所以这里初始化是在performInitialization方法中执行的。西面的程序分为两部分：
- bind会绑定要使用的logger的工厂方法
- 然后会进行兼容性检查等操作，判断是否可以运行。
```java
private final static void performInitialization() {
    bind();
    if (INITIALIZATION_STATE == SUCCESSFUL_INITIALIZATION) {
        versionSanityCheck();
    }
}
```

然后重点看bind方法
通过对bind的方法的分析可以看到：
1. 首先会去查找静态的binder，如果当前的classpath中的binder数量大于1个，那么就会上报错误
2. 如果只有一个StaticLoggerBinder且已经加载，那么就执行getSingleton方法，对其内部进行初始化操作。
3. 如果这时classpath里面的StaticLoggerBinder是log4j的，那么slf4j就已经和log4j自动绑定；如果是logback的，那么就会和logback绑定
4. 最后将状态设置为初始化成功（SUCCESSFUL_INITIALIZATION），这样当下次再次获取logger的时候就不需要再次bind了。
```java
private final static void bind() {
    try {
        Set<URL> staticLoggerBinderPathSet = null;
        // skip check under android, see also
        // http://jira.qos.ch/browse/SLF4J-328
        if (!isAndroid()) {
            staticLoggerBinderPathSet = findPossibleStaticLoggerBinderPathSet();
            reportMultipleBindingAmbiguity(staticLoggerBinderPathSet);
        }
        // the next line does the binding
        StaticLoggerBinder.getSingleton();
        INITIALIZATION_STATE = SUCCESSFUL_INITIALIZATION;
        reportActualBinding(staticLoggerBinderPathSet);
    } catch (NoClassDefFoundError ncde) {
        String msg = ncde.getMessage();
        if (messageContainsOrgSlf4jImplStaticLoggerBinder(msg)) {
            INITIALIZATION_STATE = NOP_FALLBACK_INITIALIZATION;
            Util.report("Failed to load class \"org.slf4j.impl.StaticLoggerBinder\".");
            Util.report("Defaulting to no-operation (NOP) logger implementation");
            Util.report("See " + NO_STATICLOGGERBINDER_URL + " for further details.");
        } else {
            failedBinding(ncde);
            throw ncde;
        }
    } catch (java.lang.NoSuchMethodError nsme) {
        String msg = nsme.getMessage();
        if (msg != null && msg.contains("org.slf4j.impl.StaticLoggerBinder.getSingleton()")) {
            INITIALIZATION_STATE = FAILED_INITIALIZATION;
            Util.report("slf4j-api 1.6.x (or later) is incompatible with this binding.");
            Util.report("Your binding is version 1.5.5 or earlier.");
            Util.report("Upgrade your binding to version 1.6.x.");
        }
        throw nsme;
    } catch (Exception e) {
        failedBinding(e);
        throw new IllegalStateException("Unexpected initialization failure", e);
    } finally {
        postBindCleanUp();
    }
}
```

最后再来看findPossibleStaticLoggerBinderPathSet方法。这里binder的路径是固定的。
这里会将classpath中所有的实现了StaticLoggerBinder的class都加载进来，放到URL里面。
这里虽然使用了动态加载，但实际上是为了防止多个StaticLoggerBinder类同时出现的异常而
进行的操作。
```java

// We need to use the name of the StaticLoggerBinder class, but we can't
// reference
// the class itself.
private static String STATIC_LOGGER_BINDER_PATH = "org/slf4j/impl/StaticLoggerBinder.class";

static Set<URL> findPossibleStaticLoggerBinderPathSet() {
    // use Set instead of list in order to deal with bug #138
    // LinkedHashSet appropriate here because it preserves insertion order
    // during iteration
    Set<URL> staticLoggerBinderPathSet = new LinkedHashSet<URL>();
    try {
        ClassLoader loggerFactoryClassLoader = LoggerFactory.class.getClassLoader();
        Enumeration<URL> paths;
        if (loggerFactoryClassLoader == null) {
            paths = ClassLoader.getSystemResources(STATIC_LOGGER_BINDER_PATH);
        } else {
            paths = loggerFactoryClassLoader.getResources(STATIC_LOGGER_BINDER_PATH);
        }
        while (paths.hasMoreElements()) {
            URL path = paths.nextElement();
            staticLoggerBinderPathSet.add(path);
        }
    } catch (IOException ioe) {
        Util.report("Error getting resources from path", ioe);
    }
    return staticLoggerBinderPathSet;
}
```
