package cc.aoeiuv020.panovel.sql.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import cc.aoeiuv020.panovel.sql.entity.NovelMini

/**
 *
 * Created by AoEiuV020 on 2018.04.27-20:07:17.
 */
@Dao
abstract class BookshelfDao {
    @Query("update NovelMini set bookshelf = 1 where id = :id")
    abstract fun putBookshelfById(id: Long)

    @Query("update NovelMini set bookshelf = 1 where detailRequesterType = :type and detailRequesterExtra = :extra")
    abstract fun putBookshelfByRequester(type: String, extra: String)

    @Query("select * from NovelMini where bookshelf != 0")
    abstract fun getAllBookshelf(): List<NovelMini>

    @Query("update NovelMini set bookshelf = 1 where id in (select novelMiniId from BookListItem where bookListId = :id)")
    abstract fun putBookshelfByBookListId(id: Long)

    @Query("update NovelMini set bookshelf = 0 where id in (select novelMiniId from BookListItem where bookListId = :id)")
    abstract fun removeBookshelfByBookListId(id: Long)
}