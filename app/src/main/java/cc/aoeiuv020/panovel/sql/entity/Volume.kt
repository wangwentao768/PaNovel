package cc.aoeiuv020.panovel.sql.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.support.annotation.NonNull

/**
 * 小说的卷，
 * 可选，章节是必须，卷可以默认空，
 * Created by AoEiuV020 on 2018.04.26-13:21:56.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
@Entity(
        primaryKeys = ["novelId", "index"],
        foreignKeys = [
            (ForeignKey(
                    entity = Novel::class,
                    parentColumns = ["id"],
                    childColumns = ["novelId"]
            ))
        ]
)
class Volume {
    /**
     * 外键，小说ID,
     */
    @NonNull
    var novelId: Int? = null
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