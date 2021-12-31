package kr.dataportal.distributedlock.infrastructure

import io.mockk.Called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import kr.dataportal.distributedlock.infrastructure.lock.DistributedLock
import kr.dataportal.distributedlock.infrastructure.lock.DistributedLockAspect
import kr.dataportal.distributedlock.infrastructure.lock.DistributedSynchronizer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory
import org.springframework.stereotype.Component
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import strikt.assertions.startsWith

@ExtendWith(MockKExtension::class)
internal class DistributedLockAspectTest {

    @MockK
    private lateinit var distributedSynchronizer: DistributedSynchronizer

    private lateinit var proxy: Target

    @BeforeEach
    fun beforeEach() {
        val synchronizedBlock = slot<() -> Any?>()
        every {
            distributedSynchronizer.synchronize(any(), capture(synchronizedBlock))
        } answers { synchronizedBlock.captured() }

        val pojo = Target()
        val factory = AspectJProxyFactory(pojo)
        factory.addAspect(DistributedLockAspect(distributedSynchronizer))
        proxy = factory.getProxy()
    }

    @Test
    fun `입력받은 파라미터로 LockKey 를 만들어서 동기화 시킨다`() {

        val result = proxy.withLock(320, "dataportal.kr_heli.os", ObjectKey(42), "with lock")

        expectThat(result) isEqualTo "with lock"
        verify {
            distributedSynchronizer.synchronize(
                withArg { expectThat(it) isEqualTo "test-lock:320:dataportal.kr_heli.os:42" },
                any()
            )
        }
    }

    @Test
    fun `null 로 키를 만들순 없음`() {

        val exception = assertThrows<IllegalArgumentException> {
            proxy.withLock(320, null, ObjectKey(42), "with lock")
        }

        expectThat(exception)
            .get { message }
            .isNotNull()
            .startsWith("@DistributedLock 의 키가 null 입니다")
    }

    @Test
    fun `DistributedLock 애노테이션이 없으면 동기화 하지 않는다`() {
        val result = proxy.withoutLock(320, "dataportal.kr_heli.os", ObjectKey(42), "without lock")

        expectThat(result) isEqualTo "without lock"
        verify { distributedSynchronizer wasNot Called }
    }

    @Test
    fun `입력받은 separator 로 키를 만든다`() {
        val result = proxy.withLockSeparator(320, "dataportal.kr_heli.os", ObjectKey(42), "with lock")

        expectThat(result) isEqualTo "with lock"
        verify {
            distributedSynchronizer.synchronize(
                withArg { expectThat(it) isEqualTo "test-lock_320_dataportal.kr_heli.os_42" },
                any()
            )
        }
    }


    @Component
    class Target {

        @DistributedLock(
            name = "test-lock",
            key = ["#intKey", "#stringKey", "#objectKey.value"]
        )
        fun withLock(intKey: Int?, stringKey: String?, objectKey: ObjectKey?, message: String): String {
            return message
        }

        @DistributedLock(
            name = "test-lock",
            key = ["#intKey", "#stringKey", "#objectKey.value"],
            separator = "_"
        )
        fun withLockSeparator(intKey: Int?, stringKey: String?, objectKey: ObjectKey?, message: String): String {
            return message
        }

        fun withoutLock(intKey: Int?, stringKey: String?, objectKey: ObjectKey?, message: String): String {
            return message
        }
    }

    class ObjectKey(
        val value: Int
    )
}
