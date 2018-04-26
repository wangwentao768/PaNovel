package cc.aoeiuv020.panovel.sql

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import cc.aoeiuv020.panovel.local.BookListData
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


    @Before
    fun setUp() {
        context = InstrumentationRegistry.getTargetContext()
        AppDatabase.init(context)
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
        val bookList = AppDatabase.instance.bookListDao()
                .importBookList(bookListData)
        bookList.apply {
            assertTrue(id != null)
            assertEquals(bookListData.name, bookList.name)
        }
    }

    @Test
    @Suppress("MemberVisibilityCanBePrivate")
    fun a1_import_book_list() {
        importBookListFromAssetsJson("bookList-test.json")
    }

    @Test
    fun a2_import_another_book_list() {
        importBookListFromAssetsJson("bookList-backup.json")
    }

    @Test
    fun a3_import_again() {
        a1_import_book_list()
    }

    @Test
    fun a4_query_first() {
        val bookLists = AppDatabase.instance.bookListDao()
                .getAllBookLists()
        val bookList = bookLists.first { it.id == 1L }
        assertEquals("测试书架", bookList.name)

        val novelMiniList = AppDatabase.instance.bookListDao()
                .getNovelMiniInBookList(bookList.id!!)
        val bookListData = readBookListData("bookList-test.json")
        val expected = bookListData.list.map { it.requester.extra }
                .sorted()
        val actual = novelMiniList.map { it.detailRequesterExtra!! }
                .sorted()
        assertEquals(expected, actual)
    }
}