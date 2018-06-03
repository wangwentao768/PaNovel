package cc.aoeiuv020.panovel.api.site

import cc.aoeiuv020.panovel.api.base.DslJsoupNovelContext

/**
 * Created by AoEiuV020 on 2018.06.02-20:54:12.
 */
class Yllxs : DslJsoupNovelContext() {init {
    site {
        name = "166小说"
        baseUrl = "http://www.166xs.com/"
        logo = "https://imgsa.baidu.com/forum/w%3D580/sign=b59a4eba45540923aa696376a259d1dc/90c68adfa9ec8a135b0ce779fb03918fa1ecc004.jpg"
    }
    search {
        get {
            charset = "GBK"
            url = "/modules/article/search.php"
            data {
                "searchkey" to it
                // 加上&page=1可以避开搜索时间间隔的限制，
                // 也可以通过不加载cookies避开搜索时间间隔的限制，
                "page" to "1"
            }
        }
        document {
            if (root.ownerDocument().location().endsWith(".html")) {
                // "http://www.166xs.com/116732.html"
                single {
                    name("#book_left_a > div.book > div.book_info > div.title > h2") {
                        it.ownText()
                    }
                    author("#book_left_a > div.book > div.book_info > div.title > h2 > address", block = pickString("作者：(\\S*)"))
                }
            } else {
                items("#Updates_list > ul > li") {
                    name("> div.works > a.name")
                    author("> div.author > a", block = pickString("([^ ]*) 作品集"))
                }
            }
        }
    }
    // "http://www.166xs.com/116732.html"
    // "http://www.166xs.com/xiaoshuo/116/116732/"
    // "http://www.166xs.com/xiaoshuo/121/121623/34377467.html"
    // 前面一段只有3个数字，所以找第一个有四个以上数字的，
    bookIdRegex = "/(\\d{4,})"
    detailPageTemplate = "/%s.html"
    detail {
        document {
            novel {
                /*
                <h2>超品相师<address>作者：西域刀客</address></h2>
                 */
                name("#book_left_a > div.book > div.book_info > div.title > h2") {
                    it.ownText()
                }
                author("#book_left_a > div.book > div.book_info > div.title > h2 > address", block = pickString("作者：(\\S*)"))
            }
            image("#book_left_a > div.book > div.pic > img")
            update("#book_left_a > div.book > div.book_info > div.info > p > span:nth-child(8)", format = "yyyy-MM-dd", block = pickString("更新时间：(.*)"))
            introduction("#book_left_a > div.book > div.book_info > div.intro > p")
        }
    }
    // http://www.166xs.com/xiaoshuo/121/121623/
    chaptersPageTemplate = "/xiaoshuo/%.3s/%s/"
    chapters {
        // 七个多余的，
        // 三个class="book_btn"，
        // 一个最新章，
        // 三个功能按钮，
        document {
            items("body > dl > dd > a")
        }.drop(3)
    }
    // http://www.166xs.com/xiaoshuo/121/121623/34377471.html
    chapterIdRegex = "/xiaoshuo/\\d+/(\\d+/\\d+)"
    contentPageTemplate = "/xiaoshuo/%.3s/%s.html"
    content {
        document {
            items("p.Book_Text") {
                it.ownTextList().dropLastWhile {
                    it == "166小说阅读网"
                }
            }
        }
    }
}
}
