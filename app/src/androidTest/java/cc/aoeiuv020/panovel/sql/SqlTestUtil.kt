package cc.aoeiuv020.panovel.sql

import cc.aoeiuv020.panovel.sql.entity.Chapter
import cc.aoeiuv020.panovel.sql.entity.NovelDetail
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.util.*

/**
 * Created by AoEiuV020 on 2018.04.26-10:32:17.
 */
object SqlTestUtil : AnkoLogger {
    val timeWatcher = object : TestWatcher() {
        var start = 0L
        override fun starting(description: Description) {
            info { "Starting test: " + description.methodName }
            start = System.currentTimeMillis()
        }

        override fun finished(description: Description) {
            val end = System.currentTimeMillis()
            info { "Test " + description.methodName + " took " + (end - start) + "ms" }
        }
    }

    private val random = Random()
    private var number: Int = 0

    fun createNovelDetail(): NovelDetail = NovelDetail(
            name = "name-$number",
            author = "author-$number",
            site = "site",
            detailRequesterType = "detail-requester-type-default",
            detailRequesterExtra = "detail-requester-extra-$number",
            chaptersRequesterType = "chapter-requester-type-default",
            chaptersRequesterExtra = "chapter-requester-extra-$number",
            imageUrl = "http://imageUrl-$number",
            introduction = "introduction-$number",
            chapterReadAt = random.nextInt(),
            textReadAt = random.nextInt(),
            chapterNameLast = "chapter-name-last-$number",
            chapterNameReadAt = "chapter-name-read-at-$number",
            updateTime = Date(random.nextLong()),
            checkUpdateTime = Date(random.nextLong()),
            receiveUpdateTime = Date(random.nextLong()),
            remoteId = random.nextInt()
    ).also {
        number++
    }

    fun createChapters(novelDetailId: Long, size: Int) = List(size) { index ->
        Chapter(
                novelDetailId = novelDetailId,
                index = index,
                name = "novel-chapter-name-$index",
                textRequesterType = "text-requester-type-default",
                textRequesterExtra = "text-requester-extra-$index"
        )
    }

}