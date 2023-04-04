package internal.automator.automator.repo.local.dao

import androidx.room.*
import internal.automator.automator.repo.local.eneities.GoodsWithSalesVolume
import internal.automator.automator.repo.local.eneities.SalesVolume

@Dao
interface SalesVolumeDao {
    @Insert
    fun insert(salesVolume: SalesVolume):Long

    @Delete
    fun delete(salesVolume: SalesVolume)

    @Update
    fun update(salesVolume: SalesVolume)

    @Query("select * from salesVolume where goodsId = :goodsId")
    fun findAllByGoodsId(goodsId: Long): List<SalesVolume>

    @Query("SELECT g.*, s.salesVolume FROM Goods g INNER JOIN (SELECT goodsId, MAX(timestamp) AS maxTimestamp, MIN(timestamp) AS minTimestamp FROM SalesVolume GROUP BY goodsId) AS t ON g.id = t.goodsId INNER JOIN SalesVolume s ON g.id = s.goodsId AND s.timestamp = t.maxTimestamp INNER JOIN SalesVolume p ON g.id = p.goodsId AND p.timestamp = t.minTimestamp ORDER BY (s.salesVolume - p.salesVolume) DESC LIMIT :pageSize OFFSET :pageIndex")
    fun getSalesVolumeSortedByChange(pageSize: Int, pageIndex: Int): List<GoodsWithSalesVolume>
}