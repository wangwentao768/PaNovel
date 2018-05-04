package cc.aoeiuv020.panovel.sql.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * 永久数据库中的书单，
 * Created by AoEiuV020 on 2018.04.26-17:03:15.
 */
@Entity
@Suppress("MemberVisibilityCanBePrivate", "unused")
data class BookList(
        /**
         * 普通的id,
         */
        @PrimaryKey(autoGenerate = true)
        var id: Long? = null,
        /**
         * 书单名，可以重复，
         */
        var name: String
)
