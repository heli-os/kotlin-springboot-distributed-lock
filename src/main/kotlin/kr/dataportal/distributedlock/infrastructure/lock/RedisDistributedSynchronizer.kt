package kr.dataportal.distributedlock.infrastructure.lock

import kr.dataportal.distributedlock.infrastructure.redis.RedisService
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock

/**
 * @Author Heli
 */
@Component
internal class RedisDistributedSynchronizer(
    private val redisService: RedisService
) : AbstractDistributedSynchronizer() {
    override fun generateLock(key: String): Lock {
        return RedisLock(REDIS_LOCK_PREFIX + key)
    }

    inner class RedisLock(private val key: String) : Lock {

        override fun tryLock(): Boolean {
            return redisService.setIfAbsent(key, true, REDIS_LOCK_DURATION)
        }

        override fun unlock() {
            redisService.delete(key)
        }

        override fun tryLock(time: Long, unit: TimeUnit): Boolean {
            throw UnsupportedOperationException()
        }

        override fun lock() {
            throw UnsupportedOperationException()
        }

        override fun lockInterruptibly() {
            throw UnsupportedOperationException()
        }

        override fun newCondition(): Condition {
            throw UnsupportedOperationException()
        }
    }

    companion object {
        /**
         * 혹시나 락이 풀리지 않는 상황을 방지하기 위해 최대 1분만 락을 잡음.
         */
        private val REDIS_LOCK_DURATION: Duration = Duration.ofMinutes(1)
        private const val REDIS_LOCK_PREFIX = "lock:"
    }
}
