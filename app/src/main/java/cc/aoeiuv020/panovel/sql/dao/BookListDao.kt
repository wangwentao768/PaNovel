package cc.aoeiuv020.panovel.sql.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import cc.aoeiuv020.panovel.local.BookListData
import cc.aoeiuv020.panovel.sql.entity.BookList
import cc.aoeiuv020.panovel.sql.entity.BookListItem
import cc.aoeiuv020.panovel.sql.entity.NovelMini

/**
 * 书单相关，操作永久数据库中的表，
 * Created by AoEiuV020 on 2018.04.26-19:33:25.
 */
@Dao
abstract class BookListDao {
    @Insert
    abstract fun insertBookList(bookList: BookList): Long

    @Insert
    abstract fun insertNovelMini(novelMini: NovelMini): Long

    @Insert
    abstract fun insertBookListItem(bookListItem: BookListItem)

    @Query("select * from NovelMini" +
            " where detailRequesterType = :type" +
            " and detailRequesterExtra = :extra")
    abstract fun queryByDetailRequester(type: String, extra: String): NovelMini?

    /**
     * 查询，没有就新建一个插入，并拿出id,
     */
    private fun queryOrNew(type: String, extra: String): NovelMini {
        return queryByDetailRequester(type, extra)
                ?: run {
                    val newNovelMini = NovelMini().apply {
                        detailRequesterType = type
                        detailRequesterExtra = extra
                    }
                    val newNovelMiniId = insertNovelMini(newNovelMini)
                    newNovelMini.id = newNovelMiniId
                    newNovelMini
                }
    }

    @Transaction
    open fun importBookList(bookListData: BookListData): BookList {
        val bookList = BookList().apply {
            name = bookListData.name
        }
        val id = insertBookList(bookList)
        bookList.id = id
        bookListData.list.forEach { novelItem ->
            val type = novelItem.requester.type
            val extra = novelItem.requester.extra
            val novelMiniId = queryOrNew(type, extra).id
                    ?: throw IllegalStateException("impossible, NovelMini.id is null")
            val bookListItem = BookListItem.new(id, novelMiniId)
            insertBookListItem(bookListItem)
        }
        return bookList
    }
}