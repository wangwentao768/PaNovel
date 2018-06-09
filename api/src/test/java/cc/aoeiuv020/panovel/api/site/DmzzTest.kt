//package cc.aoeiuv020.panovel.api.site
//
//import org.jsoup.Jsoup
//import org.junit.Assert.assertEquals
//import org.junit.Assert.assertTrue
//import org.junit.Before
//import org.junit.Test
//
///**
// *
// * Created by AoEiuV020 on 2017.11.30-18:14:03.
// */
//class DmzzTest {
//    init {
//        System.setProperty("org.slf4j.simpleLogger.log.Dmzz", "trace")
//    }
//
//    private lateinit var context: Dmzz
//    @Before
//    fun setUp() {
//        context = Dmzz()
//    }
//
//    @Test
//    fun searchNovelName() {
//        context.searchNovelName("打工吧").also {
//            it.forEach { novelItem ->
//                println(novelItem)
//            }
//            assertTrue(it.any { novelItem ->
//                novelItem.name == "打工吧魔王大人"
//            })
//        }
//    }
//
//    @Test
//    fun jsoupAbsHref() {
//        val root = Jsoup.parse("""<a href="../4/index.shtml" />""", "http://q.dmzj.com/search.shtml")
//        val a = root.select("a").first()
//        // 简直蠢，没处理相对路径的上级”../“，
//        assertEquals("http://q.dmzj.com/../4/index.shtml", a.absUrl("href"))
//    }
//
//    @Test
//    fun getNovelDetail() {
//        context.getNovelDetail("http://q.dmzj.com/2137/index.shtml").let {
//            assertEquals("GAMERS！电玩咖！", it.novel.name)
//            assertEquals("葵せきな(葵关南)", it.novel.author)
//            assertEquals("", it.introduction)
//            assertEquals("https://xs.dmzj.com/img/webpic/4/games56989l.jpg", it.image)
//            println(it.update)
//        }
//    }
//
//    @Test
//    fun getNovelChaptersAsc() {
//        context.getNovelChaptersAsc("http://q.dmzj.com/2137/index.shtml").let { list ->
//            list.forEach {
//                println(it)
//            }
//            assertEquals("转载信息", list.first().name)
//        }
//        context.getNovelChaptersAsc("http://q.dmzj.com/1607/index.shtml").let { list ->
//            list.forEach {
//                println(it)
//            }
//        }
//    }
//
//    @Test
//    fun regex() {
//        val regex = Regex("chapter_list\\[\\d*\\]\\[\\d*\\] = '<a href=\"([^\"]*)\".*>(\\S*)</a>'.*;")
//        val str = """chapter_list[0][7] = '<a href="/2137/7832/61328.shtml" alt="后记" title="后记">后记</a>';"""
//        assertTrue(str.matches(regex))
//    }
///*
//
//    @Test
//    fun getNovelContent() {
//        context.getNovelContent("http://q.dmzj.com/2137/8856/78730.shtml").textListSplitWhitespace.let {
//            assertEquals("「等、等很久了吗？」", it.first())
//            assertEquals("——我们头顶上那片照耀着北方大陆的璀璨星空，今天异常闪耀。", it.last())
//            assertEquals(629, it.size)
//            it.forEach {
//                println(it)
//            }
//        }
//    }
//*/
//}