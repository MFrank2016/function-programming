package com.mfrank.functionprogram.base;

public interface Result<T> {

    void bind(Effect<T> success, Effect<String> failure);

    static <T> Result<T> failure(String message) {
        return new Failure<>(message);
    }

    static <T> Result<T> success(T value) {
        return new Success<>(value);
    }

    class Success<T> implements Result<T> {

        private final T value;

        private Success(T t) {
            value = t;
        }

        @Override
        public void bind(Effect<T> success, Effect<String> failure) {
            success.apply(value);
        }
    }


    class Failure<T> implements Result<T> {

        private final String errorMessage;

        private Failure(String s) {
            errorMessage = s;
        }

        @Override
        public void bind(Effect<T> success, Effect<String> failure) {
            failure.apply(errorMessage);
        }
    }
}
