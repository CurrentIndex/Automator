package internal.automator.automator.entities.view

import com.bin.david.form.annotation.SmartColumn
import com.bin.david.form.annotation.SmartTable


@SmartTable(name = "商品")
class GoodsInformation(
    @SmartColumn(id = 1, name = "作者名称") private val creatorName: String,
    @SmartColumn(id = 1, name = "商店名称") private val storeName: String,
    @SmartColumn(id = 1, name = "商品名称") private val goodsName: String,
    @SmartColumn(id = 1, name = "商品价格") private val goodsPrice: Int,
    @SmartColumn(id = 1, name = "商品销量") private val goodsSalesVolumes: String,
)