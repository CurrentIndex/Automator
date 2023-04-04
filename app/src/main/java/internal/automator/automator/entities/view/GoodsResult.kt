package internal.automator.automator.entities.view

import com.bin.david.form.annotation.SmartColumn
import com.bin.david.form.annotation.SmartTable

@SmartTable(name = "销量变化最大降序排序")
class GoodsResult(
    @SmartColumn(id = 1, name = "作者名称") private val creatorName: String,
    @SmartColumn(id = 2, name = "商店名称") private val storeName: String,
    @SmartColumn(id = 3, name = "商品名称") private val goodsName: String,
    @SmartColumn(id = 4, name = "商品价格") private val goodsPrice: Int,
    @SmartColumn(id = 5, name = "商品销量") private val goodsSalesVolumes: String,
)