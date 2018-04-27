package cc.aoeiuv020.panovel.sql

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import cc.aoeiuv020.panovel.sql.db.CacheDatabase
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
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
class InsertSpeedTest : AnkoLogger {
    companion object {
        private var setUpIsDone = false
    }

    @Rule
    @JvmField
    val watcher = TestUtil.timeWatcher


    @Before
    fun setUp() {
        val context: Context = InstrumentationRegistry.getTargetContext()
        CacheDatabase.init(context)
        val dbFile = CacheDatabase.dbFile
        if (!setUpIsDone) {
            setUpIsDone = true
            if (dbFile.exists()) {
                dbFile.delete()
            }
        }
        assertEquals(dbFile.path, CacheDatabase.instance.openHelper.databaseName)

    }

    @Test
    fun a1_insert() {
        // 29ms,
        val nCount = CacheDatabase.instance.novelDetailDao()
                .getCount()
        info { "now Count: $nCount" }
        val dao = CacheDatabase.instance.novelDetailDao()
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
        // 781ms,
        val nCount = CacheDatabase.instance.novelDetailDao()
                .getCount()
        info { "now Count: $nCount" }
        val count = 10000
        CacheDatabase.instance.runInTransaction {
            repeat(count) {
                CacheDatabase.instance.novelDetailDao()
                        .insertNovels(TestUtil.createNovelDetail())
            }
        }

    }

    @Test
    fun a4_insert_1w_one_time() {
        // 297ms,
        val nCount = CacheDatabase.instance.novelDetailDao()
                .getCount()
        info { "now Count: $nCount" }
        val count = 10000
        val novels = Array(count) {
            TestUtil.createNovelDetail()
        }
        CacheDatabase.instance.novelDetailDao()
                .insertNovels(*novels)
    }

    @Test
    fun a5_insert_1w_step_501() {
        // 352ms,
        val nCount = CacheDatabase.instance.novelDetailDao()
                .getCount()
        info { "now Count: $nCount" }
        val count = 10000
        val step = 501
        CacheDatabase.instance.runInTransaction {
            for (i in 0..count step step) {
                val novels = Array(min(step, count - i)) {
                    TestUtil.createNovelDetail()
                }
                CacheDatabase.instance.novelDetailDao()
                        .insertNovels(*novels)
            }
        }

    }

    @Test
    fun a6_delete_all() {
        // 249ms,
        val result = CacheDatabase.instance.novelDetailDao()
                .deleteAll()
        assertEquals(30001, result)
    }
}