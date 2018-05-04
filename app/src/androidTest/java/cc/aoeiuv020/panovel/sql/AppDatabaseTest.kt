package cc.aoeiuv020.panovel.sql

import android.arch.persistence.room.Room
import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import cc.aoeiuv020.panovel.local.BookListData
import cc.aoeiuv020.panovel.sql.db.AppDatabase
import cc.aoeiuv020.panovel.sql.entity.Bookshelf
import cc.aoeiuv020.panovel.sql.entity.RequesterData
import cc.aoeiuv020.panovel.util.assetsRead
import com.google.gson.Gson
import org.junit.Assert.assertFalse
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
class AppDatabaseTest {

    @Rule
    @JvmField
    val watcher = SqlTestUtil.timeWatcher

    lateinit var context: Context
    lateinit var db: AppDatabase

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getTargetContext()
        db = Room.inMemoryDatabaseBuilder(
                context.applicationContext,
                AppDatabase::class.java
        ).build()
    }


    private fun readBookListData(name: String): BookListData {
        val json = InstrumentationRegistry.getContext().assetsRead(name)
        return Gson().fromJson(json, BookListData::class.java)
    }

    @Test
    fun a1_contains() {
        val book = Bookshelf(
                detailRequester = RequesterData("type", "extra")
        )
        db.bookshelfDao().contains(book).also {
            assertFalse(it)
        }
        db.bookshelfDao().put(book)
        db.bookshelfDao().contains(book).also {
            assertTrue(it)
        }
    }
}