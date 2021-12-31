package kr.dataportal.distributedlock.domain.article.entity

import kr.dataportal.distributedlock.utils.lateInit
import kr.dataportal.distributedlock.utils.notNull

/**
 * @Author Heli
 */
class Article private constructor(
    var title: String,
) {
    var id: Long? = lateInit()

    val requiredId: Long get() = id.notNull { "id must not be null" }

    fun update(title: String) = this.apply {
        this.title = title
    }

    companion object {

        fun of(title: String): Article = Article(
            title = title
        )
    }
}
