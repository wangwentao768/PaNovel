package cc.aoeiuv020.panovel.sql.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

/**
 * 永久数据库中的小说表，
 * 包含小说详情的请求者，用于从缓存数据库中的小说详情表中获取详情，没有也能联网获取，
 *
 * Created by AoEiuV020 on 2018.04.26-16:51:52.
 */
@Entity(
        indices = [
            (Index(
                    value = ["detailRequesterType", "detailRequesterExtra"],
                    unique = true
            ))
        ]
)
@Suppress("MemberVisibilityCanBePrivate", "unused")
data class NovelMini(
        /**
         * 普通的id, 在书单里外键用到，
         */
        @PrimaryKey(autoGenerate = true)
        val id: Long? = null,
        /**
         * 详情请求者的类型，
         * [detailRequesterType], [detailRequesterExtra] 两个字段合起来表示详情请求者，能用来请求到小说的一切，
         */
        val detailRequesterType: String,
        /**
         * 详情请求者的参数，
         * [detailRequesterType], [detailRequesterExtra] 两个字段合起来表示详情请求者，能用来请求到小说的一切，
         */
        val detailRequesterExtra: String,
        /**
         * 是否在书架上，
         */
        val bookshelf: Boolean = false,
        /**
         * 阅读进度，
         * 阅读至的章节索引，
         */
        val chapterReadAt: Int = 0,
        /**
         * 章节内的阅读进度，
         * 看到第几页或者第几个字，具体没决定，
         */
        val textReadAt: Int = 0
) {
    companion object {
        fun new(
                type: String,
                extra: String,
                bookshelf: Boolean = false,
                chapterReadAt: Int = 0,
                textReadAt: Int = 0
        ): NovelMini = NovelMini(
                detailRequesterType = type,
                detailRequesterExtra = extra,
                bookshelf = bookshelf,
                chapterReadAt = chapterReadAt,
                textReadAt = textReadAt
        )
    }
}
