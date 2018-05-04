package cc.aoeiuv020.panovel.sql.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

/**
 * Created by AoEiuV020 on 2018.04.28-20:46:32.
 */
@Entity(
        indices = [
            (Index(
                    value = ["textRequesterType", "textRequesterExtra"],
                    unique = true
            ))
        ]
)
@Suppress("MemberVisibilityCanBePrivate", "unused")
data class Content(
        /**
         * 普通的id,
         */
        @PrimaryKey(autoGenerate = true)
        val id: Long? = null,
        /**
         * 章节内容请求者的类型，
         * [textRequesterType], [textRequesterExtra] 两个字段合起来表示详情请求者，能用来请求这一章节内容，
         */
        val textRequesterType: String,
        /**
         * 章节内容请求者的参数，
         * [textRequesterType], [textRequesterExtra] 两个字段合起来表示详情请求者，能用来请求这一章节内容，
         */
        val textRequesterExtra: String,
        /**
         * 小说正文，
         */
        val content: String
)