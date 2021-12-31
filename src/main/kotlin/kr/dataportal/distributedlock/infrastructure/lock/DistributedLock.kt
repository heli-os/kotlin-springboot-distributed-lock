package kr.dataportal.distributedlock.infrastructure.lock

/**
 * @Author Heli
 */
@Target(AnnotationTarget.FUNCTION)
annotation class DistributedLock(

    val name: String,

    val key: Array<String>,

    val separator: String = ":"
)
