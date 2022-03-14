package com.bethibande.jsql.cache;

import com.bethibande.jsql.SQLObject;

import java.util.function.Consumer;

public class CacheConfig<T extends SQLObject> {

    private int size;
    private Long timeout;

    private Consumer<T> removeItemHook;

    public CacheConfig() {
        this.size = 10;
        this.timeout = 10000L;
    }

    public int getSize() {
        return size;
    }

    public Long getTimeout() {
        return timeout;
    }

    public Consumer<T> getRemoveItemHook() {
        return removeItemHook;
    }

    public CacheConfig<T> size(int newSize) {
        this.size = newSize;
        return this;
    }

    public CacheConfig<T> timeout(Long newTimeout) {
        this.timeout = newTimeout;
        return this;
    }

    public CacheConfig<T> onCacheItemRemove(Consumer<T> listener) {
        this.removeItemHook = listener;
        return this;
    }

}
