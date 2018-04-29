package cc.aoeiuv020.panovel.sql.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

/**
 * Created by AoEiuV020 on 2018.04.26-19:37:08.
 */
@Entity(
        indices = [
            (Index(value = ["bookListId", "novelMiniId"], unique = true)),
            (Index(value = ["novelMiniId"]))
        ],
        foreignKeys = [
            (ForeignKey(
                    entity = BookList::class,
                    parentColumns = ["id"],
                    childColumns = ["bookListId"],
                    onDelete = ForeignKey.CASCADE
            )),
            (ForeignKey(
                    entity = NovelMini::class,
                    parentColumns = ["id"],
                    childColumns = ["novelMiniId"]
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
         * 外键，小说id,
         */
        val novelMiniId: Long
) {
    companion object {
        fun new(bookListId: Long, novelMiniId: Long) = BookListItem(
                bookListId = bookListId,
                novelMiniId = novelMiniId
        )
    }

}