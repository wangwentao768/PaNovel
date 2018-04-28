package cc.aoeiuv020.panovel.sql.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull

/**
 * Created by AoEiuV020 on 2018.04.28-20:46:32.
 */
@Entity(
        indices = [
            (Index(
                    value = ["textRequesterType", "textRequesterExtra"],
                    unique = true
            ))
        ]
)
class Content {
    /**
     * 普通的id,
     */
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
    /**
     * 章节内容请求者的类型，
     * [textRequesterType], [textRequesterExtra] 两个字段合起来表示详情请求者，能用来请求这一章节内容，
     */
    @NonNull
    var textRequesterType: String? = null
    /**
     * 章节内容请求者的参数，
     * [textRequesterType], [textRequesterExtra] 两个字段合起来表示详情请求者，能用来请求这一章节内容，
     */
    @NonNull
    var textRequesterExtra: String? = null
    /**
     * 小说正文，
     */
    @NonNull
    var content: String? = null
}