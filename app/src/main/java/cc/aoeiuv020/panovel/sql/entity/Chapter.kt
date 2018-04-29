package cc.aoeiuv020.panovel.sql.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

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
data class Chapter(
        /**
         * 普通的id,
         */
        @PrimaryKey(autoGenerate = true)
        val id: Long? = null,
        /**
         * 外键[NovelDetail]表的id,
         */
        val novelDetailId: Long,
        /**
         * 章节索引，
         * 也就是第几章，
         */
        val index: Int,
        /**
         * 卷索引，
         * 支持为空，没有卷，全部默认一卷，
         */
        val volumeIndex: Int? = null,
        /**
         * 章节名，
         */
        val name: String,
        /**
         * 章节内容请求者的类型，
         * [textRequesterType], [textRequesterExtra] 两个字段合起来表示详情请求者，能用来请求这一章节内容，
         */
        val textRequesterType: String,
        /**
         * 章节内容请求者的参数，
         * [textRequesterType], [textRequesterExtra] 两个字段合起来表示详情请求者，能用来请求这一章节内容，
         */
        val textRequesterExtra: String
)