package cc.aoeiuv020.panovel.sql.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull

/**
 * Created by AoEiuV020 on 2018.04.25-21:30:42.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
@Entity(
        indices = [(Index(value = ["novelId", "index"], unique = true))],
        foreignKeys = [
            (ForeignKey(
                    entity = Novel::class,
                    parentColumns = ["id"],
                    childColumns = ["novelId"]
            )),
            (ForeignKey(
                    entity = Volume::class,
                    parentColumns = ["novelId", "index"],
                    childColumns = ["novelId", "volumeIndex"]
            ))
        ]
)
class Chapter {
    /**
     * 普通的id,
     */
    @PrimaryKey
    var id: Int? = null
    /**
     * 外键[Novel]表的id,
     */
    @NonNull
    var novelId: Int? = null
    /**
     * 章节索引，
     * 也就是第几章，
     */
    @NonNull
    var index: Int? = null
    /**
     * 卷索引，
     */
    var volumeIndex: Int? = null
    /**
     * 章节名，
     */
    @NonNull
    var name: String? = null
    /**
     * 小说正文，
     */
    @NonNull
    var content: String? = null
}