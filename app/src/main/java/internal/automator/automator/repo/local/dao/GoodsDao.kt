package internal.automator.automator.repo.local.dao


import androidx.room.*
import internal.automator.automator.repo.local.eneities.Goods

@Dao
interface GoodsDao {
    @Insert
    fun insert(goods: Goods): Long

    @Delete
    fun delete(goods: Goods)

    @Update
    fun update(goods: Goods)

    @Query("select * from goods where goodsName = :goodsName limit 1")
    fun findFirstByGoodsName(goodsName: String): Goods?

    @Query("select * from goods")
    fun findAll(): List<Goods>
}