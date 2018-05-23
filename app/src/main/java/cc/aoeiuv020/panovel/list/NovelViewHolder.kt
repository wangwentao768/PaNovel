package cc.aoeiuv020.panovel.list

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cc.aoeiuv020.panovel.bookshelf.RefreshingDotView
import cc.aoeiuv020.panovel.data.entity.Novel
import cc.aoeiuv020.panovel.text.CheckableImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.novel_item_big.view.*
import java.text.SimpleDateFormat
import java.util.*

class NovelViewHolder(itemView: View,
                      private val itemListener: NovelItemActionListener)
    : RecyclerView.ViewHolder(itemView) {
    companion object {
        // 用于格式化时间，可能有展示更新时间，
        private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
    }

    // 所有View可空，准备支持不同布局，小的布局可能大部分View都没有，
    private val name: TextView? = itemView.tvName
    private val author: TextView? = itemView.tvAuthor
    private val site: TextView? = itemView.tvSite
    private val image: ImageView? = itemView.ivImage
    private val last: TextView? = itemView.tvLast
    private val checkUpdate: TextView? = itemView.tvUpdate
    private val readAt: TextView? = itemView.tvReadAt
    private val star: CheckableImageView? = itemView.ivStar
    private val refreshingDot: RefreshingDotView? = itemView.rdRefreshing
    // 提供外面的加调方法使用，
    lateinit var novel: Novel
        private set
    val ctx: Context = itemView.context

    init {
        refreshingDot?.setOnClickListener {
            itemListener.onDotClick(this)
        }

        refreshingDot?.setOnLongClickListener {
            itemListener.onDotLongClick(this)
        }
        name?.setOnClickListener {
            itemListener.onNameClick(this)
        }

        name?.setOnLongClickListener {
            itemListener.onNameLongClick(this)
        }

        last?.setOnClickListener {
            itemListener.onLastChapterClick(this)
        }

        itemView.setOnClickListener {
            itemListener.onItemClick(this)
        }

        itemView.setOnLongClickListener {
            itemListener.onItemLongClick(this)
        }

        // TODO: star控件改成支持onCheckChanged，这样的话，要试试外部调用移出书架指定isChecked会不会调用click事件，
        star?.setOnClickListener {
            it as CheckableImageView
            it.toggle()
            itemListener.onStarChanged(this, it.isChecked)
        }

/*
TODO:
        newChapterDot.setHeight(ctx.dip(Settings.bookshelfRedDotSize))
        newChapterDot.setColorFilter(Settings.bookshelfRedDotColor)
*/
    }

    fun apply(novel: Novel, refreshTime: Date) {
        this.novel = novel
        name?.text = novel.name
        author?.text = novel.author
        site?.text = novel.site
        last?.text = novel.lastChapterName
        image?.let { imageView ->
            Glide.with(imageView)
                    .load(novel.image)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                            // TODO: 考虑在特定某些异常出现时直接改数据库里的小说图片地址，
                            novel.image = "https://www.snwx8.com/modules/article/images/nocover.jpg"
                            Glide.with(imageView).load(novel.image)
                                    .into(target)
                            return true
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            return false
                        }
                    })
                    .into(imageView)
        }
        star?.isChecked = novel.bookshelf
        checkUpdate?.text = sdf.format(novel.checkUpdateTime)
        readAt?.text = novel.readAtChapterName
        // 用tag防止复用vh导致异步冲突，
        // 如果没开始刷新或者刷新结束，tag会是null,
        // 如果刷新结束前vh被复用，tag就是非空且不等于当前novel,
        // 如果刷新结束前vh被复用，且刷新了新小说且刷新没结束就被原来的小说复用，tag等于novel，不重复请求刷新，
        when {
            itemView.tag === novel -> refreshing()
        // 手动刷新后需要联网更新，
            refreshTime > novel.checkUpdateTime -> refresh()
            else -> refreshed(novel)
        }
    }

    private fun refreshing() {
        // 显示正在刷新，
        refreshingDot?.refreshing()
    }

    /**
     * 主动刷新，
     * 可以在itemListener里调用以刷新这本小说，
     */
    fun refresh() {
        refreshing()
        if (itemView.tag == null || itemView.tag !== novel) {
            itemView.tag = novel
            itemListener.requireRefresh(this)
        }
    }

    /**
     * 刷新结束时调用，
     */
    fun refreshed(novel: Novel) {
        if (itemView.tag !== novel) {
            return
        }
        itemView.tag = null
        // 显示是否有更新，
        refreshingDot?.refreshed(this.novel.receiveUpdateTime > this.novel.readTime)
        // TODO: 根据是否刷出章节，移动item,
    }

    /**
     * 外部调用，小说移出书架，
     */
    fun removeBookshelf() {
        star?.isChecked = false
        itemListener.onStarChanged(this, false)
    }

    fun addBookshelf() {
        star?.isChecked = true
        itemListener.onStarChanged(this, false)
    }
}