package cc.aoeiuv020.panovel.sql.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull

/**
 * Created by AoEiuV020 on 2018.04.26-19:37:08.
 */
@Entity(
        indices = [(Index(value = ["bookListId", "novelMiniId"], unique = true))],
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
class BookListItem {
    companion object {
        fun new(bookListId: Long, novelMiniId: Long) = BookListItem().apply {
            this.bookListId = bookListId
            this.novelMiniId = novelMiniId
        }
    }

    /**
     * 普通的id,
     */
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
    /**
     * 外键，书单id,
     */
    @NonNull
    var bookListId: Long? = null
    /**
     * 外键，小说id,
     */
    @NonNull
    var novelMiniId: Long? = null
}