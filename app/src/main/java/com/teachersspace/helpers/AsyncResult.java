package com.teachersspace.helpers;

public class AsyncResult<T> {
    private T value;
    public AsyncResult(T v) {
        this.set(v);
    }
    public void set(T v) {
        this.value = v;
    }
    public T get() {
        return this.value;
    }
}
