package internal.automator.automator.repo.local.eneities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SalesVolume(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo val timestamp: Long?,
    @ColumnInfo val salesVolume: Int?,
    @ColumnInfo val goodsId: Long?,
)