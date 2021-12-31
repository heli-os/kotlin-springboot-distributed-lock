package kr.dataportal.distributedlock.infrastructure.redis

import java.time.Duration

/**
 * @Author Heli
 */
interface RedisService {

    fun <T> getOrNull(key: String, type: Class<T>): T?

    fun setIfAbsent(key: String, value: Any, duration: Duration): Boolean

    fun delete(key: String): Boolean
}
