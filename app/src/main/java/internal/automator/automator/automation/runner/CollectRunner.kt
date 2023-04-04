package internal.automator.automator.automation.runner

import android.content.Context
import cn.vove7.andro_accessibility_api.api.back
import cn.vove7.andro_accessibility_api.api.waitForApp
import cn.vove7.andro_accessibility_api.viewfinder.SF
import cn.vove7.andro_accessibility_api.viewfinder.containsText
import cn.vove7.andro_accessibility_api.viewfinder.id
import cn.vove7.andro_accessibility_api.viewfinder.text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import internal.automator.automator.App
import internal.automator.automator.automation.common.Automation
import internal.automator.automator.automation.common.GoodsFormatter
import internal.automator.automator.automation.common.Time
import internal.automator.automator.automation.conditions.className
import internal.automator.automator.automation.conditions.textStartsWith
import internal.automator.automator.repo.local.eneities.Goods
import internal.automator.automator.repo.local.eneities.SalesVolume
import internal.automator.automator.repo.local.eneities.Store
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class CollectRunner(
    private val context: Context,
    private val println: (message: Any?) -> Unit,
) : Runner {
    var duration: Long = 1000
    var name: String = ""



    override suspend fun runAutomation() {
        context.startActivity(context.packageManager.getLaunchIntentForPackage("com.tencent.mm"))
        if (!waitForApp("com.tencent.mm")) return
        SF.text("发现").waitFor()?.tryClick()
        SF.text("视频号").waitFor()?.tryClick()
        while (true) {
            SF.id("kl7").waitFor()?.scrollForward()
            delay(1000)

            val oriCreatorName = SF.id("bh6").waitFor()?.text
            if (oriCreatorName.isNullOrBlank()) throw Exception("作者名称获取失败")
            println("作者名称:$oriCreatorName")

            val container = SF.id("bm3").waitFor(6000) ?: continue
            container.tryClick()
            val originalTip = container.requireChildAt(0).requireChildAt(1).text ?: continue

            try {
                val oriGoodsPriceContainer = SF
                    .textStartsWith("￥")
                    .containsText("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
                    .waitFor(waitTime = 10000, includeInvisible = true)
                delay(500)
                val oriGoodsPrice = oriGoodsPriceContainer?.text
                if (oriGoodsPrice.isNullOrBlank()) throw Exception("商品价格获取失败")
                println("商品价格:$oriGoodsPrice")


                val oriGoodsSalesVolume = SF
                    .textStartsWith("已售")
                    .containsText("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
                    .waitFor(waitTime = 10000, includeInvisible = true)
                    ?.text
                if (oriGoodsSalesVolume.isNullOrBlank()) throw Exception("商品销量获取失败")
                println("商品销量:$oriGoodsSalesVolume")


                val oriGoodsName = SF
                    .containsText(*originalTip.toString().split(" ").toTypedArray())
                    .waitFor(waitTime = 10000, includeInvisible = true)
                    ?.text
                if (oriGoodsName.isNullOrBlank()) throw Exception("商品名称获取失败")
                println("商品名称:$oriGoodsName")


                val oriStoreName = SF
                    .text("评分")
                    .waitFor(waitTime = 10000, includeInvisible = true)
                    ?.parent?.childAt(0)?.childAt(0)?.text
                if (oriStoreName.isNullOrBlank()) throw Exception("商店名称获取失败")
                println("商店名称:$oriStoreName")


                val fmtCreatorName = oriCreatorName.toString()
                val fmtStoreName = oriStoreName.toString()
                val fmtGoodsName = oriGoodsName.toString()
                val fmtGoodsSalesVolume = GoodsFormatter.formatSalesVolume(oriGoodsSalesVolume.toString())
                val fmtGoodsPrice = GoodsFormatter.formatPrice(oriGoodsPrice.toString())

                saveData(
                    fmtCreatorName,
                    fmtStoreName,
                    fmtGoodsName,
                    fmtGoodsPrice,
                    fmtGoodsSalesVolume
                )

                delay(1000)
                SF.className("android.widget.Image").waitFor()?.tryClick()
                SF.text(name).waitFor()?.tryClick()
                delay(1000)
                SF.text("发送").waitFor()?.tryClick()
                back()
                SF.text("发现").waitFor()?.tryClick()
                SF.text("视频号").waitFor()?.tryClick()
                SF.id("k07").waitFor()?.tryClick()
            } catch (e: Exception) {
                println(e.toString())
                back()
            }
        }
    }

    private suspend fun saveData(
        iCreatorName: String,
        iStoreName: String,
        iGoodsName: String,
        iGoodsPrice: Int,
        iGoodsSalesVolume: Int,
    ) {
        withContext(Dispatchers.IO) {
            val iStoreDao = App.appDatabase.useStoreDao()
            val iGoodsDao = App.appDatabase.useGoodsDao()
            val iSalesVolumeDao = App.appDatabase.useSalesVolumeDao()

            var iStore = iStoreDao.findFirstByStoreName(iStoreName)
            if (iStore == null) {
                printlnInIO("当前商店首次采集")
                iStore = Store(
                    storeName = iStoreName,
                    creatorName = iCreatorName,
                )
                iStore.id = iStoreDao.insert(iStore)
                printlnInIO("id:${iStore.id}")
            }

            var iGoods = iGoodsDao.findFirstByGoodsName(iGoodsName)
            if (iGoods == null) {
                printlnInIO("当前商品首次采集")
                iGoods = Goods(
                    goodsName = iGoodsName,
                    goodsPrice = iGoodsPrice,
                    storeId = iStore.id
                )
                iGoods.id = iGoodsDao.insert(iGoods)
                printlnInIO("id:${iGoods.id}")
            }

            val exists = iSalesVolumeDao.findAllByGoodsId(iGoods.id!!).find {
                Time.isSameDate(it.timestamp!!, System.currentTimeMillis())
            } != null

            if (!exists) {
                printlnInIO("更新当前商品销量")
                val id = iSalesVolumeDao.insert(
                    SalesVolume(
                        timestamp = System.currentTimeMillis(),
                        salesVolume = iGoodsSalesVolume,
                        goodsId = iGoods.id
                    )
                )
                printlnInIO("id:${id}")
            } else printlnInIO("当前商品销量今日已经更新")
        }
    }

    private suspend fun printlnInIO(content: Any?) {
        withContext(Dispatchers.Main) {
            println(content)
        }
    }
}