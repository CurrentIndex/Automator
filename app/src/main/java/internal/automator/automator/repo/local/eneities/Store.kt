package internal.automator.automator.repo.local.eneities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Store(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo val creatorName: String? = null,
    @ColumnInfo val storeName: String? = null,
)