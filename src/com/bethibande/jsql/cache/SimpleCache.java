package com.bethibande.jsql.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SimpleCache<K, V> {

    private final Deque<CacheItem<K, V>> cache = new ConcurrentLinkedDeque<>();

    private int size;
    private long timeout;

    private boolean noErrors = false;

    private Consumer<V> removeItemHook;

    /**
     * @param size max cache size / max amount of items in cache at once
     * @param timeout timeout time in ms, if last item/value access + timeout < currentTime, then cache.update(); will remove the item from cache
     */
    public SimpleCache(int size, long timeout) {
        this.size = size;
        this.timeout = timeout;
    }

    /**
     * Set the remove item hook, this consumer will be executed whenever an item is being removed from cache
     * @param hook the hook
     */
    public void setRemoveItemHook(Consumer<V> hook) {
        this.removeItemHook = hook;
    }

    /**
     * Get the current amount of items in cache
     * @return current cache size
     */
    public int getSize() {
        return cache.size();
    }

    /**
     * Get the max cache size
     * @return current max size
     */
    public int getMaxSize() {
        return size;
    }

    /**
     * Get the max item timeout
     * @return get current timeout time in ms
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * Update the max cache size
     * @param size new size
     */
    public void setMaxSize(int size) {
        this.size = size;
    }

    /**
     * Update the max item timeout time
     * @param timeout new time in ms
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * Disable overflow warning message
     */
    public void noErrors() {
        this.noErrors = true;
    }

    /**
     * Removes items from head of cache if cache size exceeds max cache size.
     * Prints overflow warning message if head of cache was added within the last 100 ms, meaning all items within the cache were added to the cache withing the last 100 ms.
     * Disable error message by calling cache.noErrors();
     */
    public void makeSpace() {
        if(cache.size() < this.size) return;
        long timeNow = System.currentTimeMillis();
        int overflow = cache.size()-this.size;

        // removing items from head of cache
        for (int i = 0; i < overflow; i++) {
            CacheItem<K, V> it = cache.poll(); // .poll() will get and remove the item from the cache
            if(it == null) return;

            if(this.removeItemHook != null) this.removeItemHook.accept(it.getValue());

            if(!noErrors && it.getTime() + 100 >= timeNow) {
                // if last item added to cache was added within the last 100 ms, print overflow message
                System.err.println("[SQL WARNING] Cache overflow, all items in cache were added to the cache within the last 100 ms, please consider increasing your cache size (" + it.getValue().getClass().getName() + ".class)");
            }
        }
    }

    /**
     * Add a key/value pair to the cache
     * @param key the key of the value use to retrieve the value
     * @param value the value
     */
    public void put(K key, V value) {
        if(key == null) return;
        this.remove(key); // remove old entries if existing to prevent duplicate entries
        cache.offer(new CacheItem<>(key, value));
        makeSpace();
    }

    /**
     * Remove key/value from cache
     * @param key the key of your value
     */
    public void remove(K key) {
        if(key == null) return;

        List<CacheItem<K, V>> remove = new ArrayList<>();
        cache.stream().filter(it -> it.getKey().equals(key)).forEach(it -> {
            if(this.removeItemHook != null) this.removeItemHook.accept(it.getValue());
            remove.add(it);
        });

        remove.forEach(cache::remove);
        remove.clear();
    }

    /**
     * Get the value belonging to the key from cache
     * @param key the key of your value
     * @return the value belonging to the key or null if there is none
     */
    public V get(K key) {
        for (CacheItem<K, V> item : cache) {
            if(item.getKey().equals(key)) {
                cache.remove(item);
                cache.offer(item); // move item to tail of cache
                return item.getValue();
            }
        }
        return null;
    }

    /**
     * @return all values in this cache instance
     */
    public Collection<V> getAll() {
        return this.cache.stream().map(CacheItem::getValue).collect(Collectors.toList());
    }

    /**
     * @param key the key to check
     * @return true if cache contains key
     */
    public boolean hasKey(K key) {
        return cache.stream().anyMatch(it -> it.getKey().equals(key));
    }

    /**
     * Clear the cache, all items will be removed immediately
     */
    public void clear() {
        this.cache.clear();
    }

    /**
     * Remove all values, that have exceeded the timeout time
     */
    public void update() {
        cache.stream().filter(it -> !it.isValid(this.timeout)).forEach(it -> {
            if(this.removeItemHook != null) this.removeItemHook.accept(it.getValue());
            cache.remove(it);
        });
    }

    public static class CacheItem<K, V> {

        private final K key;
        private final V value;
        private Long time;

        public CacheItem(K key, V value) {
            this.key = key;
            this.value = value;
            this.time = System.currentTimeMillis();
        }

        public K getKey() {
            return this.key;
        }

        public V getValue() {
            this.updateTime();
            return this.value;
        }

        public Long getTime() {
            return this.time;
        }

        public void updateTime() {
            this.time = System.currentTimeMillis();
        }

        public boolean isValid(Long timeout) {
            return System.currentTimeMillis() < this.time + timeout;
        }

    }

}
