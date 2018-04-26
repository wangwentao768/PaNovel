package cc.aoeiuv020.panovel.sql.db

import android.arch.persistence.room.TypeConverter
import java.util.*

/**
 * Created by AoEiuV020 on 2018.04.25-22:25:56.
 */
@Suppress("unused")
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}