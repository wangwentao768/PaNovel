package cc.aoeiuv020.panovel.sql.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import cc.aoeiuv020.panovel.sql.entity.NovelStatus
import cc.aoeiuv020.panovel.sql.entity.RequesterData

/**
 * Created by AoEiuV020 on 2018.05.02-22:24:57.
 */
@Dao
abstract class NovelStatusDao {

    @Query("select * from NovelStatus where detail_requester_type = :type and detail_requester_extra = :extra")
    abstract fun queryStatusByDetailRequester(type: String, extra: String): NovelStatus?

    @Insert
    protected abstract fun insertNovelStatus(novelStatus: NovelStatus): Long

    /**
     * 查询，没有就新建一个插入，并拿出id,
     */
    fun queryOrNew(type: String, extra: String): NovelStatus {
        return queryStatusByDetailRequester(type, extra)
                ?: run {
                    val newEntity = NovelStatus(
                            detailRequester = RequesterData(type, extra)
                    )
                    newEntity.copy(
                            id = insertNovelStatus(newEntity)
                    )
                }
    }

}