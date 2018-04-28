package cc.aoeiuv020.panovel.sql.dao

import android.arch.persistence.room.*
import android.support.annotation.VisibleForTesting
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

    @Update
    protected abstract fun updateBookList(bookList: BookList)

    @Delete
    protected abstract fun deleteBookList(bookList: BookList)

    @Insert
    abstract fun insertBookListItem(bookListItem: BookListItem)

    @Query("select * from BookList")
    abstract fun getAllBookLists(): List<BookList>

    @Query("select * from BookList where id = :id")
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    abstract fun getBookListById(id: Long): BookList?

    /**
     * join效率不敢保证，也没测试，
     * 这表正常不会太大，没必要优化，
     */
    @Query("select Mini.*" +
            " from BookList" +
            " join BookListItem" +
            " on BookList.id = BookListItem.bookListId" +
            " join NovelMini Mini" +
            " on BookListItem.novelMiniId = Mini.id" +
            " where BookList.id = :id")
    abstract fun getNovelMiniInBookList(id: Long): List<NovelMini>

    @Transaction
    open fun renameBookList(id: Long, name: String) {
        val bookList = getBookListById(id)
                ?: throw NoSuchElementException("no such BookList where id = $id")
        if (bookList.name != name) {
            bookList.name = name
            updateBookList(bookList)
        }
    }

    @Transaction
    open fun removeBookList(id: Long) {
        val bookList = getBookListById(id)
                ?: throw NoSuchElementException("no such BookList where id = $id")
        deleteBookList(bookList)
    }
}