@file:Suppress("DEPRECATION")

package cc.aoeiuv020.panovel.text

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.view.*
import android.widget.ImageView
import cc.aoeiuv020.base.jar.toBean
import cc.aoeiuv020.base.jar.toJson
import cc.aoeiuv020.panovel.App
import cc.aoeiuv020.panovel.IView
import cc.aoeiuv020.panovel.R
import cc.aoeiuv020.panovel.api.NovelChapter
import cc.aoeiuv020.panovel.data.DataManager
import cc.aoeiuv020.panovel.data.entity.Novel
import cc.aoeiuv020.panovel.detail.NovelDetailActivity
import cc.aoeiuv020.panovel.main.MainActivity
import cc.aoeiuv020.panovel.report.Reporter
import cc.aoeiuv020.panovel.search.FuzzySearchActivity
import cc.aoeiuv020.panovel.settings.GeneralSettings
import cc.aoeiuv020.panovel.settings.Margins
import cc.aoeiuv020.panovel.settings.ReaderSettings
import cc.aoeiuv020.panovel.util.*
import cc.aoeiuv020.reader.*
import cc.aoeiuv020.reader.AnimationMode
import cc.aoeiuv020.reader.ReaderConfigName.*
import kotlinx.android.synthetic.main.activity_novel_text.*
import kotlinx.android.synthetic.main.dialog_editor.view.*
import kotlinx.android.synthetic.main.dialog_select_color_scheme.view.*
import org.jetbrains.anko.*
import java.io.FileNotFoundException
import java.util.concurrent.TimeUnit


/**
 *
 * Created by AoEiuV020 on 2017.10.03-19:06:44.
 */
class NovelTextActivity : NovelTextBaseFullScreenActivity(), IView {
    companion object {
        fun start(ctx: Context, novel: Novel) {
            start(ctx, novel.nId)
        }

        fun start(ctx: Context, id: Long) {
            ctx.startActivity<NovelTextActivity>(Novel.KEY_ID to id)
        }

        fun start(ctx: Context, novel: Novel, index: Int) {
            ctx.startActivity<NovelTextActivity>(
                    Novel.KEY_ID to novel.nId,
                    "index" to index.toJson(App.gson)
            )
        }
    }

    private lateinit var alertDialog: AlertDialog
    private lateinit var progressDialog: ProgressDialog
    lateinit var presenter: NovelTextPresenter
    private var chaptersAsc: List<NovelChapter> = listOf()
    private var navigation: NovelTextNavigation? = null
    private lateinit var reader: INovelReader
    // 缓存传入的索引，阅读器准备好后跳到这一章，-1表示最后一章，
    private var index: Int? = null
    private var _novel: Novel? = null
    private var novel: Novel
        get() = _novel.notNullOrReport()
        set(value) {
            _novel = value
        }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        _novel ?: return
        outState.apply {
            // pause时有本地持久化阅读进度，
            // 不是很清楚要否必要存在state里，
            // 防止的是android恢复了intent的数据，初始化阅读器时就跳到intent持有的index章节了，
            // onCreate先读取state里的index，存在就无视intent,
            putString("index", novel.readAtChapterIndex.toJson(App.gson))
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        alertDialog = AlertDialog.Builder(this).create()
        progressDialog = ProgressDialog(this)

        val id = intent?.getLongExtra(cc.aoeiuv020.panovel.data.entity.Novel.KEY_ID, -1L)
        debug { "receive id: $id" }
        if (id == null || id == -1L) {
            Reporter.unreachable()
            finish()
            return
        }
        title = id.toString()

        // TODO: 进来时就取消有新章节的通知，cancel notify,

        // 进度，读取顺序， savedInstanceState > intent > DataManager
        // intent传入的index，activity死了再开，应该会恢复这个intent, 不能让intent覆盖了死前的阅读进度，
        // 用getSerializableExtra读Int不需要默认值，
        // 出现过存在savedInstanceState但是getString("index")为null的不明状况，干脆都加问号?,
        index = savedInstanceState?.run { getString("index")?.toBean<Int>(App.gson) }
                ?: intent.getStringExtra("index")?.toBean(App.gson)

        presenter = NovelTextPresenter(id)

        loading(progressDialog, R.string.novel_chapters)
        presenter.attach(this)
        // 进去后根据id得到小说对象，
        // 只查询数据库，认为很快，所以不考虑没有小说对象时的用户操作，
        presenter.start()
    }

    // 不能lambda， lambda不能this调用Runnable自己，
    private val autoSaveReadStatusRunnable = object : Runnable {
        override fun run() {
            // 查询到小说对象才会开始线程，所以这里novel对象必然存在，
            presenter.saveReadStatus(novel)
            // 循环调用自己，
            handler.postDelayed(this, TimeUnit.SECONDS.toMillis(1) * ReaderSettings.autoSaveReadStatus)
        }
    }

    fun showNovel(novel: Novel) {
        this.novel = novel
        initReader(novel)
        navigation = NovelTextNavigation(this, novel, nav_view)
        try {
            urlTextView.text = DataManager.getDetailUrl(novel)
        } catch (e: Exception) {
            val message = "获取小说《${novel.name}》<${novel.site}, ${novel.detail}>详情页地址失败，"
            // 按理说每个网站的extra都是设计好的，可以得到完整地址的，
            Reporter.post(message, e)
            error(message, e)
            showError(message, e)
        }
        urlBar.setOnClickListener {
            // urlTextView只显示完整地址，以便点击打开，
            browse(urlTextView.text.toString())
        }
        if (ReaderSettings.autoSaveReadStatus > 0) {
            // 启动自动保存阅读进度循环，
            handler.post(autoSaveReadStatusRunnable)
        } else {
            // 进来就至少更新一次阅读时间，确保退到书架页查询时这本小说的阅读时间是最新，
            // 退出时还有更新一次，
            presenter.saveReadStatus(novel)
        }

        cancelNotify(novel.nId.toInt())

        presenter.requestChapters(novel)
    }

    private val contentRequester: TextRequester = object : TextRequester {
        override fun request(index: Int, refresh: Boolean): List<String> {
            return presenter.requestContent(novel, chaptersAsc[index], refresh)
        }
    }

    private fun initReader(novel: Novel) {
        reader = Readers.getReader(this, novel.name,
                flContent, contentRequester, ReaderSettings.makeReaderConfig()).apply {
            menuListener = object : MenuListener {
                override fun hide() {
                    this@NovelTextActivity.hide()
                }

                override fun show() {
                    this@NovelTextActivity.show()
                }

                override fun toggle() {
                    this@NovelTextActivity.toggle()
                }
            }
            readingListener = object : ReadingListener {
                override fun onReading(chapter: Int, text: Int) {
                    // 阅读时退出全屏，
                    hide()
                    if (chapter in chaptersAsc.indices) {
                        onChapterSelected(chapter)
                        novel.readAtTextIndex = text
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (ReaderSettings.backPressOutOfFullScreen && !mVisible) {
            show()
        } else {
            super.onBackPressed()
        }
    }

    override fun show() {
        super.show()
        navigation?.reset(reader.maxTextProgress, reader.textProgress)
    }

    fun previousChapter() {
        selectChapter(reader.currentChapter - 1)
    }

    fun nextChapter() {
        selectChapter(reader.currentChapter + 1)
    }

    fun setTextProgress(progress: Int) {
        reader.textProgress = progress
    }

    fun refreshCurrentChapter() {
        reader.refreshCurrentChapter()
    }

    /**
     * 切换动画时调用,
     * 重置阅读器，
     */
    private fun resetReader() {
        reader.onDestroy()
        flContent.removeAllViews() // 多余，上面已经移除，
        initReader(novel)
        showChaptersAsc(chaptersAsc)
    }

    fun setAnimationSpeed(animationSpeed: Float) {
        reader.config.animationSpeed = animationSpeed
    }

    fun setAnimationMode(animationMode: AnimationMode, oldAnimationMode: AnimationMode) {
        debug { "setAnimationMode $oldAnimationMode to $animationMode" }
        if ((animationMode == AnimationMode.SIMPLE && oldAnimationMode != AnimationMode.SIMPLE)
                || (animationMode != AnimationMode.SIMPLE && oldAnimationMode == AnimationMode.SIMPLE)) {
            resetReader()
        } else {
            reader.config.animationMode = animationMode
        }
    }

    fun setMargins(margins: Margins, name: ReaderConfigName) {
        when (name) {
            ContentMargins -> reader.config.contentMargins = margins
            PaginationMargins -> reader.config.paginationMargins = margins
            TimeMargins -> reader.config.timeMargins = margins
            BatteryMargins -> reader.config.batteryMargins = margins
            BookNameMargins -> reader.config.bookNameMargins = margins
            ChapterNameMargins -> reader.config.chapterNameMargins = margins
            else -> {
            }
        }
    }

    /**
     * 选择图片，
     * 选择后修改当前背景图，但不保存，
     */
    private fun requestBackgroundImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    fun requestFont() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, 1)
    }

    fun resetFont() {
        ReaderSettings.font = null
        setFont(null)
    }

    private var cacheUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            0 -> data?.data?.let { uri ->
                try {
                    // 不在这里做永久保存，
                    reader.config.backgroundImage = uri
                } catch (e: SecurityException) {
                    error("读取背景图失败", e)
                    cacheUri = uri
                    ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE), requestCode)
                } catch (e: FileNotFoundException) {
                    // 不明原因，
                    // https://bugly.qq.com/v2/crash-reporting/crashes/be0d684a75/1705?pid=1
                    error("神奇，图片找不到，", e)
                }
            }
            1 -> data?.data?.let { uri ->
                try {
                    ReaderSettings.font = uri
                    setFont(ReaderSettings.tfFont)
                } catch (e: SecurityException) {
                    error("读取字体失败", e)
                    cacheUri = uri
                    ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE), requestCode)
                } catch (e: FileNotFoundException) {
                    // 不明原因，
                    // https://bugly.qq.com/v2/crash-reporting/crashes/be0d684a75/1705?pid=1
                    error("神奇，图片找不到，", e)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            0 -> cacheUri?.let { uri ->
                try {
                    // 不在这里做永久保存，
                    reader.config.backgroundImage = uri
                } catch (e: SecurityException) {
                    error("读取背景图还是失败", e)
                    cacheUri = null
                }
            }
            1 -> cacheUri?.let { uri ->
                try {
                    ReaderSettings.font = uri
                    setFont(ReaderSettings.tfFont)
                } catch (e: SecurityException) {
                    error("读取字体还是失败", e)
                    cacheUri = null
                }
            }
        }
    }

    private fun setFont(font: Typeface?) {
        reader.config.font = font
    }

    fun setParagraphSpacing(progress: Int) {
        reader.config.paragraphSpacing = progress
    }

    fun setLineSpacing(progress: Int) {
        reader.config.lineSpacing = progress
    }

    fun setMessageSize(textSize: Int) {
        reader.config.messageSize = textSize
    }

    fun setTextSize(textSize: Int) {
        reader.config.textSize = textSize
    }

    override fun onDestroy() {
        // 删除循环自动保存阅读自动的回调，
        handler.removeCallbacks(autoSaveReadStatusRunnable)
        // 删除可能存在的循环通知下载状态的回调，
        handler.removeCallbacks(downloadingRunnable)
        if (::presenter.isInitialized) {
            presenter.detach()
        }
        if (::reader.isInitialized) {
            reader.onDestroy()
        }
        super.onDestroy()
    }

    /**
     * 这个是代码主动选择章节，
     */
    private fun selectChapter(index: Int) {
        if (index !in chaptersAsc.indices) {
            // 超出范围直接无视，
            return
        }
        reader.currentChapter = index
        onChapterSelected(index)
    }

    /**
     * 这个无论是用户翻页切换章节还是其他跳章节都要调用，
     * 改变界面上显示的内容，
     */
    private fun onChapterSelected(index: Int) {
        debug { "onChapterSelected $index" }
        // 可能重复赋值，但是无所谓了，
        novel.readAt(index, chaptersAsc)
        if (index in chaptersAsc.indices) {
            val chapter = chaptersAsc[index]
            title = "${novel.name} - ${chapter.name}"
            urlTextView.text = DataManager.getContentUrl(novel, chapter)
        }
    }

    fun showError(message: String, e: Throwable) {
        progressDialog.dismiss()
        alertError(alertDialog, message, e)
        show()
    }

    /**
     * 给定id找不到小说也就不用继续了，
     */
    @Suppress("UNUSED_PARAMETER")
    fun showNovelNotFound(message: String, e: Throwable) {
        // 两个参数已经打过日志了，这里就不重复了，
        toast("小说不存在，")
        finish()
    }

    fun showChaptersAsc(chaptersAsc: List<NovelChapter>) {
        debug { "chapters loaded ${chaptersAsc.size}" }
        this.chaptersAsc = chaptersAsc
        if (chaptersAsc.isEmpty()) {
            // 真有小说空章节的，不知道怎么回事，
            // https://m.qidian.com/book/2346657
            alert(alertDialog, R.string.novel_not_support)
            // 无法浏览的情况显示状态栏标题栏导航栏，方便离开，
            show()
            // 进度条可以收起来了，
            progressDialog.dismiss()
            return
        }
        index?.let {
            // 以防万一index再被使用，不知道是否必要，
            index = null
            // 支持跳到最后一章，
            val chapterIndex = if (it < 0) {
                chaptersAsc.lastIndex
            } else {
                it
            }
            // 如果有传入章节索引，就修改novel里存的阅读进度，
            novel.readAt(chapterIndex, chaptersAsc)
            // 章节内进度改成本章开头，
            novel.readAtTextIndex = 0
        }
        if (novel.readAtChapterIndex > chaptersAsc.lastIndex
                || novel.readAtChapterIndex < 0) {
            // 以防万一，比如更新后章节反而减少了，
            // 总觉得还有其他可能，但是找不到，
            // 主要是太乱了，找不到到底什么情况会出现-1,
            novel.readAt(chaptersAsc.lastIndex, chaptersAsc)
        }
        onChapterSelected(novel.readAtChapterIndex)
        progressDialog.dismiss()
        doAsync({ e ->
            val message = "处理小说章节列表失败，"
            Reporter.post(message, e)
            error(message, e)
            runOnUiThread {
                showError(message, e)
            }
        }) {
            val chapterList = chaptersAsc.map { it.name }
            uiThread {
                debug {
                    "load status: <${novel.run { "$readAtChapterIndex.$readAtChapterName/$readAtTextIndex" }}"
                }
                reader.chapterList = chapterList
                reader.currentChapter = novel.readAtChapterIndex
                reader.textProgress = novel.readAtTextIndex
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // 要是没得到小说对象，避免进入后面的保存进度，
        _novel ?: return
        // 得到小说novel对象后，进度始终保存在其中，
        // 这里刷一下数据库就好，
        presenter.saveReadStatus(novel)
    }

    private fun refineSearch() {
        FuzzySearchActivity.start(this, novel)
    }

    fun refreshChapterList() {
        loading(progressDialog, R.string.novel_chapters)
        debug {
            "refreshChapterList"
        }
        if (_novel == null) {
            // 以防万一，太乱了，
            return
        }
        // 保存一下的进度，
        if (::reader.isInitialized) {
            novel.readAt(reader.currentChapter, chaptersAsc)
            novel.readAtTextIndex = reader.textProgress
            debug {
                "save status: <${novel.run { "$readAtChapterIndex.$readAtChapterName/$readAtTextIndex" }}"
            }
        }
        presenter.refreshChapterList(novel)
    }

    /**
     * 上一对配色，文字色/背景（图|色），
     */
    fun lastColorScheme() {
        // 交换设置中的两套配色，
        tempColorPref.textColor = ReaderSettings.lastTextColor
        tempColorPref.backgroundColor = ReaderSettings.lastBackgroundColor
        tempColorPref.backgroundImage = ReaderSettings.lastBackgroundImage
        ReaderSettings.lastTextColor = ReaderSettings.textColor
        ReaderSettings.lastBackgroundColor = ReaderSettings.backgroundColor
        ReaderSettings.lastBackgroundImage = ReaderSettings.backgroundImage
        ReaderSettings.textColor = tempColorPref.textColor
        ReaderSettings.backgroundColor = tempColorPref.backgroundColor
        ReaderSettings.backgroundImage = tempColorPref.backgroundImage
        // 切换到上次的配色，已经是现在的配色了，
        // 先设置图片，因为每次设置都会刷新全部，图片可能Uri存在但是文件已经被删除了，
        reader.config.backgroundImage = ReaderSettings.backgroundImage
        reader.config.textColor = ReaderSettings.textColor
        reader.config.backgroundColor = ReaderSettings.backgroundColor
    }

    private val tempColorPref = object : Pref {
        override val name: String
            get() = "TempColor"
        // 默认值没有用，
        var textColor: Int by Delegates.int(0xff000000.toInt())
        var backgroundColor: Int by Delegates.int(0xffffe3aa.toInt())
        var backgroundImage: Uri? by Delegates.uri()
    }

    /**
     * 弹出对话框选择配色，文字色/背景（图|色），
     */
    @SuppressLint("InflateParams")
    fun selectColorScheme() {
        if (!::reader.isInitialized) {
            // 以防万一，
            return
        }
        // 备份当前的配色，
        // 对话框中如果选择取消，就恢复临时配色，
        // 如果确定，临时配色保存到last上次设置的配色，
        tempColorPref.textColor = ReaderSettings.textColor
        tempColorPref.backgroundColor = ReaderSettings.backgroundColor
        tempColorPref.backgroundImage = ReaderSettings.backgroundImage
        AlertDialog.Builder(ctx).apply {
            setTitle(R.string.select_color_scheme)
            val view = layoutInflater.inflate(R.layout.dialog_select_color_scheme, null)
            view.tvBackgroundImage.setOnClickListener {
                requestBackgroundImage()
            }
            view.tvInputBackgroundColor.setOnClickListener {
                changeColor(reader.config.backgroundColor) { color ->
                    reader.config.backgroundImage = null
                    reader.config.backgroundColor = color
                }
            }
            view.llBackgroundColor.apply {
                val listener = View.OnClickListener {
                    val color = ((it as ImageView).drawable as ColorDrawable).color
                    reader.config.backgroundImage = null
                    reader.config.backgroundColor = color
                }
                (getChildAt(0) as ImageView).apply {
                    setImageDrawable(ColorDrawable(ReaderSettings.backgroundColor))
                    setOnClickListener(listener)
                }
                (getChildAt(1) as ImageView).apply {
                    setImageDrawable(ColorDrawable(ReaderSettings.lastBackgroundColor))
                    setOnClickListener(listener)
                }
                for (index in 2 until 7) {
                    val ivColor = getChildAt(index)
                    ivColor.setOnClickListener(listener)
                }
            }
            view.tvInputTextColor.setOnClickListener {
                changeColor(reader.config.textColor) { color ->
                    reader.config.textColor = color
                }
            }
            view.llTextColor.apply {
                val listener = View.OnClickListener {
                    val color = ((it as ImageView).drawable as ColorDrawable).color
                    reader.config.textColor = color
                }
                (getChildAt(0) as ImageView).apply {
                    setImageDrawable(ColorDrawable(ReaderSettings.textColor))
                    setOnClickListener(listener)
                }
                (getChildAt(1) as ImageView).apply {
                    setImageDrawable(ColorDrawable(ReaderSettings.lastTextColor))
                    setOnClickListener(listener)
                }
                for (index in 2 until 7) {
                    val ivColor = getChildAt(index)
                    ivColor.setOnClickListener(listener)
                }
            }
            setView(view)
            setPositiveButton(android.R.string.yes) { _, _ ->
                // 确定，临时配色保存到last上次设置的配色，
                ReaderSettings.lastTextColor = tempColorPref.textColor
                ReaderSettings.lastBackgroundColor = tempColorPref.backgroundColor
                ReaderSettings.lastBackgroundImage = tempColorPref.backgroundImage
                // 当前配色保存，
                ReaderSettings.textColor = reader.config.textColor
                ReaderSettings.backgroundColor = reader.config.backgroundColor
                ReaderSettings.backgroundImage = reader.config.backgroundImage
            }
            setNegativeButton(android.R.string.cancel) { _, _ ->
                // 选择取消，就恢复临时配色，全程没有操作ReaderSettings永久保存的设置，
                reader.config.textColor = tempColorPref.textColor
                setBackground(tempColorPref.backgroundColor, tempColorPref.backgroundImage)
            }
        }.setCancelable(false).create().also {
            // 去除对话框的灰背景，
            it.window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }.show()
    }

    private fun setBackground(color: Int, image: Uri?) {
        reader.config.backgroundColor = color
        if (image != null) {
            // 避免重复设置覆盖背景色，
            reader.config.backgroundImage = image
        }
    }

    private fun detail() {
        NovelDetailActivity.start(this, novel)
    }

    fun showContents() {
        doAsync({ e ->
            val message = "加载小说正文缓存列表失败，"
            Reporter.post(message, e)
            error(message, e)
            runOnUiThread {
                showError(message, e)
            }
        }) {
            // 虽然给了异步，但还是要快，因为没给任何提示，
            // 查询小说已经缓存的章节列表，
            val cachedList = DataManager.novelContentsCached(novel)
            uiThread {
                AlertDialog.Builder(it)
                        .setTitle(R.string.contents)
                        .setAdapter(NovelContentsAdapter(it, novel, chaptersAsc, cachedList)) { _, index ->
                            selectChapter(index)
                        }.create().apply {
                            listView.isFastScrollEnabled = true
                            listView.post {
                                listView.setSelection(novel.readAtChapterIndex)
                            }
                        }.show()
            }
        }
    }

    /**
     * 用于延迟通知下载过程，
     * 以及循环通知保存阅读进度，
     */
    private val handler: Handler = Handler()

    /**
     * 安卓通知是不按顺序的，使用唯一runnable确保顺序，
     */
    private val downloadingRunnable = object : Runnable {
        var exists = 0
        var downloads = 0
        var errors = 0
        var left = 0
        fun set(exists: Int, downloads: Int, errors: Int, left: Int) {
            this.exists = exists
            this.downloads = downloads
            this.errors = errors
            this.left = left
        }

        // 太早了Intent不能用，
        val nb: NotificationCompat.Builder by lazy {
            val intent = intentFor<MainActivity>()
            val pendingIntent = PendingIntent.getActivity(ctx, 0, intent, 0)
            @Suppress("DEPRECATION")
            // 用过时的通知，可以兼容api26,
            val notificationBuilder = NotificationCompat.Builder(ctx)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(true)
                    .setContentTitle(novel.name)
                    .setContentIntent(pendingIntent)
            notificationBuilder.apply {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    setLargeIcon(getBitmapFromVectorDrawable(R.drawable.ic_file_download))
                    setSmallIcon(R.mipmap.ic_launcher_round)
                } else {
                    setSmallIcon(R.drawable.ic_file_download)
                }
            }
            notificationBuilder
        }
        // System services not available to Activities before onCreate()
        val manager by lazy { NotificationManagerCompat.from(ctx) }

        fun complete() {
            // 完成时停止通知循环，
            // 可能正在通知？删除后又调用了延迟100ms的通知，覆盖了完成的通知，
            // 不对啊，这是在主线程的操作，只有一个线程，那就是其中一个通知在阻塞中，这里通知完了才到它，
            // 不管了，这多线程实在恶心，给个一秒的延迟，确保最后通知，
            handler.removeCallbacks(this)
            handler.postDelayed({
                nb.setContentText(getString(R.string.download_complete_placeholder, exists, downloads, errors))
                        .setProgress(0, 0, false)
                manager.notify(1, nb.build())
            }, 1000)
        }

        override fun run() {
            val progress = ((exists + downloads + errors).toFloat() / ((exists + downloads + errors) + left) * 100).toInt()
            nb.setContentText(getString(R.string.downloading_placeholder, exists, downloads, errors, left))
                    .setProgress(100, progress, false)
            manager.notify(1, nb.build())
            // 100ms通知一次，避免过快，
            handler.postDelayed(this, 100)
        }
    }

    fun download() {
        val index = reader.currentChapter
        val count = GeneralSettings.downloadCount
        when {
            count < 0 -> alert {
                titleResource = R.string.download_chapters_count
                val layout = View.inflate(ctx, R.layout.dialog_editor, null)
                customView = layout
                val etCount = layout.editText.apply {
                    inputType = InputType.TYPE_CLASS_NUMBER
                    setText(50.toString())
                }
                neutralPressed(R.string.all) {
                    presenter.download(novel, index, Int.MAX_VALUE)
                }
                yesButton {
                    presenter.download(novel, index, etCount.text.toString().toIntOrNull() ?: 0)
                }
                cancelButton { }
            }.show()
            count == 0 -> presenter.download(novel, index, Int.MAX_VALUE)
            else -> presenter.download(novel, index, count)
        }
    }

    fun showDownloadStart(left: Int) {
        downloadingRunnable.left = left
        // 开始通知循环，
        handler.post(downloadingRunnable)
    }

    fun showDownloadError() {
        // 下载失败直接停止通知循环，
        handler.removeCallbacks(downloadingRunnable)
    }

    fun showDownloading(exists: Int, downloads: Int, errors: Int, left: Int) {
        // 更新数据，下次通知自己读取，
        downloadingRunnable.set(exists, downloads, errors, left)
    }

    fun showDownloadComplete(exists: Int, downloads: Int, errors: Int) {
        downloadingRunnable.set(exists, downloads, errors, 0)
        downloadingRunnable.complete()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (ReaderSettings.volumeKeyScroll) {
            when (keyCode) {
                KeyEvent.KEYCODE_VOLUME_DOWN -> scrollNext()
                KeyEvent.KEYCODE_VOLUME_UP -> scrollPrev()
                else -> return super.onKeyDown(keyCode, event)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (ReaderSettings.volumeKeyScroll) {
            return when (keyCode) {
                KeyEvent.KEYCODE_VOLUME_DOWN -> true
                KeyEvent.KEYCODE_VOLUME_UP -> true
                else -> super.onKeyUp(keyCode, event)
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun scrollNext() {
        reader.scrollNext()
    }

    private fun scrollPrev() {
        reader.scrollPrev()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refresh -> refreshChapterList()
            R.id.search -> refineSearch()
            R.id.detail -> detail()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_text, menu)
        return true
    }
}

