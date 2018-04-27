package cc.aoeiuv020.panovel.sql.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull

/**
 * 永久数据库中的小说表，
 * 包含小说详情的请求者，用于从缓存数据库中的小说详情表中获取详情，没有也能联网获取，
 *
 * Created by AoEiuV020 on 2018.04.26-16:51:52.
 */
@Entity(
        indices = [
            (android.arch.persistence.room.Index(
                    value = ["detailRequesterType", "detailRequesterExtra"],
                    unique = true
            ))
        ]
)
@Suppress("MemberVisibilityCanBePrivate", "unused")
class NovelMini {
    /**
     * 普通的id, 在书单里外键用到，
     */
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
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
     * 是否在书架上，
     */
    var bookshelf: Boolean? = null
    /**
     * 阅读进度，
     * 阅读至的章节索引，
     */
    var chapterReadAt: Int? = null
}