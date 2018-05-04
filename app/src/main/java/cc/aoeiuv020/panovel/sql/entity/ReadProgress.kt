package cc.aoeiuv020.panovel.sql.entity

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

/**
 * Created by AoEiuV020 on 2018.05.04-16:39:19.
 */
@Entity(
        indices = [
            (Index(
                    value = ["detail_requester_type", "detail_requester_extra"],
                    unique = true
            ))
        ]
)
@Suppress("MemberVisibilityCanBePrivate", "unused")
data class ReadProgress(
        /**
         * 普通的id,
         */
        @PrimaryKey(autoGenerate = true)
        val id: Long? = null,
        /**
         * 详情请求者的类型，
         * "detail_requester_type", "detail_requester_extra" 两个字段合起来表示详情请求者，能用来请求到小说的一切，
         */
        @Embedded(prefix = "detail_requester_")
        val detailRequester: RequesterData,
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
)