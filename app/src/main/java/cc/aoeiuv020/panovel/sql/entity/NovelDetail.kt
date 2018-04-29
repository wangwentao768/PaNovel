package cc.aoeiuv020.panovel.sql.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import java.util.*

/**
 * 缓存数据库中的小说详情表，
 * Created by AoEiuV020 on 2018.04.25-20:35:27.
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
data class NovelDetail(
        /**
         * 普通的id,
         */
        @PrimaryKey(autoGenerate = true)
        val id: Long? = null,
        /**
         * 小说名，
         */
        val name: String,
        /**
         * 作者名，
         */
        val author: String,
        /**
         * 网站名，
         * 考虑要不要把网站放另一个表，
         */
        val site: String,
        /**
         * 图片地址，
         * 考虑要不要用两个字段表示，能支持只有地址拿不到的图片，
         * 应该不用，只是小说的话，应该没有哪个网站给小说封面做防盗链，
         */
        val imageUrl: String,
        /**
         * 简介，
         */
        val introduction: String,
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
         * 章节列表请求者的类型，
         * [chaptersRequesterType], [chaptersRequesterExtra] 两个字段合起来表示章节列表请求者，能用来请求到小说的一切，
         */
        val chaptersRequesterType: String,
        /**
         * 章节列表请求者的参数，
         * [chaptersRequesterType], [chaptersRequesterExtra] 两个字段合起来表示章节列表请求者，能用来请求到小说的一切，
         */
        val chaptersRequesterExtra: String,
        /**
         * 阅读进度，
         * 阅读至的章节索引，
         */
        val chapterReadAt: Int = 0,
        /**
         * 章节内的阅读进度，
         * 看到第几页或者第几个字，具体没决定，
         */
        val textReadAt: Int = 0,
        /**
         * 是否在书架上，
         */
        val bookshelf: Boolean = false,

        // 下面的可空，

        /**
         * 最新章节名,
         */
        val chapterNameLast: String? = null,
        /**
         * 阅读进度章节名,
         */
        val chapterNameReadAt: String? = null,
        /**
         * 最新更新时间, 也就是最新一章更新的时间，
         */
        val updateTime: Date? = null,
        /**
         * 检查更新时间, 也就是这个时间之前的更新是已知的，
         */
        val checkUpdateTime: Date? = null,
        /**
         * 拿到上一个更新的时间, 也就是上次刷出更新的[checkUpdateTime],
         * 用来对比阅读时间就知道是否是已读了，
         */
        val receiveUpdateTime: Date? = null,
        /**
         * 服务器端id,
         */
        val remoteId: Int? = null
)

