package internal.automator.automator.repo.local.dao

import androidx.room.*
import internal.automator.automator.repo.local.eneities.Store

@Dao
interface StoreDao {
    @Insert
    fun insert(store: Store): Long

    @Delete
    fun delete(store: Store)

    @Update
    fun update(store: Store)

    @Query("select * from store where storeName = :storeName limit 1")
    fun findFirstByStoreName(storeName: String): Store?

    @Query("select * from store where id = :id limit 1")
    fun findById(id: Long): Store?
}