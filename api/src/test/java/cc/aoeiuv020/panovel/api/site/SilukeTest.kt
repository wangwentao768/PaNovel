package cc.aoeiuv020.panovel.api.site

import org.junit.Test

/**
 * Created by AoEiuV020 on 2018.06.05-18:25:28.
 */
class SilukeTest : BaseNovelContextText(Siluke::class) {
    @Test
    fun search() {
        search("都市")
        search("料理王", "葱爆洋葱", "68917")
        search("万界天尊", "血红", "37247")
    }

    @Test
    fun detail() {
        detail("68917", "68917", "料理王", "葱爆洋葱",
                "http://www.siluke.org/files/article/image/68/68917/68917s.jpg",
                "他叫陈小灶。\n" +
                        "但人们更习惯叫他——料理王！",
                "2017-03-22 00:00:00")
        detail("37247", "37247", "万界天尊", "血红",
                "http://www.siluke.org/files/article/image/37/37247/37247s.jpg",
                "天为何物？\n" +
                        "高高在上，威严莫测，是法则，是戒律，是无情，是冷酷。\n" +
                        "天意，何也？\n" +
                        "俯瞰众生，操持风云，褫夺赏罚，随性而为。故，天意不可测，苍天不可近。顺之应之，未必得其利；逆之叛之，福祸却难定。\n" +
                        "天，禁锢万物如深井，红尘众生如烂泥。\n" +
                        "有这么一只很单纯、很坚定的井底之蛙，谨慎守护着心头那一点小小的微弱的光，带着一定要咬一块天鹅肉的微薄信念，一步步从红尘烂泥中挣扎而出，一步步走出深不见底的污秽深井。\n" +
                        "他张开大嘴向心中的天鹅咬上去的时候，不小心将这一方苍天也一口吞下！\n" +
                        "楚天说，欠钱的，要还钱；欠命的，要偿命。\n" +
                        "这就是楚天认定的最简单、最坚定的道理。\n" +
                        "这道理，比天大！",
                "2018-06-05 00:00:00")
    }

    @Test
    fun chapters() {
        chapters("68917", "第1章 陈小灶", "68917/20835244", null,
                "第129章 大结局（完）", "68917/22138255", null,
                129)
        chapters("37247", "楔子 青铜神木", "37247/14375856", null,
                "第七百八十章 天（2）", "37247/39019068", null,
                1570)
    }

    @Test
    fun content() {
        content("68917/20835244",
                "和许多藏在秦岭脚下的小镇一样，古北镇也是一座依山傍水，风景秀丽的西北小镇。",
                "【新书开启！跪求大家收藏、推荐养肥！】",
                78)
        content("37247/39019068",
                "一轮清光从楚天等人暂居的世界冲天飞起，绕着世界盘旋一周后，就朝着混乱天域核心方向飞去。",
                "“只不过，你们浅薄的认知和弱小的智慧，不足以明白我的努力和挣扎！”",
                40)
    }

}