package cc.aoeiuv020.panovel.sql

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import cc.aoeiuv020.panovel.sql.db.CacheDatabase
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

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
    val watcher = SqlTestUtil.timeWatcher

    private lateinit var cache: CacheDatabaseManager
    private lateinit var db: CacheDatabase


    @Before
    fun setUp() {
        val context: Context = InstrumentationRegistry.getTargetContext()
        DataManager.init(context)
        cache = DataManager.cache
        if (!setUpIsDone) {
            setUpIsDone = true
//            CacheDatabase.dbFile.takeIf { it.exists() }?.delete()
        }
        db = cache.db
    }

/*
    @Test
    fun a0_delete_all() {
        // 26ms,
        dao.deleteAll()
    }
*/

/*
    @Test
    fun a1_insert() {
        // 6ms,
        val novel = SqlTestUtil.createNovelDetail()
        dao.insertNovels(novel)
        val result = dao.queryByDetailRequester(novel)
                ?: throw IllegalStateException("novel not found,")
        assertEquals(novel.name, result.name)
    }
*/

/*
    @Test
    fun a2_insert_1w_direct() {
        // 33089ms,
        val count = 10000
        repeat(count) {
            CacheDatabase.instance.novelDetailDao()
                    .insertNovels(SqlTestUtil.createNovelDetail())
        }
    }
*/

/*
    @Test
    fun a3_insert_1w_transaction() {
        // 742ms,
        val count = 10000
        db.runInTransaction {
            repeat(count) {
                dao.insertNovels(SqlTestUtil.createNovelDetail())
            }
        }

    }

    @Test
    fun a4_insert_1w_one_time() {
        // 319ms,
        val count = 10000
        val novels = Array(count) {
            SqlTestUtil.createNovelDetail()
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
                    SqlTestUtil.createNovelDetail()
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
*/

    @Test
    fun b1_insert_1k_novel_with_5k_chapter() = db.runInTransaction {
        if (db.chapterDao().getCount() > 100 * 4000) {
            return@runInTransaction
        }
        // 78802ms,
        repeat(1000) {
            SqlTestUtil.createNovelDetail().let {
                db.novelDetailDao().insertNovel(it).let { id ->
                    it.id = id
                    SqlTestUtil.createChapters(it, 5000).let { chapters ->
                        db.chapterDao().insertChapters(chapters)
                    }
                }
            }
        }
    }

    @Test
    fun b2_update_chapters() = db.runInTransaction {
        // 327ms,
        val novelDetail = SqlTestUtil.createNovelDetail().apply {
            detailRequesterExtra = "detail-requester-extra-444"
        }.let {
            cache.queryByDetailRequester(it)
        }!!
        val chapters = SqlTestUtil.createChapters(novelDetail, 10000)
        cache.putChapters(novelDetail, chapters)
    }

    @Test
    fun b3_query_chapters() = db.runInTransaction {
        val novelDetail = SqlTestUtil.createNovelDetail().apply {
            detailRequesterExtra = "detail-requester-extra-444"
        }.let {
            cache.queryByDetailRequester(it)
        }!!
        val chapters = cache.getChapters(novelDetail)
        assertEquals(10000, chapters.size)
    }
}