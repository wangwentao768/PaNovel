package cc.aoeiuv020.panovel.sql

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import cc.aoeiuv020.panovel.sql.dao.NovelDetailDao
import cc.aoeiuv020.panovel.sql.db.CacheDatabase
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import kotlin.math.min

/**
 * 测试在缓存里用数据库，
 * Created by AoEiuV020 on 2018.04.25-18:40:33.
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
class CacheDatabaseTest {
    companion object {
        var setUpIsDone = false
    }

    @Rule
    @JvmField
    val watcher = TestUtil.timeWatcher

    private lateinit var db: CacheDatabase
    private lateinit var dao: NovelDetailDao


    @Before
    fun setUp() {
        val context: Context = InstrumentationRegistry.getTargetContext()
        DataManager.init(context)
        db = DataManager.cache.db
        dao = db.novelDetailDao()
        val dbFile = CacheDatabase.dbFile
        if (!setUpIsDone) {
            setUpIsDone = true
            if (dbFile.exists()) {
                dbFile.delete()
            }
        }
        assertEquals(dbFile.path, db.openHelper.databaseName)

    }

    @Test
    fun a0_delete_all() {
        // 26ms,
        dao.deleteAll()
    }

    @Test
    fun a1_insert() {
        // 6ms,
        val novel = TestUtil.createNovelDetail()
        dao.insertNovels(novel)
        val result = dao.queryByDetailRequester(novel)
                ?: throw IllegalStateException("novel not found,")
        assertEquals(novel.name, result.name)
    }

/*
    @Test
    fun a2_insert_1w_direct() {
        // 33089ms,
        val count = 10000
        repeat(count) {
            CacheDatabase.instance.novelDetailDao()
                    .insertNovels(TestUtil.createNovelDetail())
        }
    }
*/

    @Test
    fun a3_insert_1w_transaction() {
        // 742ms,
        val count = 10000
        db.runInTransaction {
            repeat(count) {
                dao.insertNovels(TestUtil.createNovelDetail())
            }
        }

    }

    @Test
    fun a4_insert_1w_one_time() {
        // 319ms,
        val count = 10000
        val novels = Array(count) {
            TestUtil.createNovelDetail()
        }
        dao.insertNovels(*novels)
    }

    @Test
    fun a5_insert_1w_step_501() {
        // 333ms,
        val count = 10000
        val step = 501
        db.runInTransaction {
            for (i in 0..count step step) {
                val novels = Array(min(step, count - i)) {
                    TestUtil.createNovelDetail()
                }
                dao.insertNovels(*novels)
            }
        }

    }

    @Test
    fun a6_delete_all() {
        // 238ms,
        val result = dao.deleteAll()
        assertEquals(30001, result)
    }
}