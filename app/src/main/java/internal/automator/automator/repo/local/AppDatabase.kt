package internal.automator.automator.repo.local

import androidx.room.Database
import androidx.room.RoomDatabase
import internal.automator.automator.repo.local.dao.GoodsDao
import internal.automator.automator.repo.local.dao.SalesVolumeDao
import internal.automator.automator.repo.local.dao.StoreDao
import internal.automator.automator.repo.local.eneities.Goods
import internal.automator.automator.repo.local.eneities.SalesVolume
import internal.automator.automator.repo.local.eneities.Store


@Database(
    entities = [
        Store::class,
        Goods::class,
        SalesVolume::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun useGoodsDao(): GoodsDao
    abstract fun useStoreDao(): StoreDao
    abstract fun useSalesVolumeDao(): SalesVolumeDao
}