package cc.aoeiuv020.panovel.sql.entity

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

/**
 * 缓存数据库中的小说详情表，
 * Created by AoEiuV020 on 2018.04.25-20:35:27.
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
         * "detail_requester_type", "detail_requester_extra" 两个字段合起来表示详情请求者，能用来请求到小说的一切，
         */
        @Embedded(prefix = "detail_requester_")
        val detailRequester: RequesterData,
        /**
         * 章节列表请求者的类型，
         * "chapters_requester_type", "chapters_requester_extra" 两个字段合起来表示章节列表请求者，能用来请求到小说的章节列表，
         */
        @Embedded(prefix = "chapters_requester_")
        val chaptersRequester: RequesterData
)

