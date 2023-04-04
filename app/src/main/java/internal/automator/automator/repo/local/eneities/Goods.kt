package internal.automator.automator.repo.local.eneities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Goods(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo val goodsName: String?,
    @ColumnInfo val goodsPrice: Int?,
    @ColumnInfo val storeId: Long?,
)