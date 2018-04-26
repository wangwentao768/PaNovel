package cc.aoeiuv020.panovel.sql.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import cc.aoeiuv020.panovel.sql.dao.NovelDao
import cc.aoeiuv020.panovel.sql.entity.Chapter
import cc.aoeiuv020.panovel.sql.entity.Novel
import cc.aoeiuv020.panovel.sql.entity.Volume
import java.io.File

/**
 * Created by AoEiuV020 on 2018.04.25-21:50:22.
 */
@Database(
        entities = [Novel::class, Chapter::class, Volume::class],
        version = 1
)
@TypeConverters(value = [Converters::class])
abstract class CacheDatabase : RoomDatabase() {
    companion object {
        lateinit var instance: CacheDatabase
            private set
        lateinit var dbFile: File
            private set

        @Synchronized
        fun init(context: Context) {
            instance = build(context)
        }

        private fun build(context: Context): CacheDatabase {
            dbFile = context.cacheDir.resolve("panovel-cache.db")
            return Room.databaseBuilder(
                    context.applicationContext,
                    CacheDatabase::class.java,
                    dbFile.path
            ).build()
        }
    }

    abstract fun novelDao(): NovelDao
}