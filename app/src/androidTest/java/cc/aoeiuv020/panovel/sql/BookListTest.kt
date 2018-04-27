package cc.aoeiuv020.panovel.sql

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import cc.aoeiuv020.panovel.local.BookListData
import cc.aoeiuv020.panovel.sql.dao.BookListDao
import cc.aoeiuv020.panovel.sql.db.AppDatabase
import cc.aoeiuv020.panovel.util.assetsRead
import com.google.gson.Gson
import org.jetbrains.anko.AnkoLogger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 *
 * Created by AoEiuV020 on 2018.04.26-18:01:24.
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
class BookListTest : AnkoLogger {
    companion object {
        private var setUpIsDone = false
    }

    @Rule
    @JvmField
    val watcher = TestUtil.timeWatcher

    lateinit var context: Context

    lateinit var dao: BookListDao

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getTargetContext()
        AppDatabase.init(context)
        dao = AppDatabase.instance.bookListDao()
        val dbFile = AppDatabase.dbFile
        if (!setUpIsDone) {
            setUpIsDone = true
            if (dbFile.exists()) {
                dbFile.delete()
            }
        }
        assertEquals(dbFile.path, AppDatabase.instance.openHelper.databaseName)
    }

    private fun readBookListData(name: String): BookListData {
        val json = InstrumentationRegistry.getContext().assetsRead(name)
        return Gson().fromJson(json, BookListData::class.java)
    }

    private fun importBookListFromAssetsJson(name: String) {
        val bookListData = readBookListData(name)
        val bookList = dao.importBookList(bookListData)
        bookList.apply {
            assertTrue(id != null)
            assertEquals(bookListData.name, bookList.name)
        }
    }

    @Test
    fun a1_import_test_book_list() {
        importBookListFromAssetsJson("bookList-test.json")
        val count = dao.getNovelMiniCount()
        assertEquals(7, count)
    }

    @Test
    fun a2_import_backup_book_list() {
        importBookListFromAssetsJson("bookList-backup.json")
        val count = dao.getNovelMiniCount()
        assertEquals(14, count)
    }

    @Test
    fun a3_import_again() {
        importBookListFromAssetsJson("bookList-test.json")
        val count = dao.getNovelMiniCount()
        assertEquals(14, count)
    }

    @Test
    fun a4_query_first() {
        val bookLists = AppDatabase.instance.bookListDao()
                .getAllBookLists()
        val bookList = bookLists.first { it.id == 1L }
        assertEquals("测试书架", bookList.name)

        val novelMiniList = dao.getNovelMiniInBookList(bookList.id!!)
        val bookListData = readBookListData("bookList-test.json")
        val expected = bookListData.list.map { it.requester.extra }
                .sorted()
        val actual = novelMiniList.map { it.detailRequesterExtra!! }
                .sorted()
        assertEquals(expected, actual)
    }

    @Test
    fun a5_rename_first() {
        val bookList = AppDatabase.instance.bookListDao()
                .getBookListById(1L)!!
        assertEquals("测试书架", bookList.name)
        val newName = "新名字书架"
        dao.renameBookList(bookList.id!!, newName)
        val result = dao.getBookListById(bookList.id!!)!!
        assertEquals(newName, result.name)
    }

    @Test
    fun a6_remove_second() {
        val bookList = dao.getBookListById(2L)!!
        assertEquals("书架备份", bookList.name)
        dao.removeBookList(bookList.id!!)
        val count = dao.getNovelMiniCount()
        // TODO: 同步删除小说，这里应该7本，
        assertEquals(14, count)
    }

    @Test
    fun a7_import_backup_book_list() {
        importBookListFromAssetsJson("bookList-backup.json")
        val count = dao.getNovelMiniCount()
        assertEquals(14, count)
    }
}