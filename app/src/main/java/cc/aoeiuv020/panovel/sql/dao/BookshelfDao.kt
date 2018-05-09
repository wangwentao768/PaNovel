package cc.aoeiuv020.panovel.sql.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import cc.aoeiuv020.panovel.sql.entity.Bookshelf
import cc.aoeiuv020.panovel.sql.entity.RequesterData

/**
 *
 * Created by AoEiuV020 on 2018.04.27-20:07:17.
 */
@Dao
abstract class BookshelfDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun put(bookshelf: Bookshelf)

    @Query("delete from Bookshelf where detail_requester_type = :type and detail_requester_extra = :extra")
    abstract fun remove(type: String, extra: String)

    @Query("select * from Bookshelf")
    abstract fun list(): List<Bookshelf>

    @Query("select count(*) from Bookshelf where detail_requester_type = :type and detail_requester_extra = :extra")
    abstract fun contains(type: String, extra: String): Boolean

    fun contains(bookshelf: Bookshelf) =
            contains(bookshelf.detailRequester)

    fun contains(requesterData: RequesterData) =
            contains(
                    requesterData.type,
                    requesterData.extra
            )

    fun remove(bookshelf: Bookshelf) =
            remove(bookshelf.detailRequester)

    fun remove(requesterData: RequesterData) =
            remove(
                    requesterData.type,
                    requesterData.extra
            )
}