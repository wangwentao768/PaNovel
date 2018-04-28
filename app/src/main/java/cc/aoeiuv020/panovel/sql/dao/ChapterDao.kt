package cc.aoeiuv020.panovel.sql.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.support.annotation.VisibleForTesting
import cc.aoeiuv020.panovel.sql.entity.Chapter

/**
 * Created by AoEiuV020 on 2018.04.28-18:51:22.
 */
@Dao
abstract class ChapterDao {
    @Insert
    abstract fun insertChapters(chapters: List<Chapter>)

    @Query("delete from Chapter where novelDetailId = :id")
    abstract fun removeChaptersByNovelId(id: Long): Int

    @Query("select count(*) from Chapter")
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    abstract fun getCount(): Int

    @Query("select * from Chapter where novelDetailId = :id")
    abstract fun queryChaptersByNovelDetailId(id: Long): List<Chapter>
}