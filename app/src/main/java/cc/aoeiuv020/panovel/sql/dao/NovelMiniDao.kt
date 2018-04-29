package cc.aoeiuv020.panovel.sql.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.support.annotation.VisibleForTesting
import cc.aoeiuv020.panovel.sql.entity.NovelMini

/**
 * 操作永久数据库中的表，NovelMini,
 * Created by AoEiuV020 on 2018.04.27-12:25:14.
 */
@Dao
abstract class NovelMiniDao {
    @Insert
    protected abstract fun insertNovelMini(novelMini: NovelMini): Long

    @Query("select count(*) from NovelMini")
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    abstract fun getNovelMiniCount(): Int

    @Query("select * from NovelMini" +
            " where detailRequesterType = :type" +
            " and detailRequesterExtra = :extra")
    protected abstract fun queryByDetailRequester(type: String, extra: String): NovelMini?

    /**
     * 查询，没有就新建一个插入，并拿出id,
     */
    fun queryOrNew(type: String, extra: String): NovelMini {
        return queryByDetailRequester(type, extra)
                ?: run {
                    val newNovelMini = NovelMini.new(type, extra)
                    newNovelMini.copy(
                            id = insertNovelMini(newNovelMini)
                    )
                }
    }

}