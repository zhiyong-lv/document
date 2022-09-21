# Collection

## Async
### ConcurrentHashMap
#### features
1. 功能和HashTable相同，都不支持null值为key或者value
2. 使用的数据结构和HashMap相同，都是使用了array作为Hash的槽位（bucket），如果发生hash冲突，使用链表法或者二叉树来解决（bins）
3. 读功能不会加锁，这样就导致iterate遍历的时候，拿到的值可能是创建iterator之后改变的
4. put功能尽量不加锁，如果要加锁，那么只对单个槽位加锁


#### 代码分析
```java
class ConcurrentHashMap {

    /* ---------------- Nodes -------------- */

    /**
     * Key-value entry.  This class is never exported out as a
     * user-mutable Map.Entry (i.e., one supporting setValue; see
     * MapEntry below), but can be used for read-only traversals used
     * in bulk tasks.  Subclasses of Node with a negative hash field
     * are special, and contain null keys and values (but are never
     * exported).  Otherwise, keys and vals are never null.
     * 这个Node是ConcurrentHashMap里面所有的节点的类或者父类，它有一些特殊的
     * 子类，其key和val为0，hash值为负数
     */
    static class Node<K,V> implements Map.Entry<K,V> {
        // hash为final说明设置后不可变。这个也是合理的，因为key也是final的，而hash值是由key产生的
        // 但这里比较特殊的是，hash值是由val和key共同产生的。不过通过注释和setValue函数可以看出
        // 这里的value值不能修改。
        final int hash;
        // 对于hash map来说，key是基础。key可以变化，那么HashMap也就没有存在的意义。
        // 一个新的key值，意味着一个新的Node。
        final K key;
        // val值为volatile的，保证在不同cpu核的可见行
        volatile V val;
        // next作为指向下一个Node的指针，也同时存在与Node及所有的子类中。
        // 对于TreeNode也是一样的，这样也就说明，TreeNode除了通过二叉树二分查找，
        // 也可以通过next进行遍历
        volatile Node<K,V> next;

        Node(int hash, K key, V val, Node<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.val = val;
            this.next = next;
        }

        public final K getKey()       { return key; }
        public final V getValue()     { return val; }
        public final int hashCode()   { return key.hashCode() ^ val.hashCode(); }
        public final String toString(){ return key + "=" + val; }
        public final V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        /**
         * 子类中可能会重写这个函数？
         * 输入的o是map.entry，且它的key和value都不为null。
         * 且key和value都相等或者equalTo，
         * @param o
         * @return
         */
        public final boolean equals(Object o) {
            Object k, v, u; Map.Entry<?,?> e;
            return ((o instanceof Map.Entry) &&
                    (k = (e = (Map.Entry<?,?>)o).getKey()) != null &&
                    (v = e.getValue()) != null &&
                    (k == key || k.equals(key)) &&
                    (v == (u = val) || v.equals(u)));
        }

        /**
         * Virtualized support for map.get(); overridden in subclasses.
         * 从当前的节点开始，顺着next指针遍历查找是否能找到对应的key和hash值
         */
        Node<K,V> find(int h, Object k) {
            Node<K,V> e = this;
            if (k != null) {
                do {
                    K ek;
                    if (e.hash == h &&
                            ((ek = e.key) == k || (ek != null && k.equals(ek))))
                        return e;
                } while ((e = e.next) != null);
            }
            return null;
        }
    }

    /**
     * A place-holder node used in computeIfAbsent and compute
     * RESERVED=-3，说明ReservtionNode也是一个特殊的Node，其key和value都为null
     * 不是用来保存数据的节点。
     */
    static final class ReservationNode<K,V> extends Node<K,V> {
        ReservationNode() {
            super(RESERVED, null, null, null);
        }

        Node<K,V> find(int h, Object k) {
            return null;
        }
    }


    /**
     * TreeNodes used at the heads of bins. TreeBins do not hold user
     * keys or values, but instead point to list of TreeNodes and
     * their root. They also maintain a parasitic read-write lock
     * forcing writers (who hold bin lock) to wait for readers (who do
     * not) to complete before tree restructuring operations.
     */
    static final class TreeBin<K,V> extends Node<K,V> {
        TreeNode<K,V> root;
        volatile TreeNode<K,V> first;
        volatile Thread waiter;
        volatile int lockState;
        // values for lockState
        static final int WRITER = 1; // set while holding write lock
        static final int WAITER = 2; // set when waiting for write lock
        static final int READER = 4; // increment value for setting read lock

        /**
         * Tie-breaking utility for ordering insertions when equal
         * hashCodes and non-comparable. We don't require a total
         * order, just a consistent insertion rule to maintain
         * equivalence across rebalancings. Tie-breaking further than
         * necessary simplifies testing a bit.
         */
        static int tieBreakOrder(Object a, Object b) {
            int d;
            if (a == null || b == null ||
                    (d = a.getClass().getName().
                            compareTo(b.getClass().getName())) == 0)
                d = (System.identityHashCode(a) <= System.identityHashCode(b) ?
                        -1 : 1);
            return d;
        }

        /**
         * Creates bin with initial set of nodes headed by b.
         */
        TreeBin(TreeNode<K,V> b) {
            super(TREEBIN, null, null, null);
            this.first = b;
            TreeNode<K,V> r = null;
            for (TreeNode<K,V> x = b, next; x != null; x = next) {
                next = (TreeNode<K,V>)x.next;
                x.left = x.right = null;
                if (r == null) {
                    x.parent = null;
                    x.red = false;
                    r = x;
                }
                else {
                    K k = x.key;
                    int h = x.hash;
                    Class<?> kc = null;
                    for (TreeNode<K,V> p = r;;) {
                        int dir, ph;
                        K pk = p.key;
                        if ((ph = p.hash) > h)
                            dir = -1;
                        else if (ph < h)
                            dir = 1;
                        else if ((kc == null &&
                                (kc = comparableClassFor(k)) == null) ||
                                (dir = compareComparables(kc, k, pk)) == 0)
                            dir = tieBreakOrder(k, pk);
                        TreeNode<K,V> xp = p;
                        if ((p = (dir <= 0) ? p.left : p.right) == null) {
                            x.parent = xp;
                            if (dir <= 0)
                                xp.left = x;
                            else
                                xp.right = x;
                            r = balanceInsertion(r, x);
                            break;
                        }
                    }
                }
            }
            this.root = r;
            assert checkInvariants(root);
        }

        /**
         * Acquires write lock for tree restructuring.
         */
        private final void lockRoot() {
            if (!U.compareAndSwapInt(this, LOCKSTATE, 0, WRITER))
                contendedLock(); // offload to separate method
        }

        /**
         * Releases write lock for tree restructuring.
         */
        private final void unlockRoot() {
            lockState = 0;
        }

        /**
         * Possibly blocks awaiting root lock.
         */
        private final void contendedLock() {
            boolean waiting = false;
            for (int s;;) {
                if (((s = lockState) & ~WAITER) == 0) {
                    if (U.compareAndSwapInt(this, LOCKSTATE, s, WRITER)) {
                        if (waiting)
                            waiter = null;
                        return;
                    }
                }
                else if ((s & WAITER) == 0) {
                    if (U.compareAndSwapInt(this, LOCKSTATE, s, s | WAITER)) {
                        waiting = true;
                        waiter = Thread.currentThread();
                    }
                }
                else if (waiting)
                    LockSupport.park(this);
            }
        }

        /**
         * Returns matching node or null if none. Tries to search
         * using tree comparisons from root, but continues linear
         * search when lock not available.
         */
        final Node<K,V> find(int h, Object k) {
            if (k != null) {
                for (Node<K,V> e = first; e != null; ) {
                    int s; K ek;
                    if (((s = lockState) & (WAITER|WRITER)) != 0) {
                        if (e.hash == h &&
                                ((ek = e.key) == k || (ek != null && k.equals(ek))))
                            return e;
                        e = e.next;
                    }
                    else if (U.compareAndSwapInt(this, LOCKSTATE, s,
                            s + READER)) {
                        TreeNode<K,V> r, p;
                        try {
                            p = ((r = root) == null ? null :
                                    r.findTreeNode(h, k, null));
                        } finally {
                            Thread w;
                            if (U.getAndAddInt(this, LOCKSTATE, -READER) ==
                                    (READER|WAITER) && (w = waiter) != null)
                                LockSupport.unpark(w);
                        }
                        return p;
                    }
                }
            }
            return null;
        }

        /**
         * Finds or adds a node.
         * @return null if added
         */
        final TreeNode<K,V> putTreeVal(int h, K k, V v) {
            Class<?> kc = null;
            boolean searched = false;
            for (TreeNode<K,V> p = root;;) {
                int dir, ph; K pk;
                if (p == null) {
                    first = root = new TreeNode<K,V>(h, k, v, null, null);
                    break;
                }
                else if ((ph = p.hash) > h)
                    dir = -1;
                else if (ph < h)
                    dir = 1;
                else if ((pk = p.key) == k || (pk != null && k.equals(pk)))
                    return p;
                else if ((kc == null &&
                        (kc = comparableClassFor(k)) == null) ||
                        (dir = compareComparables(kc, k, pk)) == 0) {
                    if (!searched) {
                        TreeNode<K,V> q, ch;
                        searched = true;
                        if (((ch = p.left) != null &&
                                (q = ch.findTreeNode(h, k, kc)) != null) ||
                                ((ch = p.right) != null &&
                                        (q = ch.findTreeNode(h, k, kc)) != null))
                            return q;
                    }
                    dir = tieBreakOrder(k, pk);
                }

                TreeNode<K,V> xp = p;
                if ((p = (dir <= 0) ? p.left : p.right) == null) {
                    TreeNode<K,V> x, f = first;
                    first = x = new TreeNode<K,V>(h, k, v, f, xp);
                    if (f != null)
                        f.prev = x;
                    if (dir <= 0)
                        xp.left = x;
                    else
                        xp.right = x;
                    if (!xp.red)
                        x.red = true;
                    else {
                        lockRoot();
                        try {
                            root = balanceInsertion(root, x);
                        } finally {
                            unlockRoot();
                        }
                    }
                    break;
                }
            }
            assert checkInvariants(root);
            return null;
        }

        /**
         * Removes the given node, that must be present before this
         * call.  This is messier than typical red-black deletion code
         * because we cannot swap the contents of an interior node
         * with a leaf successor that is pinned by "next" pointers
         * that are accessible independently of lock. So instead we
         * swap the tree linkages.
         *
         * @return true if now too small, so should be untreeified
         */
        final boolean removeTreeNode(TreeNode<K,V> p) {
            TreeNode<K,V> next = (TreeNode<K,V>)p.next;
            TreeNode<K,V> pred = p.prev;  // unlink traversal pointers
            TreeNode<K,V> r, rl;
            if (pred == null)
                first = next;
            else
                pred.next = next;
            if (next != null)
                next.prev = pred;
            if (first == null) {
                root = null;
                return true;
            }
            if ((r = root) == null || r.right == null || // too small
                    (rl = r.left) == null || rl.left == null)
                return true;
            lockRoot();
            try {
                TreeNode<K,V> replacement;
                TreeNode<K,V> pl = p.left;
                TreeNode<K,V> pr = p.right;
                if (pl != null && pr != null) {
                    TreeNode<K,V> s = pr, sl;
                    while ((sl = s.left) != null) // find successor
                        s = sl;
                    boolean c = s.red; s.red = p.red; p.red = c; // swap colors
                    TreeNode<K,V> sr = s.right;
                    TreeNode<K,V> pp = p.parent;
                    if (s == pr) { // p was s's direct parent
                        p.parent = s;
                        s.right = p;
                    }
                    else {
                        TreeNode<K,V> sp = s.parent;
                        if ((p.parent = sp) != null) {
                            if (s == sp.left)
                                sp.left = p;
                            else
                                sp.right = p;
                        }
                        if ((s.right = pr) != null)
                            pr.parent = s;
                    }
                    p.left = null;
                    if ((p.right = sr) != null)
                        sr.parent = p;
                    if ((s.left = pl) != null)
                        pl.parent = s;
                    if ((s.parent = pp) == null)
                        r = s;
                    else if (p == pp.left)
                        pp.left = s;
                    else
                        pp.right = s;
                    if (sr != null)
                        replacement = sr;
                    else
                        replacement = p;
                }
                else if (pl != null)
                    replacement = pl;
                else if (pr != null)
                    replacement = pr;
                else
                    replacement = p;
                if (replacement != p) {
                    TreeNode<K,V> pp = replacement.parent = p.parent;
                    if (pp == null)
                        r = replacement;
                    else if (p == pp.left)
                        pp.left = replacement;
                    else
                        pp.right = replacement;
                    p.left = p.right = p.parent = null;
                }

                root = (p.red) ? r : balanceDeletion(r, replacement);

                if (p == replacement) {  // detach pointers
                    TreeNode<K,V> pp;
                    if ((pp = p.parent) != null) {
                        if (p == pp.left)
                            pp.left = null;
                        else if (p == pp.right)
                            pp.right = null;
                        p.parent = null;
                    }
                }
            } finally {
                unlockRoot();
            }
            assert checkInvariants(root);
            return false;
        }

        /* ------------------------------------------------------------ */
        // Red-black tree methods, all adapted from CLR

        static <K,V> TreeNode<K,V> rotateLeft(TreeNode<K,V> root,
                                              TreeNode<K,V> p) {
            TreeNode<K,V> r, pp, rl;
            if (p != null && (r = p.right) != null) {
                if ((rl = p.right = r.left) != null)
                    rl.parent = p;
                if ((pp = r.parent = p.parent) == null)
                    (root = r).red = false;
                else if (pp.left == p)
                    pp.left = r;
                else
                    pp.right = r;
                r.left = p;
                p.parent = r;
            }
            return root;
        }

        static <K,V> TreeNode<K,V> rotateRight(TreeNode<K,V> root,
                                               TreeNode<K,V> p) {
            TreeNode<K,V> l, pp, lr;
            if (p != null && (l = p.left) != null) {
                if ((lr = p.left = l.right) != null)
                    lr.parent = p;
                if ((pp = l.parent = p.parent) == null)
                    (root = l).red = false;
                else if (pp.right == p)
                    pp.right = l;
                else
                    pp.left = l;
                l.right = p;
                p.parent = l;
            }
            return root;
        }

        static <K,V> TreeNode<K,V> balanceInsertion(TreeNode<K,V> root,
                                                    TreeNode<K,V> x) {
            x.red = true;
            for (TreeNode<K,V> xp, xpp, xppl, xppr;;) {
                if ((xp = x.parent) == null) {
                    x.red = false;
                    return x;
                }
                else if (!xp.red || (xpp = xp.parent) == null)
                    return root;
                if (xp == (xppl = xpp.left)) {
                    if ((xppr = xpp.right) != null && xppr.red) {
                        xppr.red = false;
                        xp.red = false;
                        xpp.red = true;
                        x = xpp;
                    }
                    else {
                        if (x == xp.right) {
                            root = rotateLeft(root, x = xp);
                            xpp = (xp = x.parent) == null ? null : xp.parent;
                        }
                        if (xp != null) {
                            xp.red = false;
                            if (xpp != null) {
                                xpp.red = true;
                                root = rotateRight(root, xpp);
                            }
                        }
                    }
                }
                else {
                    if (xppl != null && xppl.red) {
                        xppl.red = false;
                        xp.red = false;
                        xpp.red = true;
                        x = xpp;
                    }
                    else {
                        if (x == xp.left) {
                            root = rotateRight(root, x = xp);
                            xpp = (xp = x.parent) == null ? null : xp.parent;
                        }
                        if (xp != null) {
                            xp.red = false;
                            if (xpp != null) {
                                xpp.red = true;
                                root = rotateLeft(root, xpp);
                            }
                        }
                    }
                }
            }
        }

        static <K,V> TreeNode<K,V> balanceDeletion(TreeNode<K,V> root,
                                                   TreeNode<K,V> x) {
            for (TreeNode<K,V> xp, xpl, xpr;;)  {
                if (x == null || x == root)
                    return root;
                else if ((xp = x.parent) == null) {
                    x.red = false;
                    return x;
                }
                else if (x.red) {
                    x.red = false;
                    return root;
                }
                else if ((xpl = xp.left) == x) {
                    if ((xpr = xp.right) != null && xpr.red) {
                        xpr.red = false;
                        xp.red = true;
                        root = rotateLeft(root, xp);
                        xpr = (xp = x.parent) == null ? null : xp.right;
                    }
                    if (xpr == null)
                        x = xp;
                    else {
                        TreeNode<K,V> sl = xpr.left, sr = xpr.right;
                        if ((sr == null || !sr.red) &&
                                (sl == null || !sl.red)) {
                            xpr.red = true;
                            x = xp;
                        }
                        else {
                            if (sr == null || !sr.red) {
                                if (sl != null)
                                    sl.red = false;
                                xpr.red = true;
                                root = rotateRight(root, xpr);
                                xpr = (xp = x.parent) == null ?
                                        null : xp.right;
                            }
                            if (xpr != null) {
                                xpr.red = (xp == null) ? false : xp.red;
                                if ((sr = xpr.right) != null)
                                    sr.red = false;
                            }
                            if (xp != null) {
                                xp.red = false;
                                root = rotateLeft(root, xp);
                            }
                            x = root;
                        }
                    }
                }
                else { // symmetric
                    if (xpl != null && xpl.red) {
                        xpl.red = false;
                        xp.red = true;
                        root = rotateRight(root, xp);
                        xpl = (xp = x.parent) == null ? null : xp.left;
                    }
                    if (xpl == null)
                        x = xp;
                    else {
                        TreeNode<K,V> sl = xpl.left, sr = xpl.right;
                        if ((sl == null || !sl.red) &&
                                (sr == null || !sr.red)) {
                            xpl.red = true;
                            x = xp;
                        }
                        else {
                            if (sl == null || !sl.red) {
                                if (sr != null)
                                    sr.red = false;
                                xpl.red = true;
                                root = rotateLeft(root, xpl);
                                xpl = (xp = x.parent) == null ?
                                        null : xp.left;
                            }
                            if (xpl != null) {
                                xpl.red = (xp == null) ? false : xp.red;
                                if ((sl = xpl.left) != null)
                                    sl.red = false;
                            }
                            if (xp != null) {
                                xp.red = false;
                                root = rotateRight(root, xp);
                            }
                            x = root;
                        }
                    }
                }
            }
        }

        /**
         * Recursive invariant check
         */
        static <K,V> boolean checkInvariants(TreeNode<K,V> t) {
            TreeNode<K,V> tp = t.parent, tl = t.left, tr = t.right,
                    tb = t.prev, tn = (TreeNode<K,V>)t.next;
            if (tb != null && tb.next != t)
                return false;
            if (tn != null && tn.prev != t)
                return false;
            if (tp != null && t != tp.left && t != tp.right)
                return false;
            if (tl != null && (tl.parent != t || tl.hash > t.hash))
                return false;
            if (tr != null && (tr.parent != t || tr.hash < t.hash))
                return false;
            if (t.red && tl != null && tl.red && tr != null && tr.red)
                return false;
            if (tl != null && !checkInvariants(tl))
                return false;
            if (tr != null && !checkInvariants(tr))
                return false;
            return true;
        }

        private static final sun.misc.Unsafe U;
        private static final long LOCKSTATE;
        static {
            try {
                U = sun.misc.Unsafe.getUnsafe();
                Class<?> k = TreeBin.class;
                LOCKSTATE = U.objectFieldOffset
                        (k.getDeclaredField("lockState"));
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }
    
    /**
     * 当进行transfer操作的时候，放到bins的head处的Node。
     * hash值为特殊值MOVE（-1），当进行各种操作遇到这个节点的时候，会进行特殊的操作。
     * 
     * @param <K>
     * @param <V>
     */
    static final class ForwardingNode<K,V> extends Node<K,V> {
        // 可以看到，ForwardingNode节点不存储k和v，只存储下一个正在搬移中的table
        final Node<K,V>[] nextTable;
        
        // 初始化的时候，需要传入一个tab，这个tab就是进行resizing的时候使用的新的table
        ForwardingNode(Node<K,V>[] tab) {
            super(MOVED, null, null, null);
            this.nextTable = tab;
        }

        // 重写find函数，因为当进行ForwardingNode的查找的时候，需要在新的table上查找
        Node<K,V> find(int h, Object k) {
            // 使用的跳转，避免再一次调用find函数造成的递归操作。
            // 递归操作会造成栈资源的大量消耗，有可能会造成堆栈溢出
            // 另外也说明，当在新的table上进行遍历时，有可能会遇到一个新的table
            // 那就说明一次resizing还没有完成，又开启了另外一次新的resizing，这个是什么鬼。。。
            // loop to avoid arbitrarily deep recursion on forwarding nodes
            outer: for (Node<K,V>[] tab = nextTable;;) {
                Node<K,V> e; int n;
                if (k == null || tab == null || (n = tab.length) == 0 ||
                        (e = tabAt(tab, (n - 1) & h)) == null)
                    return null;
                for (;;) {
                    int eh; K ek;
                    if ((eh = e.hash) == h &&
                            ((ek = e.key) == k || (ek != null && k.equals(ek))))
                        return e;
                    if (eh < 0) {
                        // 如果不是为了避免循环递归调用，完全不需要这种跳转。
                        // 这种跳转极容易出错。
                        if (e instanceof ForwardingNode) {
                            tab = ((ForwardingNode<K,V>)e).nextTable;
                            continue outer;
                        }
                        else
                            // 如果不是ForwardingNode，那么就调用find函数。
                            // 正如刚才说的，这里其实可以与ForwardingNode走统一流程，执行find函数
                            return e.find(h, k);
                    }
                    
                    // 如果遍历完也没有找到，那么就返回null
                    if ((e = e.next) == null)
                        return null;
                }
            }
        }
    }
    
    final V putVal(K key, V value, boolean onlyIfAbsent) {
        // key和value不能为null，这点和HashMap不同
        if (key == null || value == null) throw new NullPointerException();
        
        // 对key对hash值进行了再次hash，使得key的分布更加的均衡
        int hash = spread(key.hashCode());
        
        // 用来检查是否需要转化为TreeNode
        int binCount = 0;
        
        // 这里的for循环用的比较巧妙，避免了多次重复判断
        for (Node<K,V>[] tab = table;;) {
            Node<K,V> f; int n, i, fh;
            
            // 这里说明同样是懒加载
            // 比如第一次进来的时候，如果tab没有初始化， 那么执行初始化操作。
            // 由于这个分支没有进行break，所以循环不会退出
            // 重新进入循环后，tab已经初始化了，这时可以进行剩下的操作了
            // 如读取tab上对应槽位的值，判断是否为null
            if (tab == null || (n = tab.length) == 0)
                // 这里需要考虑initTable是如何防止并发问题的
                tab = initTable();
            else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
                // 如果对应的槽位为null，就是用cas操作直接赋值
                // 如果cas操作失败，进行下一次判断，这时f已经有值了
                if (casTabAt(tab, i, null,
                             new Node<K,V>(hash, key, value, null)))
                    break;                   // no lock when adding to empty bin
            }
            
            // 表示取到的f正在做迁移，需要特殊处理。
            else if ((fh = f.hash) == MOVED)
                // 这里要研究transfer是作什么的，MOVED的状态表示什么，还有其他的含义么
                tab = helpTransfer(tab, f);
            else {
                V oldVal = null;
                // 这里开始，f为一个正常的值，那么从这个f开始遍历，找到对应的key，然后赋值
                // 可以看到，这里的锁的粒度是f，而f是这个slot对应的头节点，也就是锁住了整个槽位
                synchronized (f) {
                    // 这里再次进行判断，确保tab的i位置的头节点，仍然为f
                    // 是一种双重锁校验的保证
                    if (tabAt(tab, i) == f) {
                        
                        // 查看TreeBin定义的时候需要看一下，是否所有的TreeBin的hash值都为负数
                        if (fh >= 0) {
                            binCount = 1;
                            
                            // 遍历整个slot，直到最后或者找到或者到末尾
                            for (Node<K,V> e = f;; ++binCount) {
                                K ek;
                                
                                // 发现key已经存在，就进行相关的替换操作
                                if (e.hash == hash &&
                                    ((ek = e.key) == key ||
                                     (ek != null && key.equals(ek)))) {
                                    oldVal = e.val;
                                    if (!onlyIfAbsent)
                                        e.val = value;
                                    break;
                                }
                                
                                // 否则继续向后遍历
                                // 这里使用的是尾插法，可以保证以第一个node为锁的有效性
                                // 也可以使得遍历map时，顺序得到基本的保证
                                Node<K,V> pred = e;
                                if ((e = e.next) == null) {
                                    pred.next = new Node<K,V>(hash, key,
                                                              value, null);
                                    break;
                                }
                            }
                        }
                        
                        // 如果f是TreeBin，那么直接将新的节点放入f这个Tree中，
                        else if (f instanceof TreeBin) {
                            Node<K,V> p;
                            binCount = 2;
                            if ((p = ((TreeBin<K,V>)f).putTreeVal(hash, key,
                                                           value)) != null) {
                                oldVal = p.val;
                                if (!onlyIfAbsent)
                                    p.val = value;
                            }
                        }
                    }
                }
                
                // 检查是否需要tree化，不需要再锁住整个slot了，这时县释放锁，然后检查是否需要tree化
                if (binCount != 0) {
                    if (binCount >= TREEIFY_THRESHOLD)
                        // 这里进行tree化的时候应该会有对并发的处理，需要进一步研究
                        treeifyBin(tab, i);
                    if (oldVal != null)
                        return oldVal;
                    break;
                }
            }
        }
        
        // 对map的元素个数进行增加时，也需要考虑到并发的处理，详细流程需要进一步研究
        addCount(1L, binCount);
        return null;
    }

    private final Node<K,V>[] initTable() {
        Node<K,V>[] tab; int sc;
        // 检查是否可以进行初始化
        while ((tab = table) == null || tab.length == 0) {
            // 如果sizeCtl为负数，那么说明其他的进程正在初始化，
            // yield触发一次cpu调度，重新竞争
            // 如果当前线程重新执行，如果table没有被赋值，那么说明还在初始化，继续yield
            // 如果table已经完成，那么进行下一步操作。这里通过while循环实现了block的效果
            if ((sc = sizeCtl) < 0)
                Thread.yield(); // lost initialization race; just spin
            
            // 使用cas，修改sizeCtl的值为-1。
            // 实际效果为乐观锁
            else if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
                try {
                    // 同样进行一次双重锁校验，判断table是否仍然没有初始化
                    if ((tab = table) == null || tab.length == 0) {
                        int n = (sc > 0) ? sc : DEFAULT_CAPACITY;
                        @SuppressWarnings("unchecked")
                        Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n];
                        table = tab = nt;
                        sc = n - (n >>> 2);
                    }
                } finally {
                    // 如果双重锁校验没有通过，那么将sizeCtl设为原值，相当于释放锁操作
                    // 如果双重锁校验通过了，那么将sizeCtl设置为当前数组大小的75%，用于控制下次扩容
                    sizeCtl = sc;
                }
                break;
            }
        }
        return tab;
    }

    final Node<K,V>[] helpTransfer(Node<K,V>[] tab, Node<K,V> f) {
        Node<K,V>[] nextTab; int sc;
        if (tab != null && (f instanceof ForwardingNode) &&
                (nextTab = ((ForwardingNode<K,V>)f).nextTable) != null) {
            int rs = resizeStamp(tab.length);
            while (nextTab == nextTable && table == tab &&
                    (sc = sizeCtl) < 0) {
                if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 ||
                        sc == rs + MAX_RESIZERS || transferIndex <= 0)
                    break;
                if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1)) {
                    transfer(tab, nextTab);
                    break;
                }
            }
            return nextTab;
        }
        return table;
    }
}
```

TreeBind的代码太长了，这里不对它进行逐行分析。
其对外提供到接口主要是用于查找到find函数和用于插入值到putTreeVal函数
```java
class TreeBin<K,V> extends Node<K,V> {
    // values for lockState
    static final int WRITER = 1; // set while holding write lock
    static final int WAITER = 2; // set when waiting for write lock
    static final int READER = 4; // increment value for setting read lock

    /**
     * Acquires write lock for tree restructuring.
     */
    private final void lockRoot() {
        // 抢占写锁，如果没有抢到，那么就阻塞
        if (!U.compareAndSwapInt(this, LOCKSTATE, 0, WRITER))
            contendedLock(); // offload to separate method
    }

    /**
     * Releases write lock for tree restructuring.
     */
    private final void unlockRoot() {
        lockState = 0;
    }

    /**
     * Possibly blocks awaiting root lock.
     */
    private final void contendedLock() {
        boolean waiting = false;
        for (int s;;) {
            // 如果当前没有读锁，也没有写锁，说明这时候，本线程可以去抢占写锁了
            if (((s = lockState) & ~WAITER) == 0) {
                if (U.compareAndSwapInt(this, LOCKSTATE, s, WRITER)) {
                    // 如果这时候还在waiting的状态，那么就将waiting设置为null
                    if (waiting)
                        waiter = null;
                    return;
                }
            }
            
            // 走到这里，说明程序还没有获取到写锁。
            // 如果当前线程还没有加入到等待状态，那么就尝试进入等待状态，并将waiter设置为当前的线程
            // waiting的状态设置为true
            else if ((s & WAITER) == 0) {
                if (U.compareAndSwapInt(this, LOCKSTATE, s, s | WAITER)) {
                    waiting = true;
                    waiter = Thread.currentThread();
                }
            }
            
            // 走到这里，说明没有获取到写锁，且已经是等待状态了
            else if (waiting)
                // 如果没有获取到写锁，而是获取到了等待锁，那么调用park让当前线程进入到waiting状态
                // 不再占用cpu资源。相当于进入了阻塞态。
                LockSupport.park(this);
        }
    }

    /**
     * 这里可以看到，遍历整个note又两套方案，
     * 方案1： 当有写锁，或者写等待当时候，按照链表当顺序遍历
     * 方案2： 当没有写锁当时候，尝试加读锁，然后按照二叉树遍历
     * @param h
     * @param k
     * @return
     */
    final Node<K,V> find(int h, Object k) {
        if (k != null) {
            for (Node<K,V> e = first; e != null; ) {
                int s; K ek;
                // 如果lock状态是写锁状态或者是写等待状态，那么按照链表当方式进行遍历查找
                if (((s = lockState) & (WAITER|WRITER)) != 0) {
                    if (e.hash == h &&
                            ((ek = e.key) == k || (ek != null && k.equals(ek))))
                        return e;
                    e = e.next;
                }
                
                // 如果既不是写状态，也不是写等待状态，那么就尝试给读锁增加1
                else if (U.compareAndSwapInt(this, LOCKSTATE, s,
                        s + READER)) {
                    TreeNode<K,V> r, p;
                    try {
                        // 这里说明读锁已经加成功了，那么按照二叉树来进行搜索
                        p = ((r = root) == null ? null :
                                r.findTreeNode(h, k, null));
                    } finally {
                        Thread w;
                        
                        // 不论是否找到，都需要将读锁减1。然后调用unpark触发等待的线程去尝试抢占一次写锁
                        if (U.getAndAddInt(this, LOCKSTATE, -READER) ==
                                (READER|WAITER) && (w = waiter) != null)
                            LockSupport.unpark(w);
                    }
                    return p;
                }
            }
        }
        return null;
    }

    /**
     * Finds or adds a node.
     * 这里的信息比较关键，这个函数的作用是找到，或者添加一个node。如果找到就返回找到的节点
     * 如果添加，那么就返回null
     * @return null if added
     */
    final TreeNode<K,V> putTreeVal(int h, K k, V v) {
        Class<?> kc = null;
        boolean searched = false;
        for (TreeNode<K,V> p = root;;) {
            int dir, ph; K pk;
            
            // 如果这里的p为null，由于循环中赋值p的同时，进行了null值判断，所以不可能为后面的重新赋值后的p
            // 那么就表明root为null，这里会执行插入操作。由于是插入操作，所以返回null
            if (p == null) {
                first = root = new TreeNode<K,V>(h, k, v, null, null);
                break;
            }
            
            // 若干p节点的hash值大于输入的hash值，那么说明要插入的节点应该在该节点的左边
            else if ((ph = p.hash) > h)
                dir = -1;

            // 若干p节点的hash值小于输入的hash值，那么说明要插入的节点应该在该节点的右边
            else if (ph < h)
                dir = 1;
            
            // 如果ph和输入的h相等，那么判断key是否相等，如果相等， 直接返回这个节点
            // 那么更新操作什么时候进行呢？？？
            else if ((pk = p.key) == k || (pk != null && k.equals(pk)))
                return p;
            
            // 这里表示，hash值相等，但是key值不等，
            // 如果key都是可以比较类型，那么就比较key值，来确定dir的方向。
            // 如果走到这里，说明比较key再次相同 （不相等，但相同）
            else if ((kc == null &&
                    (kc = comparableClassFor(k)) == null) ||
                    (dir = compareComparables(kc, k, pk)) == 0) {
                // 如果没有查找过，那么分别以左节点和右节点为起点进行搜索。
                // 这里searched标识表示是否搜索过，因为搜索过一次后，如果没有找到，那么再次搜索也没有意义。
                if (!searched) {
                    TreeNode<K,V> q, ch;
                    searched = true;
                    if (((ch = p.left) != null &&
                            (q = ch.findTreeNode(h, k, kc)) != null) ||
                            ((ch = p.right) != null &&
                                    (q = ch.findTreeNode(h, k, kc)) != null))
                        return q;
                }
                // 这时应该
                dir = tieBreakOrder(k, pk);
            }

            // 走到这里，只能是插入，都需要从左节点或者右节点进行查找
            // 由于下面要修改p值了，所以这保存一下p节点。这样好确定新的插入的节点的顺序
            TreeNode<K,V> xp = p;
            
            // 如果下一个要查找的节点为null，说明该插入了
            // 如果不为null，那就继续走起
            if ((p = (dir <= 0) ? p.left : p.right) == null) {
                TreeNode<K,V> x, f = first;
                // 新节点替换头节点，头节点
                first = x = new TreeNode<K,V>(h, k, v, f, xp);
                if (f != null)
                    f.prev = x;
                if (dir <= 0)
                    xp.left = x;
                else
                    xp.right = x;
                if (!xp.red)
                    x.red = true;
                else {
                    // 插入节点后，需要重新调整二叉树了
                    lockRoot();
                    try {
                        root = balanceInsertion(root, x);
                    } finally {
                        unlockRoot();
                    }
                }
                break;
            }
        }
        assert checkInvariants(root);
        return null;
    }
}

```