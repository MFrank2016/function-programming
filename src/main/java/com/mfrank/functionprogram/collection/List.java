package com.mfrank.functionprogram.collection;

import com.mfrank.functionprogram.base.Function;
import com.mfrank.functionprogram.base.TailCall;

import static com.mfrank.functionprogram.base.TailCall.*;

public abstract class List<A> {

    /**
     * 获取第一个元素
     *
     * @return 返回第一个元素
     */
    public abstract A head();

    /**
     * 获取除第一个元素之外的其它元素组成的列表
     *
     * @return 返回移除第一个元素后的列表
     */
    public abstract List<A> tail();

    /**
     * 列表是否为空
     *
     * @return 如果列表为空，则返回true，否则返回false
     */
    public abstract boolean isEmpty();

    /**
     * 添加一个元素到列表头部
     *
     * @param a 待添加的元素
     * @return 返回添加元素后的列表
     */
    public abstract List<A> cons(A a);

    /**
     * 替换列表的头元素
     *
     * @param list 待替换的列表
     * @param h    待替换的头元素
     * @return 返回替换了头元素的新列表
     */
    public abstract List<A> setHead(List<A> list, A h);

    /**
     * 打印列表
     *
     * @return 打印列表中的元素
     */
    public abstract String toString();

    /**
     * 删除前n个元素
     *
     * @param n 需要删除的元素个数
     * @return 返回删除前n个元素后的列表
     */
    public abstract List<A> drop(int n);

    /**
     * 只要条件为真，则删除List的head元素
     * @param f 用于判断的函数
     * @return 返回处理过后的列表
     */
    public abstract List<A> dropWhile(Function<A, Boolean> f);

    /**
     * 反转列表
     * @return 返回反转后的列表
     */
    public abstract List<A> reverse();

    /**
     * 删除最后一个元素
     * @return 返回删除最后一个元素的列表
     */
    public abstract List<A> init();

    /**
     * 左折叠
     * @param identity 初始值
     * @param f 折叠函数
     * @return 返回左折叠后的结果
     */
    public abstract <B> B foldLeft(B identity, Function<B, Function<A, B>> f);

    /**
     * 左折叠
     * @param identity 初始值
     * @param f 折叠函数
     * @return 返回左折叠后的结果
     */
    public abstract <B> B foldRight(B identity, Function<A, Function<B, B>> f);

    /**
     * 对列表中的每一个元素应用函数，使得将列表中的A类型元素映射为B类型元素
     * @param f 映射函数
     * @return 返回映射后的列表
     */
    public <B> List<B> map(Function<A, B> f){
        return foldRight(list(), h -> t -> new Cons<>(f.apply(h), t));
    }

    /**
     * 过滤，对列表中的每一个元素应用函数f，取出结果为true的元素
     * @param f 过滤函数
     * @return 返回过滤后的元素列表
     */
    public List<A> filter(Function<A, Boolean> f){
        return foldRight(list(), h -> t -> f.apply(h) ? new Cons<>(h, t) : t);
    }

    @SuppressWarnings("rawtypes")
    public static final List NIL = new Nil();

    private List() {
    }

    private static class Nil<A> extends List<A> {

        private Nil() {
        }

        @Override
        public A head() {
            throw new IllegalStateException("head called on empty list");
        }

        @Override
        public List<A> tail() {
            throw new IllegalStateException("tail called on empty list");
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public List<A> cons(A a) {
            return new Cons<>(a, this);
        }

        @Override
        public List<A> setHead(List<A> list, A h) {
            throw new IllegalStateException("setHead called on empty list");
        }

        @Override
        public String toString() {
            return "[NIL]";
        }

        @Override
        public List<A> drop(int n) {
            return this;
        }

        @Override
        public List<A> dropWhile(Function<A, Boolean> f) {
            return this;
        }

        @Override
        public List<A> reverse() {
            return this;
        }

        @Override
        public List<A> init() {
            throw new IllegalStateException("init called on empty list");
        }

        @Override
        public <B> B foldLeft(B identity, Function<B, Function<A, B>> f) {
            return identity;
        }

        @Override
        public <B> B foldRight(B identity, Function<A, Function<B, B>> f) {
            return identity;
        }
    }

    private static class Cons<A> extends List<A> {

        private final A head;
        private final List<A> tail;

        private Cons(A head, List<A> tail) {
            this.head = head;
            this.tail = tail;
        }

        @Override
        public A head() {
            return head;
        }

        @Override
        public List<A> tail() {
            return tail;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public List<A> cons(A a) {
            return new Cons<>(a, this);
        }

        @Override
        public List<A> setHead(List<A> list, A h) {
            return new Cons<>(h, this.tail);
        }

        @Override
        public String toString() {
            return String.format("[%sNIL]",
                    toString(new StringBuilder(), this).eval());
        }

        @Override
        public List<A> drop(int n) {
            return n <= 0
                    ? this
                    : drop_(this, n).eval();
        }

        @Override
        public List<A> dropWhile(Function<A, Boolean> f) {
            return dropWhile_(this, f).eval();
        }

        @Override
        public List<A> reverse() {
            return reverse_(list(), this).eval();
        }

        @Override
        public List<A> init() {
            return reverse().tail().reverse();
        }

        @Override
        public <B> B foldLeft(B identity, Function<B, Function<A, B>> f) {
            return foldLeft_(identity, this, f).eval();
        }

        @Override
        public <B> B foldRight(B identity, Function<A, Function<B, B>> f) {
            return foldRight_(identity, this, f).eval();
        }

        private <B> TailCall<B> foldRight_(B acc, List<A> aList, Function<A, Function<B, B>> f) {
            return aList.isEmpty()
                    ? ret(acc)
                    : sus(() -> foldRight_(f.apply(aList.head()).apply(acc), aList.tail(), f));
        }

        private <B> TailCall<B> foldLeft_(B acc, List<A> list, Function<B, Function<A, B>> f) {
            return list.isEmpty()
                    ? ret(acc)
                    : sus(() -> foldLeft_(f.apply(acc).apply(list.head()), list.tail(), f));
        }

        private TailCall<List<A>> reverse_(List<A> acc, List<A> list) {
            return list.isEmpty()
                    ? ret(acc)
                    : sus(() -> reverse_(new Cons<>(list.head(), acc), list.tail()));
        }

        private TailCall<List<A>> dropWhile_(List<A> list, Function<A, Boolean> f) {
            return !list.isEmpty() && f.apply(list.head())
                    ? sus(() -> dropWhile_(list.tail(), f))
                    : ret(list);
        }

        private TailCall<List<A>> drop_(List<A> list, int n){
            return n <= 0 || list.isEmpty()
                    ? ret(list)
                    : sus(() -> drop_(list.tail(), n - 1));
        }

        private TailCall<StringBuilder> toString(StringBuilder acc, List<A> list) {
            return list().isEmpty()
                    ? ret(acc)
                    : sus(() -> toString(acc.append(list.head()).append(", "), list.tail()));
        }
    }

    @SuppressWarnings("unchecked")
    public static <A> List<A> list() {
        return NIL;
    }

    @SafeVarargs
    public static <A> List<A> list(A... a) {
        List<A> n = list();
        for (int i = a.length; i >= 0; i--) {
            n = new Cons<>(a[i], n);
        }
        return n;
    }

    public static <A, B> B foldRight(List<A> list, B n, Function<A, Function<B, B>> f){
        return list.reverse().foldLeft(n, x -> y -> f.apply(y).apply(x));
    }

    public static <A> List<A> concat(List<A> list1, List<A> list2){
        return list1.reverse().foldLeft(list2, x -> x::cons);
    }

    public static <A> List<A> flatten(List<List<A>> list){
        return foldRight(list, List.<A>list(), x -> y -> concat(x, y));
    }

}
