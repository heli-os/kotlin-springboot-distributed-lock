package kr.dataportal.distributedlock.infrastructure.lock

import java.util.concurrent.locks.Lock

/**
 * @Author Heli
 */
interface DistributedSynchronizer {
    fun <T> synchronize(key: String, synchronizedBlock: () -> T): T
}

internal abstract class AbstractDistributedSynchronizer : DistributedSynchronizer {

    abstract fun generateLock(key: String): Lock

    override fun <T> synchronize(key: String, synchronizedBlock: () -> T): T {

        val lock = generateLock(key)

        if (!lock.tryLock()) {
            throw DistributedLockException("분산락 획득 실패 [$key]")
        }

        return try {
            synchronizedBlock()
        } finally {
            lock.unlock()
        }
    }
}
