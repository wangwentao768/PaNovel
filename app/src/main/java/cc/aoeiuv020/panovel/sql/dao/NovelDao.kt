package cc.aoeiuv020.panovel.sql.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import cc.aoeiuv020.panovel.sql.entity.Novel

/**
 * Created by AoEiuV020 on 2018.04.25-21:47:54.
 */
@Dao
abstract class NovelDao {
    /**
     * 测试用，不要实用，
     */
    @Insert
    abstract fun insertNovels(vararg novels: Novel)

    @Query("delete from Novel")
    abstract fun deleteAll(): Int

    @Update
    abstract fun updateNovel(novel: Novel)

    @Query("select * from Novel" +
            " where detailRequesterType = :type" +
            " and detailRequesterExtra = :extra")
    abstract fun queryByDetailRequester(type: String, extra: String): Novel

    fun queryByDetailRequester(novel: Novel): Novel {
        val type = novel.detailRequesterType
                ?: throw IllegalArgumentException("require type is null,")
        val extra = novel.detailRequesterExtra
                ?: throw IllegalArgumentException("require extra is null,")
        return queryByDetailRequester(type, extra)
    }

    @Query("select count(*) from Novel")
    abstract fun getCount(): Int
}