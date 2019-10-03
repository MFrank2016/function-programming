package com.mfrank.functionprogram.base;

public class Case<T> extends Tuple<Supplier<Boolean>, Supplier<Result<T>>> {

    private Case(Supplier<Boolean> booleanSupplier, Supplier<Result<T>> resultSupplier) {
        super(booleanSupplier, resultSupplier);
    }

    public static <T> Case<T> mcase(Supplier<Boolean> condition, Supplier<Result<T>> value){
        return new Case<>(condition, value);
    }

    public static <T> DefaultCase<T> mcase(Supplier<Result<T>> value){
        return new DefaultCase<>(() -> true, value);
    }

    private static class DefaultCase<T> extends Case<T>{
        private DefaultCase(Supplier<Boolean> booleanSupplier, Supplier<Result<T>> resultSupplier){
            super(booleanSupplier, resultSupplier);
        }
    }


    public static <T> Result<T> match(DefaultCase<T> defaultCase, Case<T>... machers){
        for (Case<T> macher : machers) {
            if (macher._1.get()) return macher._2.get();
        }
        return defaultCase._2.get();
    }
}
