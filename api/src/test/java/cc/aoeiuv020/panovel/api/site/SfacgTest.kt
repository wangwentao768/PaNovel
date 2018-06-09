package cc.aoeiuv020.panovel.api.site

import org.junit.Test

/**
 *
 * Created by AoEiuV020 on 2018.03.06-19:25:09.
 */
class SfacgTest : BaseNovelContextText(Sfacg::class) {
    @Test
    fun search() {
        search("都市")
        search("黑猫变成少女才不奇怪呢", "青衣流苏", "123589")
        search("重生之都市少女", "雁落荆南", "71095")
        search("学战都市").first {
            // 不支持搜索空格，
            "学战都市 开阳" == it.name
                    && "帝国之钻" == it.author
                    && "49506" == it.extra
        }
    }

    @Test
    fun detail() {
        detail("123589", "123589", "黑猫变成少女才不奇怪呢", "青衣流苏",
                "http://rs.sfacg.com/web/novel/images/NovelCover/Big/2018/02/556aa0b6-25b5-43de-bd60-9a7bc9234ba5.jpg",
                "好久好久，都没有这么快乐过了。黑发的少女于彼端如此叹息。\n" +
                        "“你的世界，真美。”",
                "2018-05-17 22:51:10")
        detail("114367", "114367", "我的学生们都是病娇大小姐", "诡话连篇",
                "http://rs.sfacg.com/web/novel/images/NovelCover/Big/2018/03/cef45df7-b012-460b-a18b-85f4bb4be97b.jpg",
                "“同学们，下课了！”我站在讲台上整理了一下衣服，怯怯地说道：“所以你们能把我的手铐打开吗……”\n" +
                        "我的可爱学生们一齐答道：“啊咧，这怎么可以呢？合格的老师就应该二十四小时陪伴在学生身边吧？”\n" +
                        "什么？我拿你们当学生，你们却想上我？！\n" +
                        "还有坐在后面的同学，请务必放下你手中的柴..",
                "2018-05-20 17:53:37")
    }

    @Test
    fun chapters() {
        chapters("114367",
                "二月月票福利计划~", "/Novel/114367/199838/1653202/", null,
                "第四十一章", "/vip/c/1892767/", null,
                207)
    }

    @Test
    fun content() {
        content("/Novel/114367/191449/1584685/",
                "樱才高中，位于K市市中心，是一所名副其实的贵族女校。",
                "“喂，等等，你们要什么？不要拉我……我真的没有语言猥亵学生啊！”",
                34)
        content("/vip/c/1725750/",
                "双手抱胸的短发女孩，浑身上下都透露着浓厚的“夹击妹抖”（日语“风纪委员”谐音）气息，简洁干练的学生制服，不施粉黛略带怒容的俏颜，如果再给她配上一根教鞭，一定会成为令所有学生闻风丧胆的存在。",
                "但我可不..",
                2)
    }
}
