# Async 和 EventListener 一起使用失败
## 问题描述
### 异常信息
当Async和EventListener一起使用的时候，抛出如下异常。
```text
2022-11-03 01:14:53 [scanTaskPool-127491990214957-1] ERROR  send event error. the event is ITEM_SCAN_DONE.
java.lang.IllegalStateException: The event listener method class '********.MaskingPatternLayoutScanStateListener' is not an instance of the actual bean class 'com.sun.proxy.$Proxy96'. If the bean requires proxying (e.g. due to @Transactional), please use class-based proxying.
HandlerMethod details: 
Bean [com.sun.proxy.$Proxy96]
Method [public void ******.MaskingPatternLayoutScanStateListener.scanStateChangedEventListener(********.ScanStateChangedEvent)]
Resolved arguments: 
[0] [type=*******.ScanStateChangedEvent] [value=*****.ScanStateChangedEvent[source=HostEntity(hostId=1137788, platform=Linux, domain=, selected=false, active=true, createDate=Wed Nov 02 18:05:36 CST 2022, lastModifiedDate=Wed Nov 02 18:05:36 CST 2022, lastScanDate=Thu Nov 03 13:10:21 CST 2022)]]

    at org.springframework.context.event.ApplicationListenerMethodAdapter.assertTargetBean(ApplicationListenerMethodAdapter.java:421)
    at org.springframework.context.event.ApplicationListenerMethodAdapter.doInvoke(ApplicationListenerMethodAdapter.java:347)
    at org.springframework.context.event.ApplicationListenerMethodAdapter.processEvent(ApplicationListenerMethodAdapter.java:229)
    at org.springframework.context.event.ApplicationListenerMethodAdapter.onApplicationEvent(ApplicationListenerMethodAdapter.java:166)
    at org.springframework.context.event.SimpleApplicationEventMulticaster.doInvokeListener(SimpleApplicationEventMulticaster.java:176)
    at org.springframework.context.event.SimpleApplicationEventMulticaster.invokeListener(SimpleApplicationEventMulticaster.java:169)
    at org.springframework.context.event.SimpleApplicationEventMulticaster.multicastEvent(SimpleApplicationEventMulticaster.java:143)
    at org.springframework.context.support.AbstractApplicationContext.publishEvent(AbstractApplicationContext.java:421)
    at org.springframework.context.support.AbstractApplicationContext.publishEvent(AbstractApplicationContext.java:378)
    at *****.fireStateChangedEvent(AbstractHostScanWorker.java:37)
    at *****.doWork(LinuxHostScanWorkerImpl.java:44)
    at *****.scan(AbstractHostScanWorker.java:56)
    at *****.ScanServiceImpl$ScanTask.lambda$scan$0(ScanServiceImpl.java:108)
    at java.base/java.util.concurrent.FutureTask.run$$$capture(FutureTask.java:264)
    at java.base/java.util.concurrent.FutureTask.run(FutureTask.java)
    at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
    at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
    at java.base/java.lang.Thread.run(Thread.java:829)
```

### 环境信息
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.6.2</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <properties>
        <java.version>1.8</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-tomcat</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.2.1</version>
        </dependency>
        <dependency>
            <groupId>org.xerial</groupId>
            <artifactId>sqlite-jdbc</artifactId>
            <version>3.36.0.3</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.awaitility</groupId>
            <artifactId>awaitility</artifactId>
            <version>4.2.0</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-cache</artifactId>
        </dependency>
        <dependency>
            <groupId>com.github.ben-manes.caffeine</groupId>
            <artifactId>caffeine</artifactId>
            <version>2.9.3</version>
        </dependency>
        <dependency>
            <groupId>org.jetbrains</groupId>
            <artifactId>annotations</artifactId>
            <version>22.0.0</version>
        </dependency>
        <dependency>
            <groupId>com.jcraft</groupId>
            <artifactId>jsch</artifactId>
            <version>0.1.54</version>
        </dependency>
        <dependency>
            <groupId>commons-io</groupId>
            <artifactId>commons-io</artifactId>
            <version>1.3.2</version>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-csv</artifactId>
            <version>1.9.0</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.79</version>
        </dependency>
    </dependencies>
</project>
```

### 相关代码
#### 主函数
```java
@Slf4j
@EnableAsync()
@EnableCaching
@SpringBootApplication
public class RaasApplication {
}
```

#### event handler
```java
import org.springframework.scheduling.annotation.Async;

@Slf4j
@Component
class MaskingPatternLayoutScanStateListener implements ScanStateChangedEvent.EventListener {
    @Override
    @Async
    @EventListener
    @Order(50)
    public void scanStateChangedEventListener(@NotNull ScanStateChangedEvent scanStateChangedEvent) {
    }
}
```

## 问题分析
当查看到出现问题的栈信息的时候，怀疑Async未生效，还是走的同步调用的方式。于是，去掉Async注解，在同步模式下，打印正常执行的函数的栈信息。
堆栈信息如下所示。从这些堆栈信息可以看出，之前的Async确实没有生效。还是走的同步方式。
```text
2022-11-03 01:35:10 [scanTaskPool-128705163856667-1] ERROR c.v.s.r.l.MaskingPatternLayoutScanStateListener 127 [hostId=1137788] - 
java.lang.Exception: null
	at *****.MaskingPatternLayoutScanStateListener.scanStateChangedEventListener(MaskingPatternLayout.java:125)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at org.springframework.context.event.ApplicationListenerMethodAdapter.doInvoke(ApplicationListenerMethodAdapter.java:344)
	at org.springframework.context.event.ApplicationListenerMethodAdapter.processEvent(ApplicationListenerMethodAdapter.java:229)
	at org.springframework.context.event.ApplicationListenerMethodAdapter.onApplicationEvent(ApplicationListenerMethodAdapter.java:166)
	at org.springframework.context.event.SimpleApplicationEventMulticaster.doInvokeListener(SimpleApplicationEventMulticaster.java:176)
	at org.springframework.context.event.SimpleApplicationEventMulticaster.invokeListener(SimpleApplicationEventMulticaster.java:169)
	at org.springframework.context.event.SimpleApplicationEventMulticaster.multicastEvent(SimpleApplicationEventMulticaster.java:143)
	at org.springframework.context.support.AbstractApplicationContext.publishEvent(AbstractApplicationContext.java:421)
	at org.springframework.context.support.AbstractApplicationContext.publishEvent(AbstractApplicationContext.java:378)
	at *****.AbstractHostScanWorker.fireStateChangedEvent(AbstractHostScanWorker.java:37)
	at *****.LinuxHostScanWorkerImpl.doWork(LinuxHostScanWorkerImpl.java:44)
	at *****.AbstractHostScanWorker.scan(AbstractHostScanWorker.java:56)
	at *****.ScanServiceImpl$ScanTask.lambda$scan$0(ScanServiceImpl.java:108)
	at java.base/java.util.concurrent.FutureTask.run$$$capture(FutureTask.java:264)
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
	at java.base/java.lang.Thread.run(Thread.java:829)
```

错误信息中提示`java.lang.IllegalStateException: The event listener method class '********.MaskingPatternLayoutScanStateListener' is not an instance of the actual bean class 'com.sun.proxy.$Proxy96'. If the bean requires proxying (e.g. due to @Transactional), please use class-based proxying.`
这说明，Async的加入，是的类本身变为了动态代理的bean，当event handler进行调用的时候，由于类型异常导致错误。
需要使用cglib的方式，通过创建子类的方式才能正常工作。

## 问题修复
将代理方式修改为cglib，此问题解决
```java
@Slf4j
@EnableAsync(proxyTargetClass = true)
@EnableCaching
@SpringBootApplication
public class RaasApplication {
}
```

再次运行，并在handler函数内部打印出堆栈信息。这里的堆栈信息和之前的有很大的不同，已经没有了event handler调用部分的代码，
从这个堆栈信息可以分析得到，当前的程序已经处于异步执行了。通过查看日志中的thread信息也可以看出，这时的thread已经是defaultPool-1。
说明之前的event handler的流程已经处理完成，并由异步执行的代理类启动了新的线程来异步执行了任务。
```text
2022-11-03 01:10:17 [defaultPool-1] INFO  c.v.s.r.l.MaskingPatternLayoutScanStateListener 129 [hostId=-1] - 
java.lang.RuntimeException: null
    at *****.MaskingPatternLayoutScanStateListener.scanStateChangedEventListener(MaskingPatternLayout.java:127)
    at *****.MaskingPatternLayoutScanStateListener$$FastClassBySpringCGLIB$$7ef822fd.invoke(<generated>)
    at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218)
    at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:783)
    at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)
    at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.proceed(CglibAopProxy.java:753)
    at org.springframework.aop.interceptor.AsyncExecutionInterceptor.lambda$invoke$0(AsyncExecutionInterceptor.java:115)
    at java.base/java.util.concurrent.FutureTask.run$$$capture(FutureTask.java:264)
    at java.base/java.util.concurrent.FutureTask.run(FutureTask.java)
    at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
    at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
    at java.base/java.lang.Thread.run(Thread.java:829)
```

## 原理
这个issue的初步解决办法找到了，进一步分析其中的根因，还有以下的一些疑问：
1. Spring生成哪种代理类的判断方式是什么
2. Spring event handler，以及async机制是如何工作的，他们是怎么配合在一起的

下面首先先看一下spring使用的两种基础的代理方式：
### 代理
#### JDK动态代理
JDK动态代理的使用条件：
1. 必须实现InvocationHandler接口； 
2. 使用Proxy.newProxyInstance产生代理对象； 
3. 被代理的对象必须要实现接口；

具体的使用方式可以参考这个[文章](https://www.jianshu.com/p/9d5557b5c8d0)
目前使用的JDK版本为OpenJDK 11，这和上面的文章有所不同，但大体的流程是相同的。

创建proxy的入口函数如下
```java
@CallerSensitive
public static Object newProxyInstance(ClassLoader loader,
                                      Class<?>[] interfaces,
                                      InvocationHandler h) {
    Objects.requireNonNull(h);

    final Class<?> caller = System.getSecurityManager() == null
                                ? null
                                : Reflection.getCallerClass();

    /*
     * Look up or generate the designated proxy class and its constructor.
     * 获得一个以InvocationHandler为参数的构造方法
     */
    Constructor<?> cons = getProxyConstructor(caller, loader, interfaces);

    // 使用改构造方法，创建代理类
    return newProxyInstance(caller, cons, h);
}
```

首先看一下构造器是如何创建的
```java

    /**
     * Returns the {@code Constructor} object of a proxy class that takes a
     * single argument of type {@link InvocationHandler}, given a class loader
     * and an array of interfaces. The returned constructor will have the
     * {@link Constructor#setAccessible(boolean) accessible} flag already set.
     *
     * @param   caller passed from a public-facing @CallerSensitive method if
     *                 SecurityManager is set or {@code null} if there's no
     *                 SecurityManager
     * @param   loader the class loader to define the proxy class
     * @param   interfaces the list of interfaces for the proxy class
     *          to implement
     * @return  a Constructor of the proxy class taking single
     *          {@code InvocationHandler} parameter
     */
    private static Constructor<?> getProxyConstructor(Class<?> caller,
                                                      ClassLoader loader,
                                                      Class<?>... interfaces)
    {
        // 这里和下面基本上重复的，一个是针对只有一个接口的时候做的特殊的优化，另一个是多个接口的 
        if (interfaces.length == 1) {
            Class<?> intf = interfaces[0];
            if (caller != null) {
                checkProxyAccess(caller, loader, intf);
            }
            return proxyCache.sub(intf).computeIfAbsent(
                loader,
                (ld, clv) -> new ProxyBuilder(ld, clv.key()).build()
            );
        } else {
            // interfaces cloned
            final Class<?>[] intfsArray = interfaces.clone();
            if (caller != null) {
                checkProxyAccess(caller, loader, intfsArray);
            }
            final List<Class<?>> intfs = Arrays.asList(intfsArray);
            return proxyCache.sub(intfs).computeIfAbsent(
                loader,
                (ld, clv) -> new ProxyBuilder(ld, clv.key()).build()
            );
        }
    }
```

这里关键代码就是proxyCache.computeIfAbsent方法。
> proxyCache.sub(intf).computeIfAbsent(loader, (ld, clv) -> new ProxyBuilder(ld, clv.key()).build());

首先看sub函数，它的作用是返回一个Sub类型的instance，然后再调用这个实例的computeIfAbsent方法。
这个Sub类型的主要目的是为了在cache中查找相同接口类型的class。为了找到接口相同的class，Sub覆写了equal方法。
也就是说，只要intf是相同的，那么对应的Sub实例也是相等的。

之后在看computeIfAbsent的作用。
```java

    /**
     * Returns the value associated with this ClassLoaderValue and given
     * ClassLoader if there is one or computes the value by invoking given
     * {@code mappingFunction}, associates it and returns it.
     * <p>
     * Computation and association of the computed value is performed atomically
     * by the 1st thread that requests a particular association while holding a
     * lock associated with this ClassLoaderValue and given ClassLoader.
     * Nested calls from the {@code mappingFunction} to {@link #get},
     * {@link #putIfAbsent} or {@link #computeIfAbsent} for the same association
     * are not allowed and throw {@link IllegalStateException}. Nested call to
     * {@link #remove} for the same association is allowed but will always return
     * {@code false} regardless of passed-in comparison value. Nested calls for
     * other association(s) are allowed, but care should be taken to avoid
     * deadlocks. When two threads perform nested computations of the overlapping
     * set of associations they should always request them in the same order.
     *
     * @param cl              the ClassLoader for the associated value
     * @param mappingFunction the function to compute the value
     * @return the value associated with this ClassLoaderValue and given
     * ClassLoader.
     * @throws IllegalStateException if a direct or indirect invocation from
     *                               within given {@code mappingFunction} that
     *                               computes the value of a particular association
     *                               to {@link #get}, {@link #putIfAbsent} or
     *                               {@link #computeIfAbsent}
     *                               for the same association is attempted.
     */
    public V computeIfAbsent(ClassLoader cl,
                             BiFunction<
                                 ? super ClassLoader,
                                 ? super CLV,
                                 ? extends V
                                 > mappingFunction) throws IllegalStateException {
        ConcurrentHashMap<CLV, Object> map = map(cl);
        @SuppressWarnings("unchecked")
        CLV clv = (CLV) this;
        Memoizer<CLV, V> mv = null;
        while (true) {
            // 这里的clv就是之前的Sub类型，也就是说，只要这个接口之前建立过一次proxy class，那么再次执行的时候就会返回之前的值
            Object val = (mv == null) ? map.get(clv) : map.putIfAbsent(clv, mv);
            if (val == null) {
                if (mv == null) {
                    // create Memoizer lazily when 1st needed and restart loop
                    mv = new Memoizer<>(cl, clv, mappingFunction);
                    continue;
                }
                // mv != null, therefore sv == null was a result of successful
                // putIfAbsent
                try {
                    // trigger Memoizer to compute the value
                    // 如果没有，那么就是调用get方法，创建构造器
                    V v = mv.get();
                    // attempt to replace our Memoizer with the value
                    map.replace(clv, mv, v);
                    // return computed value
                    return v;
                } catch (Throwable t) {
                    // our Memoizer has thrown, attempt to remove it
                    map.remove(clv, mv);
                    // propagate exception because it's from our Memoizer
                    throw t;
                }
            } else {
                try {
                    return extractValue(val);
                } catch (Memoizer.RecursiveInvocationException e) {
                    // propagate recursive attempts to calculate the same
                    // value as being calculated at the moment
                    throw e;
                } catch (Throwable t) {
                    // don't propagate exceptions thrown from foreign Memoizer -
                    // pretend that there was no entry and retry
                    // (foreign computeIfAbsent invocation will try to remove it anyway)
                }
            }
            // TODO:
            // Thread.onSpinLoop(); // when available
        }
    }
```

实际的构造器是通过get方法创建的，代码如下
```java

        @Override
        public V get() throws RecursiveInvocationException {
            V v = this.v;
            // 如果之前创建过了，直接返回
            if (v != null) return v;
            Throwable t = this.t;
            // 如果之前创建的时候返回了异常，也返回
            if (t == null) {
                synchronized (this) {
                    // 双重锁判断，防止线程竞争执行
                    if ((v = this.v) == null && (t = this.t) == null) {
                        if (inCall) {
                            throw new RecursiveInvocationException();
                        }
                        inCall = true;
                        try {
                            // 调用外面传进来的mappingFunction.apply完成实际的创建。
                            this.v = v = Objects.requireNonNull(
                                mappingFunction.apply(cl, clv));
                        } catch (Throwable x) {
                            this.t = t = x;
                        } finally {
                            inCall = false;
                        }
                    }
                }
            }
            if (v != null) return v;
            if (t instanceof Error) {
                throw (Error) t;
            } else if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else {
                throw new UndeclaredThrowableException(t);
            }
        }
```

创建构造器的实际代码就是`new ProxyBuilder(ld, clv.key()).build()`，这里ld是classload，clv则是Sub类的实例，key方法则是里面保存的接口信息。
build方法代码如下：
```java

        /**
         * Generate a proxy class and return its proxy Constructor with
         * accessible flag already set. If the target module does not have access
         * to any interface types, IllegalAccessError will be thrown by the VM
         * at defineClass time.
         *
         * Must call the checkProxyAccess method to perform permission checks
         * before calling this.
         */
        Constructor<?> build() {
            // 创建一个proxy class
            Class<?> proxyClass = defineProxyClass(module, interfaces);
            final Constructor<?> cons;
            try {
                // 得到参数为IvocationHandler的构造器
                cons = proxyClass.getConstructor(constructorParams);
            } catch (NoSuchMethodException e) {
                throw new InternalError(e.toString(), e);
            }
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
                public Void run() {
                    // 设置访问权限
                    cons.setAccessible(true);
                    return null;
                }
            });
            return cons;
        }
```

然后再看真正创建代理类的方法defineProxyClass。
1. 找一个不是public，也不是final的接口，把proxy class的package设置为和它相同。如果有两个不在同一个包的这样的接口，那么会抛出异常
2. 如果没有不是public也不是final的接口，那么使用传入的Module的name和com.sun.proxy计算后的结果
3. 根据类的名字，field名字，接口定义的方法和访问限定，按照java虚拟机规范生成对应的代理类的字节码
4. 将代理类加载到虚拟机中。

可以看到，代码中很多逻辑都是用来产生代理类的全限定名字的。代理类的全限定名中有package的名字以及该类的名字。
在java中，package的名字对访问的权限影响很大。特别是在java9之后，由于module的引入，导致访问规则更加的复杂，
如果设置的代理类所在的package有问题，那么会造成该代理类无法被正确访问。

```java

    /**
     * Builder for a proxy class.
     *
     * If the module is not specified in this ProxyBuilder constructor,
     * it will map from the given loader and interfaces to the module
     * in which the proxy class will be defined.
     */
    private static final class ProxyBuilder {
        private static final Unsafe UNSAFE = Unsafe.getUnsafe();

        // prefix for all proxy class names
        private static final String proxyClassNamePrefix = "$Proxy";

        // next number to use for generation of unique proxy class names
        private static final AtomicLong nextUniqueNumber = new AtomicLong();

        // a reverse cache of defined proxy classes
        private static final ClassLoaderValue<Boolean> reverseProxyCache =
                new ClassLoaderValue<>();

        private static Class<?> defineProxyClass(Module m, List<Class<?>> interfaces) {
            String proxyPkg = null;     // package to define proxy class in
            int accessFlags = Modifier.PUBLIC | Modifier.FINAL;

            /*
             * Record the package of a non-public proxy interface so that the
             * proxy class will be defined in the same package.  Verify that
             * all non-public proxy interfaces are in the same package.
             * 找一个不是public，也不是final的接口，把proxy class的package设置为和它相同
             * 如果有两个不在同一个包的这样的接口，那么会抛出异常
             */
            for (Class<?> intf : interfaces) {
                int flags = intf.getModifiers();
                if (!Modifier.isPublic(flags)) {
                    accessFlags = Modifier.FINAL;  // non-public, final
                    String pkg = intf.getPackageName();
                    if (proxyPkg == null) {
                        proxyPkg = pkg;
                    } else if (!pkg.equals(proxyPkg)) {
                        throw new IllegalArgumentException(
                                "non-public interfaces from different packages");
                    }
                }
            }

            if (proxyPkg == null) {
                // all proxy interfaces are public
                proxyPkg = m.isNamed() ? PROXY_PACKAGE_PREFIX + "." + m.getName()
                        : PROXY_PACKAGE_PREFIX;
            } else if (proxyPkg.isEmpty() && m.isNamed()) {
                throw new IllegalArgumentException(
                        "Unnamed package cannot be added to " + m);
            }

            if (m.isNamed()) {
                if (!m.getDescriptor().packages().contains(proxyPkg)) {
                    throw new InternalError(proxyPkg + " not exist in " + m.getName());
                }
            }

            /*
             * Choose a name for the proxy class to generate.
             * 最终，确定proxy的名字
             */
            long num = nextUniqueNumber.getAndIncrement();
            String proxyName = proxyPkg.isEmpty()
                    ? proxyClassNamePrefix + num
                    : proxyPkg + "." + proxyClassNamePrefix + num;

            ClassLoader loader = getLoader(m);
            trace(proxyName, m, loader, interfaces);

            /*
             * Generate the specified proxy class.
             * 产生代理类的字节码。
             * 其原理就是把给定的名字，接口名字和访问限定，按照java字节码的规定，生成字节码
             * 如果进行了设置，那么会把生成的字节码写入制定的位置。
             */
            byte[] proxyClassFile = ProxyGenerator.generateProxyClass(
                    proxyName, interfaces.toArray(EMPTY_CLASS_ARRAY), accessFlags);
            try {
                // 将生成的代理类的字节码使用类加载器加载到虚拟机中
                Class<?> pc = UNSAFE.defineClass(proxyName, proxyClassFile,
                        0, proxyClassFile.length,
                        loader, null);
                reverseProxyCache.sub(pc).putIfAbsent(loader, Boolean.TRUE);
                return pc;
            } catch (ClassFormatError e) {
                /*
                 * A ClassFormatError here means that (barring bugs in the
                 * proxy class generation code) there was some other
                 * invalid aspect of the arguments supplied to the proxy
                 * class creation (such as virtual machine limitations
                 * exceeded).
                 */
                throw new IllegalArgumentException(e.toString());
            }
        }
    }
```

总结一下，JDK的动态代理生成了一个代理类字节码，然后把它加载到内存中。这个代理类内部实现了需要的接口的方法。而在这个代理内部，
各个接口方法都会调用InvocationHandler中对应的方法。

#### Cglib动态代理
cglib动态代理的原理，可以参考这篇[文章](https://www.jianshu.com/p/7700a48811e0)

通过对源码的分析可以知道
1. cglib是通过集成被代理类来实现代理功能的。也就是说，它不需要对众多的interface进行判断，从中取一个作为包名。cglib所在的包一般来说就是创建代理类时候的包名。
2. cglib会生成多个文件，分别是代理类，代理类的fast类，和被代理类的fast类
3. cglib通过直接调用方法，避免了反射的使用 

#### Cglib和动态代理的区别
- JDK 动态代理基于接口，CGLIB 动态代理基于类。因为 JDK 动态代理生成的代理类需要继承 java.lang.reflect.Proxy，而 Java 只支持单继承，所以只能基于接口。
- JDK 动态代理和 CGLIB 动态代理都是在运行期生成字节码，JDK 是直接写 Class 字节码，CGLIB 使用 ASM 框架写 Class 字节码。
- JDK 通过反射机制调用方法，CGLIB 通过 FastClass 机制直接调用方法，所以 CGLIB 执行的效率更高。