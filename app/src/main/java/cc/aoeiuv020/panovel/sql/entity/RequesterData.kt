package cc.aoeiuv020.panovel.sql.entity

/**
 * Created by AoEiuV020 on 2018.05.04-17:49:27.
 */
data class RequesterData(
        /**
         * 请求者的类型，
         */
        val type: String,
        /**
         * 详情请求者的参数，
         */
        val extra: String
)