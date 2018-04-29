package cc.aoeiuv020.panovel.sql

import android.content.Context
import android.support.annotation.VisibleForTesting
import cc.aoeiuv020.panovel.sql.db.CacheDatabase
import cc.aoeiuv020.panovel.sql.entity.Chapter
import cc.aoeiuv020.panovel.sql.entity.NovelDetail
import cc.aoeiuv020.panovel.sql.entity.NovelMini

/**
 * 封装一个数据库多个表多个DAO的联用，
 *
 * Created by AoEiuV020 on 2018.04.28-16:55:41.
 */
class CacheDatabaseManager(context: Context) {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val db: CacheDatabase = CacheDatabase.build(context)

    fun putChapters(novelDetail: NovelDetail, chapters: List<Chapter>) = db.runInTransaction {
        val id = requireNotNull(novelDetail.id) {
            "require novelDetail.id was null,"
        }
        db.chapterDao().removeChaptersByNovelId(id)
        db.chapterDao().insertChapters(chapters)
    }

    fun getChapters(novelDetail: NovelDetail): List<Chapter> = db.runInTransaction<List<Chapter>> {
        val id = requireNotNull(novelDetail.id) {
            "require novelDetail.id was null,"
        }
        db.chapterDao().queryChaptersByNovelDetailId(id)
    }

    fun queryByDetailRequester(novelMini: NovelMini): NovelDetail? = db.runInTransaction<NovelDetail?> {
        val type = novelMini.detailRequesterType
        val extra = novelMini.detailRequesterExtra
        db.novelDetailDao().queryByDetailRequester(type, extra)
    }

    fun queryByDetailRequester(novelDetail: NovelDetail): NovelDetail? = db.runInTransaction<NovelDetail?> {
        val type = novelDetail.detailRequesterType
        val extra = novelDetail.detailRequesterExtra
        db.novelDetailDao().queryByDetailRequester(type, extra)
    }

}