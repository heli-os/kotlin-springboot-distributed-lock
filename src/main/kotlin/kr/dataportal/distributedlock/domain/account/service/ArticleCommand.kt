package kr.dataportal.distributedlock.domain.account.service

import kr.dataportal.distributedlock.domain.account.entity.Article
import kr.dataportal.distributedlock.domain.account.repository.ArticleRepository
import kr.dataportal.distributedlock.infrastructure.lock.DistributedLock
import kr.dataportal.distributedlock.utils.notNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * @Author Heli
 */
@Service
class ArticleCommand(
    private val articleRepository: ArticleRepository
) {

    fun create(title: String): Article {

        val article = Article.of(
            title = title
        )

        return articleRepository.save(article)
    }


    @DistributedLock(
        name = ARTICLE_CREATE_LOCK_PREFIX,
        key = [
            "#articleId"
        ]
    )
    fun update(
        articleId: Long,
        title: String
    ): Article {

        val article = articleRepository.findByIdOrNull(articleId).notNull { "Article 조회 실패" }
        article.update(title)

        return articleRepository.save(article)
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ArticleCommand::class.java)
        private const val ARTICLE_CREATE_LOCK_PREFIX = "article-create-lock"
    }
}