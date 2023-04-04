package internal.automator.automator.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bin.david.form.core.SmartTable
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.button.MaterialButton
import internal.automator.automator.App
import internal.automator.automator.R
import internal.automator.automator.entities.view.GoodsInformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Result : AppCompatActivity() {
    lateinit var smartTable: SmartTable<GoodsInformation>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        var pageIndex = 0 // 第一页
        val pageSize = 500 // 每页10个结果

        setData(pageIndex, pageSize)

//        findViewById<MaterialButton>(R.id.back).setOnClickListener {
//            if (pageIndex == 0) {
//                ToastUtils.showShort("首页")
//                return@setOnClickListener
//            }
//            pageIndex--
//            setData(pageIndex, pageSize)
//        }
//        findViewById<MaterialButton>(R.id.next).setOnClickListener {
//            pageIndex++
//            setData(pageIndex, pageSize)
//        }

        smartTable = findViewById(R.id.smartTableResult)
        smartTable.setZoom(true)
    }

    private fun setData(
        pageIndex: Int,
        pageSize: Int,
    ) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {

                    val salesVolumeList = App.appDatabase.useSalesVolumeDao()
                        .getSalesVolumeSortedByChange(pageSize, pageIndex)
                    salesVolumeList.forEach {
                        println(it)
                    }
                    smartTable.setData(salesVolumeList.map {
                        val svList = App.appDatabase
                            .useSalesVolumeDao()
                            .findAllByGoodsId(it.goods.id!!)
                        val store = App.appDatabase
                            .useStoreDao()
                            .findById(it.goods.storeId!!)!!

                        val strings = svList.map { salesVolume ->
                            "时间:${TimeUtils.millis2String(salesVolume.timestamp!!)} 销量:${salesVolume.salesVolume}"
                        }.toList().joinToString("\n")

                        GoodsInformation(
                            goodsName = it.goods.goodsName!!,
                            goodsPrice = it.goods.goodsPrice!!,
                            creatorName = store.creatorName!!,
                            storeName = store.storeName!!,
                            goodsSalesVolumes = strings
                        )
                    }.toList())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}