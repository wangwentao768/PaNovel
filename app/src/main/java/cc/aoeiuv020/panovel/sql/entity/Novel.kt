package cc.aoeiuv020.panovel.sql.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull
import java.util.*

/**
 * Created by AoEiuV020 on 2018.04.25-20:35:27.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
@Entity(
        indices = [
            (Index(value = ["detailRequesterType", "detailRequesterExtra"], unique = true))
        ]
)
class Novel {
    /**
     * 普通的自增id, 没有别的用，
     */
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
    /**
     * 小说名，
     */
    var name: String? = null
    /**
     * 作者名，
     */
    var author: String? = null
    /**
     * 网站名，
     * 考虑要不要把网站放另一个表，
     */
    var site: String? = null
    /**
     * 图片地址，
     * 考虑要不要用两个字段表示，能支持只有地址拿不到的图片，
     * 应该不用，只是小说的话，应该没有哪个网站给小说封面做防盗链，
     */
    var imageUrl: String? = null
    /**
     * 简介，
     */
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
    var chaptersRequesterType: String? = null
    /**
     * 章节列表请求者的参数，
     * [chaptersRequesterType], [chaptersRequesterExtra] 两个字段合起来表示章节列表请求者，能用来请求到小说的一切，
     */
    var chaptersRequesterExtra: String? = null
    /**
     * 章节数，
     * 考虑要不要读另一张表，
     */
    var chapterCount: Int? = null
    /**
     * 阅读进度，
     * 阅读至的章节索引，
     */
    var chapterReadAt: Int? = null
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
