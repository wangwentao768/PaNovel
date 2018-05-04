package cc.aoeiuv020.panovel.sql.entity

import android.arch.persistence.room.*

/**
 * Created by AoEiuV020 on 2018.04.26-19:37:08.
 */
@Entity(
        indices = [
            (Index(
                    value = ["bookListId", "detail_requester_type", "detail_requester_extra"],
                    unique = true
            ))
        ],
        foreignKeys = [
            (ForeignKey(
                    entity = BookList::class,
                    parentColumns = ["id"],
                    childColumns = ["bookListId"],
                    onDelete = ForeignKey.CASCADE
            ))
        ]
)
@Suppress("MemberVisibilityCanBePrivate", "unused")
data class BookListItem(
        /**
         * 普通的id,
         */
        @PrimaryKey(autoGenerate = true)
        val id: Long? = null,
        /**
         * 外键，书单id,
         */
        val bookListId: Long,
        /**
         * 详情请求者的类型，
         * "detail_requester_type", "detail_requester_extra" 两个字段合起来表示详情请求者，能用来请求到小说的一切，
         */
        @Embedded(prefix = "detail_requester_")
        val detailRequester: RequesterData
)