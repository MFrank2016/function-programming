package com.mfrank.functionprogram.util;

import com.mfrank.functionprogram.base.Function;
import com.mfrank.functionprogram.base.TailCall;

import java.util.*;

import static com.mfrank.functionprogram.base.TailCall.ret;
import static com.mfrank.functionprogram.base.TailCall.sus;

public class CollectionUtility {

    public static <T, U> U fold(List<T> ts, U identity, Function<U, Function<T, U>> f) {
        U result = identity;
        for (T t : ts) {
            result = f.apply(result).apply(t);
        }
        return result;
    }

    public static <T, U> U foldLeft(List<T> ts, U identity, Function<U, Function<T, U>> f) {
        return foldLeft_(ts, identity, f).eval();
    }

    public static <T, U> TailCall<U> foldLeft_(List<T> ts, U identity, Function<U, Function<T, U>> f) {
        return ts.isEmpty()
                ? ret(identity)
                : sus(() -> foldLeft_(tail(ts), f.apply(identity).apply(head(ts)), f));
    }

    public static <T, U> U foldRight(List<T> ts, U identity, Function<T, Function<U, U>> f) {
        return foldRight_(identity, reverse(ts), f).eval();
    }

    private static <T, U> TailCall<U> foldRight_(U acc, List<T> ts, Function<T, Function<U, U>> f) {
        return ts.isEmpty()
                ? ret(acc)
                : sus(() -> foldRight_(f.apply(head(ts)).apply(acc), tail(ts), f));
    }

    public static <T, U> List<U> map(List<T> list, Function<T, U> f) {
        List<U> newList = new ArrayList<>();
        for (T value : list) {
            newList.add(f.apply(value));
        }
        return newList;
    }

    public static List<Integer> range(Integer start, Integer end) {
        return range_(list(), start, end).eval();
    }

    public static TailCall<List<Integer>> range_(List<Integer> acc, Integer start, Integer end) {
        return end <= start
                ? ret(acc)
                : sus(() -> range_(append(acc, start), start + 1, end));
    }

    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    // 获取列表的第一个元素
    public static <T> T head(List<T> list) {
        if (isEmpty(list)) {
            throw new IllegalStateException("head of empty list");
        }
        return list.get(0);
    }

    // 复制列表
    private static <T> List<T> copy(List<T> ts) {
        return new ArrayList<>(ts);
    }

    // 删除第一个元素后的列表
    public static <T> List<T> tail(List<T> list) {
        if (list.size() == 0) {
            throw new IllegalStateException("tail of empty list");
        }
        List<T> workList = copy(list);
        workList.remove(0);
        return Collections.unmodifiableList(workList);
    }

    public static <T> List<T> list() {
        return Collections.emptyList();
    }

    public static <T> List<T> list(T t) {
        return Collections.singletonList(t);
    }

    public static <T> List<T> list(List<T> ts) {
        return Collections.unmodifiableList(new ArrayList<>(ts));
    }

    public static <T> List<T> list(T... t) {
        return Collections.unmodifiableList(Arrays.asList(Arrays.copyOf(t, t.length)));
    }

    public static <T> List<T> append(List<T> list, T t) {
        List<T> ts = copy(list);
        ts.add(t);
        return Collections.unmodifiableList(ts);
    }

    public static <T> List<T> prepend(T t, List<T> list) {
        return foldLeft(list, list(t), a -> b -> append(a, b));
    }

    public static <T> List<T> reverse(List<T> list) {
        return foldLeft(list, list(), x -> y -> foldLeft(x, list(y), a -> b -> append(a, b)));
    }

}
