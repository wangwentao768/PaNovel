package cc.aoeiuv020.panovel.sql.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import cc.aoeiuv020.panovel.sql.entity.NovelDetail

/**
 * 操作缓存数据库中的表，
 * Created by AoEiuV020 on 2018.04.25-21:47:54.
 */
@Dao
abstract class NovelDetailDao {
    /**
     * 测试用，不要实用，
     */
    @Insert
    abstract fun insertNovels(vararg novelDetails: NovelDetail)

    @Query("delete from NovelDetail")
    abstract fun deleteAll(): Int

    @Update
    abstract fun updateNovel(novelDetail: NovelDetail)

    @Query("select * from NovelDetail" +
            " where detailRequesterType = :type" +
            " and detailRequesterExtra = :extra")
    abstract fun queryByDetailRequester(type: String, extra: String): NovelDetail?

    fun queryByDetailRequester(novelDetail: NovelDetail): NovelDetail? {
        val type = novelDetail.detailRequesterType
                ?: throw IllegalArgumentException("require type is null,")
        val extra = novelDetail.detailRequesterExtra
                ?: throw IllegalArgumentException("require extra is null,")
        return queryByDetailRequester(type, extra)
    }

    @Query("select count(*) from NovelDetail")
    abstract fun getCount(): Int
}