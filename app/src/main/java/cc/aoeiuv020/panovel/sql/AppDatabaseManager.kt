package cc.aoeiuv020.panovel.sql

import android.content.Context
import android.support.annotation.VisibleForTesting
import cc.aoeiuv020.panovel.local.BookListData
import cc.aoeiuv020.panovel.sql.db.AppDatabase
import cc.aoeiuv020.panovel.sql.entity.BookList
import cc.aoeiuv020.panovel.sql.entity.BookListItem
import cc.aoeiuv020.panovel.sql.entity.RequesterData
import cc.aoeiuv020.panovel.sql.entity.toSql

/**
 * 封装一个数据库多个表多个DAO的联用，
 *
 * Created by AoEiuV020 on 2018.04.27-11:52:55.
 */
class AppDatabaseManager(context: Context) {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val db: AppDatabase = AppDatabase.getInstance(context)


    fun importBookList(bookListData: BookListData): BookList = db.runInTransaction<BookList> {
        val bookList = BookList(
                name = bookListData.name
        )
        val id = db.bookListDao().insertBookList(bookList)
        bookList.id = id
        bookListData.list.forEach { novelItem ->
            val bookListItem = BookListItem(
                    bookListId = id,
                    detailRequester = novelItem.requester.toSql()
            )
            db.bookListDao().insertBookListItem(bookListItem)
        }
        bookList
    }

    fun getAllBookLists(): List<BookList> {
        return db.bookListDao().getAllBookLists()
    }

    fun renameBookList(bookList: BookList, name: String) {
        val id = requireNotNull(bookList.id) {
            "require bookList.id was null,"
        }
        db.bookListDao().renameBookList(id, name)
    }

    fun removeBookList(bookList: BookList) {
        val id = requireNotNull(bookList.id) {
            "require bookList.id was null,"
        }
        db.bookListDao().removeBookList(id)
    }

    fun listBookshelf(): List<RequesterData> {
        return db.bookshelfDao().list().map {
            it.detailRequester
        }
    }

    fun addBookshelf(requesterData: RequesterData) {
        db.bookshelfDao().put(requesterData)
    }

    fun removeBookshelf(requesterData: RequesterData) {
        db.bookshelfDao().remove(requesterData)
    }

    fun containsBookshelf(requesterData: RequesterData): Boolean {
        return db.bookshelfDao().contains(requesterData)
    }

}