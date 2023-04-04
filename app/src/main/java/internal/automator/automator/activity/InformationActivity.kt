package internal.automator.automator.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bin.david.form.core.SmartTable
import com.blankj.utilcode.util.TimeUtils
import com.google.android.material.button.MaterialButton
import internal.automator.automator.App
import internal.automator.automator.R
import internal.automator.automator.entities.view.GoodsInformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InformationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)

        val smartTable: SmartTable<GoodsInformation> = findViewById(R.id.smartTable)
        smartTable.setZoom(true)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val goodsDao = App.appDatabase.useGoodsDao()
                val storeDao = App.appDatabase.useStoreDao()
                val salesVolumeDao = App.appDatabase.useSalesVolumeDao()


                val list = goodsDao.findAll()
                val listReadied = list.map { goods ->
                    val store = storeDao.findById(goods.storeId!!)!!
                    val salesVolumes = salesVolumeDao.findAllByGoodsId(goods.id!!)
                    GoodsInformation(
                        goodsName = goods.goodsName!!,
                        goodsPrice = goods.goodsPrice!!,
                        creatorName = store.creatorName!!,
                        storeName = store.storeName!!,
                        goodsSalesVolumes = salesVolumes.map { salesVolume ->
                            "时间:${TimeUtils.millis2String(salesVolume.timestamp!!)} 销量:${salesVolume.salesVolume}"
                        }.toList().joinToString("\n")
                    )
                }
                smartTable.setData(listReadied)
            }
        }

        findViewById<MaterialButton>(R.id.run).setOnClickListener {
            val intent = Intent(applicationContext, Result::class.java)
            startActivity(intent)
        }


        findViewById<MaterialButton>(R.id.export).setOnClickListener {

        }
    }
}