package kr.dataportal.distributedlock.infrastructure.lock

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.expression.EvaluationContext
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component

/**
 * @Author Heli
 */
@Component
@Aspect
class DistributedLockAspect(
    private val distributedSynchronizer: DistributedSynchronizer
) {

    @Around("@annotation(distributedLock)")
    fun round(joinPoint: ProceedingJoinPoint, distributedLock: DistributedLock): Any? {
        val lockKey = lockKey(joinPoint, distributedLock)
        return distributedSynchronizer.synchronize(lockKey) {
            joinPoint.proceed()
        }
    }

    private fun lockKey(joinPoint: ProceedingJoinPoint, distributedLock: DistributedLock): String {
        val evaluationContext = createEvaluationContext(joinPoint)
        return distributedLock.key.asSequence()
            .map { EXPRESSION_PARSER.parseExpression(it) }
            .map { requireNotNull(it.getValue(evaluationContext)) { "@DistributedLock 의 키가 null 입니다 name[${distributedLock.name}] keyExpression[${it.expressionString}]" } }
            .joinToString(
                separator = distributedLock.separator,
                prefix = "${distributedLock.name}${distributedLock.separator}"
            )
    }

    private fun createEvaluationContext(joinPoint: ProceedingJoinPoint): EvaluationContext {
        val parameters = joinPoint.parameters
        return StandardEvaluationContext().apply { setVariables(parameters) }
    }

    private val JoinPoint.parameters
        get() = (signature as MethodSignature).parameterNames.asSequence().zip(args.asSequence()).toMap()

    companion object {
        private val EXPRESSION_PARSER: ExpressionParser = SpelExpressionParser()
    }
}
