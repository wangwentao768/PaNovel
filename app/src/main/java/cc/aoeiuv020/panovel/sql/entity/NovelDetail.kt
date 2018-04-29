package cc.aoeiuv020.panovel.sql.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull
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
class NovelDetail {
    /**
     * 普通的id,
     */
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
    /**
     * 小说名，
     */
    @NonNull
    var name: String? = null
    /**
     * 作者名，
     */
    @NonNull
    var author: String? = null
    /**
     * 网站名，
     * 考虑要不要把网站放另一个表，
     */
    @NonNull
    var site: String? = null
    /**
     * 图片地址，
     * 考虑要不要用两个字段表示，能支持只有地址拿不到的图片，
     * 应该不用，只是小说的话，应该没有哪个网站给小说封面做防盗链，
     */
    @NonNull
    var imageUrl: String? = null
    /**
     * 简介，
     */
    @NonNull
    var introduction: String? = null
    /**
     * 详情请求者的类型，
     * [detailRequesterType], [detailRequesterExtra] 两个字段合起来表示详情请求者，能用来请求到小说的一切，
     */
    @NonNull
    var detailRequesterType: String? = null
    /**
     * 详情请求者的参数，
     * [detailRequesterType], [detailRequesterExtra] 两个字段合起来表示详情请求者，能用来请求到小说的一切，
     */
    @NonNull
    var detailRequesterExtra: String? = null
    /**
     * 章节列表请求者的类型，
     * [chaptersRequesterType], [chaptersRequesterExtra] 两个字段合起来表示章节列表请求者，能用来请求到小说的一切，
     */
    @NonNull
    var chaptersRequesterType: String? = null
    /**
     * 章节列表请求者的参数，
     * [chaptersRequesterType], [chaptersRequesterExtra] 两个字段合起来表示章节列表请求者，能用来请求到小说的一切，
     */
    @NonNull
    var chaptersRequesterExtra: String? = null
    /**
     * 阅读进度，
     * 阅读至的章节索引，
     */
    @NonNull
    var chapterReadAt: Int? = null
    /**
     * 章节内的阅读进度，
     * 看到第几页或者第几个字，具体没决定，
     */
    @NonNull
    var textReadAt: Int? = null
    /**
     * 是否在书架上，
     */
    @NonNull
    var bookshelf: Boolean? = null


    /**
     * 最新章节名,
     */
    var chapterNameLast: String? = null
    /**
     * 阅读进度章节名,
     */
    var chapterNameReadAt: String? = null
    /**
     * 最新更新时间, 也就是最新一章更新的时间，
     */
    var updateTime: Date? = null
    /**
     * 检查更新时间, 也就是这个时间之前的更新是已知的，
     */
    var checkUpdateTime: Date? = null
    /**
     * 拿到上一个更新的时间, 也就是上次刷出更新的[checkUpdateTime],
     * 用来对比阅读时间就知道是否是已读了，
     */
    var receiveUpdateTime: Date? = null
    /**
     * 服务器端id,
     */
    var remoteId: Int? = null
}

