package kr.dataportal.distributedlock.domain.article.repository

import kr.dataportal.distributedlock.domain.article.entity.Article
import org.springframework.stereotype.Repository
import java.util.concurrent.atomic.AtomicLong

/**
 * @Author Heli
 */
@Repository
class ArticleRepository {

    private val articles = hashMapOf<Long, Article>()
    private val pk: AtomicLong = AtomicLong(1)

    fun save(article: Article): Article {
        return article.apply {
            this.id = pk.getAndAdd(1L)
            articles[this.requiredId] = this
        }
    }

    fun findByIdOrNull(id: Long): Article? {
        return articles[id]
    }
}
