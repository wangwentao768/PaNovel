package cc.aoeiuv020.panovel.sql.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull

/**
 * 缓存数据库中的小说的卷，
 * 可选，章节是必须，卷可以默认空，
 * Created by AoEiuV020 on 2018.04.26-13:21:56.
 */
@Entity(
        indices = [(Index(value = ["novelDetailId", "index"], unique = true))],
        foreignKeys = [
            (ForeignKey(
                    entity = NovelDetail::class,
                    parentColumns = ["id"],
                    childColumns = ["novelDetailId"]
            ))
        ]
)
@Suppress("MemberVisibilityCanBePrivate", "unused")
class Volume {
    /**
     * 普通的id,
     */
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
    /**
     * 外键，小说ID,
     */
    @NonNull
    var novelDetailId: Long? = null
    /**
     * 卷索引，第几卷，
     */
    @NonNull
    var index: Int? = null
    /**
     * 卷名，
     * 考虑要不要用非空，
     */
    var name: String? = null
}