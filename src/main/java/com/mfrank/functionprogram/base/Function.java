package com.mfrank.functionprogram.base;

import java.util.List;

import static com.mfrank.functionprogram.util.CollectionUtility.*;

public interface Function<T, U> {

    U apply(T arg);

    default <V> Function<V, U> compose(Function<V, T> f) {
        return x -> apply(f.apply(x));
    }

    default <V> Function<T, V> andThen(Function<U, V> f) {
        return x -> f.apply(apply(x));
    }

    static <T> Function<T, T> identity() {
        return t -> t;
    }

    static <T, U, V> Function<V, U> compose(Function<T, U> f, Function<V, T> g) {
        return x -> f.apply(g.apply(x));
    }

    static <T, U, V> Function<T, V> andThen(Function<T, U> f, Function<U, V> g) {
        return x -> g.apply(f.apply(x));
    }

    static <T, U, V> Function<Function<T, U>, Function<Function<U, V>, Function<T, V>>> compose() {
        return x -> y -> y.compose(x);
    }

    static <T, U, V> Function<Function<T, U>, Function<Function<V, T>, Function<V, U>>> andThen() {
        return x -> y -> y.andThen(x);
    }

    static <T, U, V> Function<Function<T, U>, Function<Function<U, V>, Function<T, V>>> higherAndThen() {
        return x -> y -> z -> y.apply(x.apply(z));
    }

    static <T, U, V> Function<Function<U, V>, Function<Function<T, U>, Function<T, V>>> higherCompose() {
        return (Function<U, V> x) -> (Function<T, U> y) -> (T z) -> x.apply(y.apply(z));
    }

    static <T, U, V> Function<U, V> partialT(T t, Function<T, Function<U, V>> f) {
        return f.apply(t);
    }

    static <T, U, V> Function<T, V> partialU(U u, Function<T, Function<U, V>> f) {
        return a -> f.apply(a).apply(u);
    }

    static <T> Function<T, T> composeAllViaFoldLeft(List<Function<T, T>> list){
        return x -> foldLeft(reverse(list), x, a -> b -> b.apply(a));
    }

    static <T> Function<T, T> composeAllViaFoldRight(List<Function<T, T>> list){
        return x -> foldRight(list, x, a -> a::apply);
    }

    static <T> Function<T, T> andThenAllViaFoldLeft(List<Function<T, T>> list){
        return x -> foldLeft(list, x, a -> b -> b.apply(a));
    }

    static <T> Function<T, T> andThenAllViaFoldRight(List<Function<T, T>> list){
        return x -> foldRight(reverse(list), x, a -> a::apply);
    }

}
