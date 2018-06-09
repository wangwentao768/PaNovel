package cc.aoeiuv020.reader

import android.content.Context
import android.view.ViewGroup
import cc.aoeiuv020.reader.complex.ComplexReader
import cc.aoeiuv020.reader.simple.SimpleReader

/**
 *
 * Created by AoEiuV020 on 2017.12.01-01:16:49.
 */
object Readers {

    fun getReader(ctx: Context, novel: String, parent: ViewGroup, requester: TextRequester, config: ReaderConfig)
            : INovelReader = if (config.animationMode == AnimationMode.SIMPLE) {
        SimpleReader(ctx, novel, parent, requester, config)
    } else {
        ComplexReader(ctx, novel, parent, requester, config)
    }
}
