package cc.aoeiuv020.panovel.api.base

import cc.aoeiuv020.base.jar.matches
import cc.aoeiuv020.base.jar.notNull
import cc.aoeiuv020.base.jar.pick
import cc.aoeiuv020.panovel.api.*
import okhttp3.*
import okhttp3.internal.http.HttpMethod
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.net.URLEncoder
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by AoEiuV020 on 2018.05.20-16:26:44.
 */
// MemberVisibilityCanBePrivate, 有不少预先准备的成员，可能暂时没有被使用，但是不能private,
// ClassName, LocalVariableName, 内部使用的类和变量通通下划线_开头，可能不符合规范，
@Suppress("ClassName", "LocalVariableName", "MemberVisibilityCanBePrivate")
abstract class DslJsoupNovelContext : JsoupNovelContext() {
    /*
    *************** member ***************
     */
    var bookIdRegex: String? = firstIntPattern
    // 应对一些复杂的正则，可能不得不用到多个组，
    var bookIdIndex: Int = 0
    // 有的网站地址有分一级出来对bookId取模，
    // 比如“https://www.gxwztv.com/55/55886/”，就是除以1000，
    // 章节列表页和正文页都可能有，
    var detailDivision: Int? = null
    // 向下传递，
        set(value) {
            field = value
            if (chapterDivision == null) {
                chapterDivision = value
            }
        }
    /**
     * 详情页地址模板，String.format形式，"/book/%s/"
     */
    var detailPageTemplate: String? = null
        /**
         * 详情页地址模板默认同时赋值给目录页，
         */
        set(value) {
            field = value
            if (chaptersPageTemplate == null) {
                chaptersPageTemplate = value
            }
        }
    // set时不传到contentDivision，一般正文地址要另外处理，
    var chapterDivision: Int? = null
    /**
     * 目录页地址模板，String.format形式，"/book/%s/"
     * 目录英文是contents，但是和正文content接近，干脆用chapters,
     */
    var chaptersPageTemplate: String? = null
    var contentDivision: Int? = null
    /**
     * 正文页地址模板，String.format形式，"/book/%s/"
     */
    var contentPageTemplate: String? = null
    // 一般网站都是bookId/chapterId的形式，合起来处理，
    var bookIdWithChapterIdRegex: String? = firstTwoIntPattern
    var bookIdWithChapterIdIndex: Int = 0

    override var charset: String? = null
    override var enabled: Boolean = true

    /*
    *************** url ***************
     */
    /**
     * 以下几个都是为了得到真实地址，毕竟extra现在支持各种随便写法，
     * 要快，要能在ui线程使用，不能有网络请求，
     */
    /**
     * 有继承给定正则就用上，没有找到就直接返回传入的数据，可能已经是bookId了，
     */
    protected open fun findBookId(extra: String): String = if (bookIdRegex != null) {
        try {
            extra.pick(bookIdRegex.notNull())[bookIdIndex]
        } catch (e: Exception) {
            extra
        }
    } else {
        extra
    }

    /**
     * 查找章节id, 是包括小说id的，
     * 有继承给定正则就用上，没有找到就直接返回传入的数据，可能已经是chapterId了，
     */
    protected open fun findBookIdWithChapterId(extra: String): String = if (bookIdWithChapterIdRegex != null) {
        try {
            extra.pick(bookIdWithChapterIdRegex.notNull())[bookIdWithChapterIdIndex]
        } catch (e: Exception) {
            extra
        }
    } else {
        extra
    }

    override fun getNovelItem(url: String): NovelItem = getNovelDetail(findBookId(url)).novel
    override fun getNovelDetailUrl(extra: String): String {
        val path = detailPageTemplate?.let { template ->
            val bookId = findBookId(extra)
            detailDivision?.let { division ->
                template.format(bookId.toInt() / division, bookId)
            } ?: template.format(bookId)
        } ?: extra
        return absUrl(path)
    }

    protected open fun getNovelChapterUrl(extra: String): String {
        val path = chaptersPageTemplate?.let { template ->
            val bookId = findBookId(extra)
            chapterDivision?.let { division ->
                template.format(bookId.toInt() / division, bookId)
            } ?: template.format(bookId)
        } ?: extra
        return absUrl(path)
    }

    override fun getNovelContentUrl(extra: String): String {
        return if (::getNovelContentUrlLambda.isInitialized) {
            absUrl(getNovelContentUrlLambda(extra))
        } else {
            val path = contentPageTemplate?.let { template ->
                val bookId = findBookId(extra)
                val chapterId = findBookIdWithChapterId(extra)
                contentDivision?.let { division ->
                    template.format(bookId.toInt() / division, chapterId)
                } ?: template.format(chapterId)
            } ?: extra
            return absUrl(path)

        }
    }

    private lateinit var getNovelContentUrlLambda: (String) -> String
    fun getNovelContentUrl(lambda: (String) -> String) {
        getNovelContentUrlLambda = lambda
    }

    /*
    *************** cookie ***************
     */
    private lateinit var cookieFilterLambda: (HttpUrl, MutableList<Cookie>) -> MutableList<Cookie>

    protected fun cookieFilter(init: _Cookie.() -> Unit) {
        cookieFilterLambda = { url, cookies ->
            _Cookie(url, cookies).also(init)
                    .filter()
        }
    }

    override fun cookieFilter(url: HttpUrl, cookies: MutableList<Cookie>): MutableList<Cookie> {
        return if (::cookieFilterLambda.isInitialized) {
            cookieFilterLambda(url, cookies)
        } else {
            super.cookieFilter(url, cookies)
        }
    }

    @Suppress("unused")
    protected class _Cookie(
            val httpUrl: HttpUrl,
            val cookies: MutableList<Cookie>
    ) {
        fun removeAll(predicate: (Cookie) -> Boolean) {
            cookies.removeAll(predicate)
        }

        fun filter(): MutableList<Cookie> = cookies
    }

    /*
    *************** site ***************
     */
    override lateinit var site: NovelSite

    /**
     * site这块是直接跑一遍存起结果，
     * 其他块都是存起lambda用的时候才调用，
     */
    protected fun site(init: _NovelSite.() -> Unit) {
        site = _NovelSite().run {
            init()
            createNovelSite()
        }
    }

    protected class _NovelSite {
        lateinit var name: String
        lateinit var baseUrl: String
        lateinit var logo: String
        fun createNovelSite(): NovelSite = NovelSite(
                name = name,
                baseUrl = baseUrl,
                logo = logo
        )
    }

    /*
    *************** search ***************
     */
    override fun searchNovelName(name: String): List<NovelItem> =
            _Search(name).initSearch(name)

    private lateinit var initSearch: _Search.(String) -> List<NovelItem>
    protected fun search(init: _Search.(String) -> List<NovelItem>) {
        initSearch = init
    }

    protected inner class _Search(name: String)
        : _Requester(name) {

        fun document(document: Document = parse(call.notNull(), charset),
                     init: _NovelItemListParser.() -> Unit): List<NovelItem> =
                _NovelItemListParser(document).also(init).parse()
    }

    protected inner class _NovelItemListParser(root: Element)
        : _Parser<List<NovelItem>>(root) {
        lateinit var novelItemList: List<NovelItem>
        /**
         * 有个模板的搜索是可能跳到详情页，就只有一个搜索结果，
         * @param detailRegex 匹配详情页的正则，
         */
        fun single(detailRegex: String, init: _NovelItemParser.() -> Unit) {
            if (root.ownerPath().matches(detailRegex)) {
                single(init)
            }
        }
        fun single(init: _NovelItemParser.() -> Unit) {
            novelItemList = listOf(
                    _NovelItemParser(root).run {
                        // 搜索结果只有一个，认为是跳转到详情页了，没法用items, 默认直接从地址找bookId,
                        extra = findBookId(root.ownerDocument().location())
                        init()
                        parse()
                    }
            )
        }

        fun itemsIgnoreFailed(query: String, parent: Element = root, init: _NovelItemParser.() -> Unit) {
            if (::novelItemList.isInitialized) {
                // 调用single方法生效了，也就是跳到详情页了，
                return
            }
            novelItemList = parent.requireElements(query).mapNotNull {
                try {
                    _NovelItemParser(it).run {
                        init()
                        parse()
                    }
                } catch (e: Exception) {
                    null
                }
            }
        }

        fun items(query: String, parent: Element = root, init: _NovelItemParser.() -> Unit) {
            if (::novelItemList.isInitialized) {
                // 调用single方法生效了，也就是跳到详情页了，
                return
            }
            novelItemList = parent.requireElements(query).map {
                _NovelItemParser(it).run {
                    init()
                    parse()
                }
            }
        }

        override fun parse(): List<NovelItem> = novelItemList
    }

    /*
    *************** detail ***************
     */
    override fun getNovelDetail(extra: String): NovelDetail =
            _Detail(extra).initDetail(extra)

    private lateinit var initDetail: _Detail.(String) -> NovelDetail
    protected fun detail(init: _Detail.(String) -> NovelDetail) {
        initDetail = init
    }

    protected inner class _Detail(extra: String) : _Requester(extra) {
        fun document(
                document: Document = parse(call
                        ?: connect(getNovelDetailUrl(extra)), charset),
                init: _NovelDetailParser.() -> Unit
        ): NovelDetail =
                _NovelDetailParser(extra, document).also(init).parse()
    }

    protected inner class _NovelDetailParser(
            // 有需要的话这个可以改public,
            detailExtra: String,
            root: Element
    ) : _Parser<NovelDetail>(root) {
        private val _novelDetail = _NovelDetail()

        init {
            // 默认直接将详情页的extra传给目录页，
            // 通常双方都是只需要一个bookId,
            _novelDetail.extra = findBookId(detailExtra)
        }

        var novel: NovelItem?
            get() = _novelDetail.novel
            set(value) {
                _novelDetail.novel = value
            }

        fun novel(init: _NovelItemParser.() -> Unit) {
            novel = _NovelItemParser(root).run {
                // 默认流程，findBookId(detailExtra) -> _novelDetail.extra -> novel.extra,
                extra = this@_NovelDetailParser._novelDetail.extra
                init()
                parse()
            }
        }

        var image: String?
            get() = _novelDetail.image
            set(value) {
                _novelDetail.image = value
            }

        fun image(query: String, parent: Element = root, block: (Element) -> String = {
            it.absDataOriginal().takeIf(String::isNotBlank) ?: it.absSrc()
        }) {
            image = parent.requireElement(query = query, name = TAG_IMAGE, block = block)
        }

        var update: Date?
            get() = _novelDetail.update
            set(value) {
                _novelDetail.update = value
            }

        fun update(query: String, parent: Element = root, format: String, block: (Element) -> String = {
            // kotlin的trim有包括utf8的特殊的空格，和java的trim不重复，
            it.text().trim()
        }) =
                update(query = query, parent = parent) {
                    val updateString = block(it)
                    val sdf = SimpleDateFormat(format, Locale.CHINA)
                    sdf.parse(updateString)
                }

        fun update(query: String, parent: Element = root, block: (Element) -> Date) {
            update = parent.getElement(query = query, block = block)
        }

        var introduction: String?
            get() = _novelDetail.introduction
            set(value) {
                _novelDetail.introduction = value
            }

        fun introduction(
                query: String,
                parent: Element = root,
                block: (Element) -> String = { it.textList().joinToString("\n") }
        ) {
            introduction = parent.getElement(query = query, block = block)
        }

        var extra: String?
            get() = _novelDetail.extra
            set(value) {
                _novelDetail.extra = value
            }

        fun extra(query: String, parent: Element = root, block: (Element) -> String = { it.path() }) {
            extra = parent.requireElement(query = query, name = TAG_CHAPTER_PAGE, block = block)
        }

        override fun parse(): NovelDetail = _novelDetail.createNovelDetail()

        private inner class _NovelDetail {
            var novel: NovelItem? = null
            var image: String? = null
            var update: Date? = null
            var introduction: String? = null
            var extra: String? = null
            fun createNovelDetail() = NovelDetail(
                    novel.notNull(),
                    image.notNull(),
                    update,
                    introduction.toString(),
                    extra.notNull()
            )
        }
    }

    /*
       *************** chapters ***************
        */

    override fun getNovelChaptersAsc(extra: String): List<NovelChapter> =
            _Chapters(extra).initChapters(extra)

    private lateinit var initChapters: _Chapters.(String) -> List<NovelChapter>
    protected fun chapters(init: _Chapters.(String) -> List<NovelChapter>) {
        initChapters = init
    }

    protected inner class _Chapters(extra: String) : _Requester(extra) {
        fun document(
                document: Document = parse(call
                        ?: connect(getNovelChapterUrl(extra)), charset),
                init: _NovelChapterListParser.() -> Unit
        ): List<NovelChapter> =
                _NovelChapterListParser(document).also(init).parse()
    }

    protected inner class _NovelChapterListParser(root: Element)
        : _Parser<List<NovelChapter>>(root) {
        private lateinit var novelChapterList: List<NovelChapter>

        @SuppressWarnings("SimpleDateFormat")
        fun lastUpdate(query: String, parent: Element = root, format: String, block: (Element) -> String = { it.text() }) =
                lastUpdate(query = query, parent = parent) {
                    val updateString = block(it)
                    val sdf = SimpleDateFormat(format)
                    sdf.parse(updateString)
                }

        fun lastUpdate(query: String, parent: Element = root, block: (Element) -> Date) {
            novelChapterList.lastOrNull()?.update = parent.getElement(query = query, block = block)
        }

        fun items(query: String, parent: Element = root, init: _NovelChapterParser.() -> Unit = {
            name = root.text()
            if (extra == null) {
                // 默认从该元素的href路径中找到chapterId，用于拼接章节正文地址，
                extra = findBookIdWithChapterId(root.absHref())
            }
        }) {
            novelChapterList = parent.requireElements(query, name = TAG_CHAPTER_LINK).mapNotNull {
                // 解析不通过的章节直接无视，不抛异常，
                tryOrNul {
                    _NovelChapterParser(it).run {
                        init()
                        parse()
                    }
                }
            }
        }

        override fun parse(): List<NovelChapter> = novelChapterList
    }

    protected inner class _NovelChapterParser(root: Element)
        : _Parser<NovelChapter>(root) {
        private val _novelChapter = _NovelChapter()
        override fun parse(): NovelChapter? = tryOrNul {
            _novelChapter.createNovelChapter()
        }

        var name: String?
            get() = _novelChapter.name
            set(value) {
                _novelChapter.name = value
            }

        fun name(query: String, parent: Element = root, block: (Element) -> String = { it.text() }) {
            name = parent.requireElement(query, block = block)
        }

        var extra: String?
            get() = _novelChapter.extra
            set(value) {
                _novelChapter.extra = value
            }

        fun extra(query: String, parent: Element = root, block: (Element) -> String = { it.path() }) {
            extra = parent.requireElement(query, block = block)
        }

        var update: Date?
            get() = _novelChapter.update
            set(value) {
                _novelChapter.update = value
            }

        @SuppressWarnings("SimpleDateFormat")
        fun update(query: String, parent: Element = root, format: String, block: (Element) -> String = { it.text() }) =
                update(query = query, parent = parent) {
                    val updateString = block(it)
                    val sdf = SimpleDateFormat(format)
                    sdf.parse(updateString)
                }

        fun update(query: String, parent: Element = root, block: (Element) -> Date) {
            update = parent.getElement(query = query, block = block)
        }

        private inner class _NovelChapter {
            var name: String? = null
            var extra: String? = null
            var update: Date? = null
            fun createNovelChapter(): NovelChapter = NovelChapter(
                    name = name.notNull(),
                    extra = extra.notNull(),
                    update = update
            )
        }
    }

    override fun getNovelContent(extra: String): List<String> =
            _Content(extra).initContent(extra)

    private lateinit var initContent: _Content.(String) -> List<String>
    protected fun content(init: _Content.(String) -> List<String>) {
        initContent = init
    }

    protected inner class _Content(extra: String) : _Requester(extra) {
        fun document(
                document: Document = parse(call
                        ?: connect(getNovelContentUrl(extra)), charset),
                init: _NovelContentParser.() -> Unit
        ): List<String> = _NovelContentParser(document).also(init).parse()
    }

    protected inner class _NovelContentParser(root: Element)
        : _Parser<List<String>>(root) {
        private lateinit var novelContent: List<String>
        // 查到的可以是一个元素，也可以是一列元素，
        fun items(query: String, parent: Element = root, block: (Element) -> List<String> = { it.textList() }) {
            novelContent = parent.requireElements(query, name = TAG_CONTENT).flatMap {
                block(it)
            }
        }

        override fun parse(): List<String> = novelContent
    }

    /*
    *************** novel ***************
     */

    protected inner class _NovelItemParser(root: Element) : _Parser<NovelItem>(root) {
        private val _novelItem = _NovelItem()
        var name: String?
            get() = _novelItem.name
            set(value) {
                _novelItem.name = value
            }

        fun name(query: String, parent: Element = root, block: (Element) -> String = { it.text() }) {
            name = parent.requireElement(query = query, name = TAG_NOVEL_NAME) {
                // 为了从列表中拿小说时方便，
                // 尝试从该元素中提取bookId，如果能成功，就不需要调用extra块，
                // 如果是详情页，在这前后传入详情页的extra都可以，不会被这里覆盖，
                if (_novelItem.extra == null && it.href().isNotBlank()) {
                    _novelItem.extra = findBookId(it.absHref())
                }
                block(it)
            }
        }

        var author: String?
            get() = _novelItem.author
            set(value) {
                _novelItem.author = value
            }

        fun author(query: String, parent: Element = root, block: (Element) -> String = { it.text() }) {
            author = parent.requireElement(query = query, name = TAG_AUTHOR_NAME, block = block)
        }

        var extra: String?
            get() = _novelItem.extra
            set(value) {
                _novelItem.extra = value
            }

        fun extra(query: String, parent: Element = root, block: (Element) -> String = { findBookId(it.path()) }) {
            extra = parent.requireElement(query = query, name = TAG_NOVEL_LINK, block = block)
        }

        override fun parse(): NovelItem = _novelItem.createNovelItem()

        private inner class _NovelItem {
            var site: String = this@DslJsoupNovelContext.site.name
            var name: String? = null
            var author: String? = null
            // extra不能给初值，搜索结果页面要有个自动判断填充extra的逻辑，
            var extra: String? = null

            fun createNovelItem() = NovelItem(
                    site,
                    name.notNull(),
                    author.notNull(),
                    extra.notNull()
            )
        }
    }

    /*
    *************** parser ***************
     */
    @DslTag
    protected abstract inner class _Parser<out T>(
            val root: Element
    ) {
        fun element(query: String, parent: Element = root) =
                parent.requireElement(query)

        abstract fun parse(): T?
    }

    /*
    *************** requester ***************
     */
    @DslTag
    protected abstract inner class _Requester(
            protected val extra: String
    ) {
        var call: Call? = null
        // 指定响应的编码，用于jsoup解析html时，
        // 不是参数的编码，参数在_Request里指定编码，
        var charset: String? = this@DslJsoupNovelContext.charset

        fun get(init: _Request.() -> Unit) {
            call = _Request().run {
                method = "GET"
                init()
                createCall()
            }
        }

        fun post(init: _Request.() -> Unit) {
            call = _Request().run {
                method = "POST"
                init()
                createCall()
            }
        }

        fun <T> response(block: (String) -> T): T {
            return responseBody(call.notNull()).use {
                val body = it.string()
                block(body)
            }
        }
    }

    @DslTag
    protected inner class _Request {
        var url: String? = null
        var charset: String? = this@DslJsoupNovelContext.charset
        var method: String? = null
        var httpUrl: HttpUrl? = null
        var requestBody: RequestBody? = null
        var request: Request? = null
        var dataMap: Map<String, String>? = null
        fun createCall(): Call {
            val httpUrlBuilder = (httpUrl
                    ?: HttpUrl.parse(absUrl(url.notNull())).notNull()).newBuilder()
            val requestBuilder = request?.newBuilder() ?: Request.Builder()
            if (HttpMethod.permitsRequestBody(method.notNull())) {
                // post,
                // 编码可以空，会是默认UTF-8,
                @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                val bodyBuilder = FormBody.Builder(charset?.let { Charset.forName(charset) })
                dataMap?.forEach { (name, value) ->
                    bodyBuilder.add(name, value)
                }
                requestBuilder.method(method.notNull(), requestBody ?: bodyBuilder.build())
            } else {
                // get,
                dataMap?.forEach { (name, value) ->
                    val eName = URLEncoder.encode(name, charset ?: defaultCharset)
                    val eValue = URLEncoder.encode(value, charset ?: defaultCharset)
                    httpUrlBuilder.addEncodedQueryParameter(eName, eValue)
                }
                requestBuilder.method(method.notNull(), null)
            }
            requestBuilder.url(httpUrlBuilder.build())
            return client.newCall(requestBuilder.build())
        }

        fun data(init: _Data.() -> Unit) {
            _Data().also { _data ->
                _data.init()
                dataMap = _data.createDataMap()
            }
        }

        inner class _Data {
            val map: MutableMap<String, String> = mutableMapOf()
            fun createDataMap(): Map<String, String> = map
            infix fun String.to(value: String) {
                // 和kotlin内建的Pair方法重名，无所谓了，
                map[this] = value

            }
        }
    }

    /*
    *************** annotation ***************
     */
    @DslMarker
    annotation class DslTag
}
