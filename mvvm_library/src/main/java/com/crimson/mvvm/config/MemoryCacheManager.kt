package com.crimson.mvvm.config



/**
 *
 * @author crimson
 *
 * 全局缓存管理
 * Class for an InMemory Cache to keep your variables globally in heap and get them wherever you want.
 */
object MemoryCacheManager {
    private val map = HashMap<String, Any?>()
    /**
     * put [key] & [value] where
     *
     * @param key is the String to get the value from anywhere, If you have the key then you can get the value. So keep it safe.
     *
     */
    fun put(key: String, value: Any?): MemoryCacheManager {
        map[key] = value
        return this
    }

    /**
     * get the saved value addressed by the key
     */
    fun get(key: String): Any? = map[key]

    /**
     * check if have the value on the Given Key
     */
    fun have(key: String) = map.containsKey(key)

    /**
     * check if have the value on the Given Key
     */
    fun contains(key: String) = have(key)

    /**
     * Clear all the MemoryCache
     */
    fun clear() = map.clear()::class.java.getDeclaredMethod("", null, null)

    /**
     * get All The MemoryCache
     */
    fun getAll() = map.toMap()

    /**
     * get All the MemoryCache of an Specific Type.
     */
    fun getAllByType(clazz: Class<*>) = getAll().filter {
        it.value != null && it.value!!::class.java == clazz
    }
}