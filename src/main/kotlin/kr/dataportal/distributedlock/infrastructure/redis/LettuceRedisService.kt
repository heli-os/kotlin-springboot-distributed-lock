package kr.dataportal.distributedlock.infrastructure.redis

import kr.dataportal.distributedlock.utils.parseJson
import kr.dataportal.distributedlock.utils.toJson
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

/**
 * @Author Heli
 */
@Component
internal class LettuceRedisService(
    private val redisTemplate: StringRedisTemplate
) : RedisService {

    override fun <T> getOrNull(key: String, type: Class<T>): T? {
        return redisTemplate.opsForValue()[key.redisKey()]?.parseJson(type)
    }

    override fun setIfAbsent(key: String, value: Any, duration: Duration): Boolean {
        return requireNotNull(redisTemplate.opsForValue().setIfAbsent(key.redisKey(), value.toJson(), duration))
    }

    override fun delete(key: String): Boolean {
        return redisTemplate.delete(key.redisKey())
    }

    private fun String.redisKey(): String {
        return KEY_PREFIX + this
    }

    companion object {
        private const val KEY_PREFIX = "dataportal.kr_heli.os:"
    }
}
