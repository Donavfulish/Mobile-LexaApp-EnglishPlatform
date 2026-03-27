package com.home.lexa.di
import android.util.LruCache

object AppMemoryCache {
    @PublishedApi
    internal val cache = LruCache<String, Any>(500)

    fun <T : Any> put(key: String, value: T) {
        cache.put(key, value)
    }

    inline fun <reified T : Any> get(key: String): T? {
        val value = cache.get(key)
        return value as? T
    }

    fun remove(key: String) {
        cache.remove(key)
    }

    fun clearAll() {
        cache.evictAll()
    }
}
