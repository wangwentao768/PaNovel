package cc.aoeiuv020.panovel.sql.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import cc.aoeiuv020.panovel.sql.dao.BookListDao
import cc.aoeiuv020.panovel.sql.entity.BookList
import cc.aoeiuv020.panovel.sql.entity.BookListItem
import cc.aoeiuv020.panovel.sql.entity.NovelMini
import java.io.File

/**
 * Created by AoEiuV020 on 2018.04.26-17:35:30.
 */
@Database(
        entities = [NovelMini::class, BookList::class, BookListItem::class],
        version = 1
)
@TypeConverters(value = [Converters::class])
abstract class AppDatabase : RoomDatabase() {
    companion object {
        lateinit var instance: AppDatabase
            private set
        lateinit var dbFile: File
            private set

        @Synchronized
        fun init(context: Context) {
            instance = build(context)
        }

        private fun build(context: Context): AppDatabase {
            dbFile = context.getDatabasePath("panovel-app.db")
            return Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    dbFile.path
            ).build()
        }
    }

    abstract fun bookListDao(): BookListDao
}