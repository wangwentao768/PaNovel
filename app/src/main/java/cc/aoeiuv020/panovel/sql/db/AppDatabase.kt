package cc.aoeiuv020.panovel.sql.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import cc.aoeiuv020.panovel.sql.dao.BookListDao
import cc.aoeiuv020.panovel.sql.dao.BookshelfDao
import cc.aoeiuv020.panovel.sql.entity.BookList
import cc.aoeiuv020.panovel.sql.entity.BookListItem
import cc.aoeiuv020.panovel.sql.entity.Bookshelf
import cc.aoeiuv020.panovel.sql.entity.ReadProgress
import java.io.File

/**
 * Created by AoEiuV020 on 2018.04.26-17:35:30.
 */
@Database(
        entities = [BookList::class, BookListItem::class, Bookshelf::class, ReadProgress::class],
        version = 1
)
@TypeConverters(value = [Converters::class])
abstract class AppDatabase : RoomDatabase() {
    companion object {
        lateinit var dbFile: File
            private set
        private var sInstance: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            dbFile = context.getDatabasePath("panovel-app.db")
            return sInstance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    dbFile.path
            ).build().also {
                sInstance = it
            }
        }
    }

    abstract fun bookListDao(): BookListDao
    abstract fun bookshelfDao(): BookshelfDao
}