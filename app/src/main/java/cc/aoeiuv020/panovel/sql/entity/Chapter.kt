package cc.aoeiuv020.panovel.sql.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull

/**
 * 缓存数据库中的章节表，
 *
 * Created by AoEiuV020 on 2018.04.25-21:30:42.
 */
@Entity(
        indices = [
            (Index(value = ["novelDetailId", "index"], unique = true)),
            (Index(value = ["novelDetailId", "volumeIndex"]))
        ],
        foreignKeys = [
            (ForeignKey(
                    entity = NovelDetail::class,
                    parentColumns = ["id"],
                    childColumns = ["novelDetailId"],
                    onDelete = ForeignKey.CASCADE
            )),
            (ForeignKey(
                    entity = Volume::class,
                    parentColumns = ["novelDetailId", "index"],
                    childColumns = ["novelDetailId", "volumeIndex"]
            ))
        ]
)
@Suppress("MemberVisibilityCanBePrivate", "unused")
class Chapter {
    /**
     * 普通的id,
     */
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
    /**
     * 外键[NovelDetail]表的id,
     */
    @NonNull
    var novelDetailId: Long? = null
    /**
     * 章节索引，
     * 也就是第几章，
     */
    @NonNull
    var index: Int? = null
    /**
     * 卷索引，
     * 支持为空，没有卷，全部默认一卷，
     */
    var volumeIndex: Int? = null
    /**
     * 章节名，
     */
    @NonNull
    var name: String? = null
    /**
     * 章节内容请求者的类型，
     * [textRequesterType], [textRequesterExtra] 两个字段合起来表示详情请求者，能用来请求这一章节内容，
     */
    @NonNull
    var textRequesterType: String? = null
    /**
     * 章节内容请求者的参数，
     * [textRequesterType], [textRequesterExtra] 两个字段合起来表示详情请求者，能用来请求这一章节内容，
     */
    @NonNull
    var textRequesterExtra: String? = null

}