package cc.aoeiuv020.panovel.base.item

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import cc.aoeiuv020.panovel.sql.entity.NovelDetail
import cc.aoeiuv020.panovel.sql.entity.NovelStatus
import cc.aoeiuv020.panovel.sql.entity.RequesterData
import cc.aoeiuv020.panovel.text.CheckableImageView
import cn.lemon.view.adapter.BaseViewHolder
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.novel_item_big.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * Created by AoEiuV020 on 2017.11.22-11:19:03.
 */
abstract class SmallItemViewHolder<out T : SmallItemPresenter<*>>(protected val itemListPresenter: BaseItemListPresenter<*, T>,
                                                                  protected val ctx: Context, parent: ViewGroup?, layoutId: Int,
                                                                  listener: OnItemLongClickListener? = null)
    : BaseViewHolder<RequesterData>(parent, layoutId), SmallItemView, AnkoLogger {
    @Suppress("UNCHECKED_CAST")
    protected val presenter: T = itemListPresenter.subPresenter()
    private val image = itemView.imageView
    private val name = itemView.tvName
    private val author = itemView.tvAuthor
    private val site = itemView.tvSite
    private val last = itemView.tvLast
    /**
     * 缓存更新时间，用来判断小红点是否显示，
     */
    protected var updateTime: Date? = null
    /**
     * 书架页没有这个star按钮，
     */
    private val star: CheckableImageView? = itemView.ivStar
    protected lateinit var detailRequester: RequesterData

    init {
/*
        name.setOnClickListener {
            NovelDetailActivity.start(ctx, detailRequester)
        }

        name.setOnLongClickListener {
            FuzzySearchActivity.start(ctx, detailRequester)
            true
        }

        last.setOnClickListener {
            NovelTextActivity.start(ctx, detailRequester, -1)
        }

        itemView.setOnClickListener {
            NovelTextActivity.start(ctx, detailRequester)
        }

*/
/*
        listener?.let { nonnullListener ->
            itemView.setOnLongClickListener {
                nonnullListener.onItemLongClick(layoutPosition, novelItem)
                true
            }
        }
*/

/*
        star?.apply {
            setOnClickListener {
                toggle()
                if (isChecked) {
                    Bookshelf.add(novelItem)
                } else {
                    Bookshelf.remove(novelItem)
                }
            }
        }
*/
    }

    override fun setData(data: RequesterData) {
        this.detailRequester = data
        debug {
            "${this.hashCode()} $layoutPosition setData $data"
        }
//        star?.isChecked = Bookshelf.contains(novelItem)

        // 清空残留数据，避免闪烁，
        name.text = ""
        author.text = ""
        site.text = ""
        last.text = ""
        image.setImageDrawable(null)

        presenter.attach(this)
        presenter.requestDetail(detailRequester)
    }

    override fun showDetail(novelDetail: NovelDetail) {
        name.text = novelDetail.name
        author.text = novelDetail.author
        site.text = novelDetail.site
        Glide.with(ctx).load(novelDetail.imageUrl).into(image)
        presenter.requestStatus(novelDetail)
    }

    override fun showStatus(novelStatus: NovelStatus) {
        last.text = novelStatus.chapterNameLast
    }

    fun destroy() {
        presenter.detach()
    }
}

open class DefaultItemViewHolder<out T : BigItemPresenter<*>>(itemListPresenter: BaseItemListPresenter<*, T>,
                                                              ctx: Context, parent: ViewGroup?, layoutId: Int,
                                                              listener: OnItemLongClickListener? = null)
    : SmallItemViewHolder<T>(itemListPresenter, ctx, parent, layoutId, listener), BigItemView {
    companion object {
        @SuppressLint("SimpleDateFormat")
        private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    }

    /**
     * 小型视图没有这两个，
     */
    private val update: TextView? = itemView.tvUpdate
    private val readAt: TextView? = itemView.tvReadAt

    override fun setData(data: RequesterData) {
        super.setData(data)

        // 清空残留数据，避免闪烁，
        update?.text = ""
        readAt?.text = ""
    }

    override fun showStatus(novelStatus: NovelStatus) {
        super.showStatus(novelStatus)

        readAt?.text = novelStatus.chapterNameReadAt
        update?.text = novelStatus.updateTime?.let {
            sdf.format(it)
        } ?: "null"
    }
}

