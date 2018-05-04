package cc.aoeiuv020.panovel.sql.dao

import android.arch.persistence.room.*
import android.support.annotation.VisibleForTesting
import cc.aoeiuv020.panovel.sql.entity.BookList
import cc.aoeiuv020.panovel.sql.entity.BookListItem

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