package internal.automator.automator.automation.runner

import android.content.Context
import android.graphics.Bitmap
import android.media.ImageReader
import cn.vove7.andro_accessibility_api.api.*
import cn.vove7.andro_accessibility_api.viewfinder.*
import cn.vove7.andro_accessibility_api.viewnode.ViewNode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import internal.automator.automator.App
import internal.automator.automator.automation.common.Automation
import internal.automator.automator.automation.common.GoodsFormatter
import internal.automator.automator.automation.common.ScreenShot
import internal.automator.automator.automation.common.Time
import internal.automator.automator.repo.local.eneities.Goods
import internal.automator.automator.repo.local.eneities.SalesVolume
import internal.automator.automator.repo.local.eneities.Store
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.stream.Collectors
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CollectStoreRunner(
    private val context: Context,
    private val send: (message: Any?) -> Unit,
) : Runner {
    lateinit var imageReader: ImageReader
    lateinit var name: String

    companion object {
        val recognizer = TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
    }

    override suspend fun runAutomation() {

        context.startActivity(context.packageManager.getLaunchIntentForPackage("com.tencent.mm"))
        if (!waitForApp("com.tencent.mm")) return

        SF.text(name).waitFor(includeInvisible = true)?.tryClick()

        val iProcessedChatContents = mutableListOf<String>() // 已经处理的商品组件
        var screenNotChange = 0 // 页面没有变化次数
        while (screenNotChange < 5) // 页面没有变化5次结束运行
        {
            delay(5000) // 等待加载

            val chats = SF.id("b47").findAll() // 商品组件
            val iProcessedChats = chats.filter { chat ->
                val chatContent = chat.findAllWith(includeInvisible = true) { node -> node.text != null }
                    .map { node -> node.text!! }
                    .toList()
                    .joinToString("") // 根据所有的文字内容组成已经处理标识
                iProcessedChatContents.contains(chatContent) // 过滤没有处理的组件
            }

            send(chats.size.toString() + "|" + iProcessedChats.size.toString())

            if (chats.size == iProcessedChats.size) { // 所有组件已经处理，页面没有变化
                screenNotChange++
                send("页面没有变化:$screenNotChange")
            } else {

                screenNotChange = 0 // 重置页面没有变化次数

                val iUnProcessedChats = chats.subtract(iProcessedChats.toSet()) // 没有处理的组件
                iUnProcessedChats.forEach { chat ->
                    val chatContent =
                        chat.findAllWith(includeInvisible = true) { node -> node.text != null }
                            .map { node -> node.text!! }
                            .toList().joinToString("") // 根据所有的文字内容组成已经处理标识
                    iProcessedChatContents.add(chatContent)
                    send("处理:${chatContent}")

                    iGoCards(chat)
                }
            }

            SF.id("b79").waitFor()?.scrollBackward() // 滑动页面
        }
        send("结束")
    }

    private suspend fun iGoCards(chat: ViewNode) {
        chat.tryClick()

        val oriStoreName = SF
            .text("评分")
            .waitFor(includeInvisible = true)
            ?.parent?.childAt(0)?.childAt(0)?.text
        if (oriStoreName.isNullOrBlank()) {
            send("商店名称获取失败")
            Automation.backCount(1)
            return
        }
        send("商店名称:$oriStoreName")


        SF.text("进店逛逛").waitFor(includeInvisible = true)?.tryClick()
        delay(2000)

        val oriCreatorName = SF.text("视频号:").waitFor()?.requireParent?.children?.last()?.text
        if (oriCreatorName.isNullOrBlank()) {
            send("作者名称获取失败")
            Automation.backCount(2)
            return
        }
        send("作者名称:$oriCreatorName")

        delay(2000)

        var screenNotChange = 0 // 页面状态没有发生改变的数量
        var deprecatedScreenContent = "" // 过时的页面内容
        while (screenNotChange < 5) {
            try {
                val bitmap = ScreenShot.createBitmap(imageReader)
                val screenContent = recognizeBitmap(bitmap).textBlocks
                    .filter { it.boundingBox!!.top <= bitmap.height * 0.5 }
                    .map { it.text.trim().replace("\n", "") }
                    .toList()
                    .joinToString("") // 页面当前内容
                // 内容相似度处理,文本识别需要容错处理
                if (sentenceDice(deprecatedScreenContent, screenContent) >= 0.8) {
                    screenNotChange++
                    send("页面状态没有变化:${screenNotChange}")
                    delay(5000)
                }
                // 页面发生变化
                else
                // 重置
                {
                    send("重置页面状态")
                    screenNotChange = 0
                    deprecatedScreenContent = screenContent
                }
                // 滑动页面
                requireGestureAccessibility()
                val bottom = (bitmap.height * 0.9).toInt()
                val center = (bitmap.width * 0.5).toInt()
                swipe(center, bottom, center, bottom - bitmap.height / 2, 400)
                requireBaseAccessibility()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            delay(1000)
        }

        val cards = SF.containsDesc("¥", "￥").clickable(true).find(includeInvisible = true).sortedBy {
            it.boundsInParent.top // 根据高度排序
        }
        var noSatisfiedCount = 0
        for (card in cards) {
            // 宝宝拍儿童摸高器 儿童摸高弹跳训练器 科学训练长高语音计数器跳高架 7天无理由 ¥29.9 起 已售2053
            val sentences = card.desc()!!
                .split(" ")
                .filter { sentence -> !sentence.contains("起") && !sentence.contains("理") }
                .toMutableList()

            var oriSalesVolume = sentences.find { sentence -> sentence.startsWith("已售") }
            if (oriSalesVolume.isNullOrBlank()) {
                send("商品详情")
                card.tryClick()
                delay(2000)

                SF.containsText("已售").find().filter {
                    !it.text!!.contains(" ") && !it.text!!.contains(":")
                }.toList().let { if (it.size == 2) oriSalesVolume = it.last().text.toString() }

                Automation.backCount(1) // 2000
                delay(2000)
            }
            sentences.remove(oriSalesVolume)
            if (oriSalesVolume.isNullOrBlank()) {
                send("商品销量获取失败")
                continue
            }

            val oriPrice = sentences.find { sentence -> sentence.startsWith("¥") || sentence.startsWith("￥") }
            sentences.remove(oriPrice)
            if (oriPrice.isNullOrBlank()) {
                send("商品价格获取失败")
                continue
            }

            val oriGoodsName = sentences.joinToString("")
            if (oriGoodsName.isBlank()) {
                send("商品名称获取失败")
                continue
            }

            send("商品销量:$oriSalesVolume")
            send("商品价格:$oriPrice")
            send("商品名称:$oriGoodsName")

            val fmtPrice = GoodsFormatter.formatPrice(oriPrice)
            val fmtSalesVolume = GoodsFormatter.formatSalesVolume(oriSalesVolume.toString())

            println("$fmtPrice $fmtSalesVolume")
            if (fmtSalesVolume < 100) {
                send("销量低于阈值")
                noSatisfiedCount++
                if (noSatisfiedCount == 2) break
            }

            if (fmtPrice < 20) {
                delay(1000)
                continue
            }

            saveData(
                iStoreName = oriStoreName.toString(),
                iStoreCreator = oriCreatorName.toString(),
                iGoodsName = oriGoodsName,
                iGoodsPrice = GoodsFormatter.formatPrice(oriPrice),
                iGoodsSalesVolume = GoodsFormatter.formatSalesVolume(oriSalesVolume.toString())
            )

            delay(1000)
        }

        Automation.backCount(2)
    }

    private fun sentenceDice(a: String, b: String): Float {
        val aChars: Set<Int> = a.chars().boxed().collect(Collectors.toSet())
        val bChars: Set<Int> = b.chars().boxed().collect(Collectors.toSet())
        val intersect: Int = aChars.intersect(bChars).size
        if (intersect == 0) return 0f
        return 2 * intersect.toFloat() / (aChars.size + bChars.size).toFloat()
    }

    private suspend fun recognizeBitmap(bitmap: Bitmap): Text {
        return suspendCoroutine { continuation ->
            val image = InputImage.fromBitmap(bitmap, 0)
            recognizer
                .process(image)
                .addOnSuccessListener { text -> continuation.resume(text) }
                .addOnFailureListener { e -> continuation.resumeWithException(e) }
        }
    }

    private suspend fun saveData(
        iStoreName: String,
        iStoreCreator: String,
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
                sendIO("当前商店首次采集")
                iStore = Store(storeName = iStoreName, creatorName = iStoreCreator)
                iStore.id = iStoreDao.insert(iStore)
            }

            var iGoods = iGoodsDao.findFirstByGoodsName(iGoodsName)
            if (iGoods == null) {
                sendIO("当前商品首次采集")
                iGoods = Goods(goodsName = iGoodsName, goodsPrice = iGoodsPrice, storeId = iStore.id)
                iGoods.id = iGoodsDao.insert(iGoods)
            }

            val exists = iSalesVolumeDao.findAllByGoodsId(iGoods.id!!).find {
                Time.isSameDate(it.timestamp!!, System.currentTimeMillis())
            } != null

            if (!exists) {
                sendIO("更新当前商品销量")
                iSalesVolumeDao.insert(SalesVolume(timestamp = System.currentTimeMillis(), salesVolume = iGoodsSalesVolume, goodsId = iGoods.id))
            } else sendIO("当前商品销量今日已经更新")
        }
    }

    private suspend fun sendIO(content: Any?) {
        withContext(Dispatchers.Main) { send(content) }
    }
}