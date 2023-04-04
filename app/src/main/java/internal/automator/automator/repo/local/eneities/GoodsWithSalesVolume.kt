package internal.automator.automator.repo.local.eneities

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class GoodsWithSalesVolume(
    @Embedded val goods: Goods,
    @ColumnInfo val salesVolume: Int
)
