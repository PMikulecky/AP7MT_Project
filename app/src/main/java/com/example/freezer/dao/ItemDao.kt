package com.example.freezer.dao

import androidx.room.*
import com.example.freezer.model.FoodItem

@Dao
interface ItemDao {
    @Insert
    suspend fun insertItem(item: FoodItem)

    @Update
    suspend fun updateItem(item: FoodItem)

    //@Delete
    @Query("DELETE FROM items WHERE ItemId = :itemId")
    suspend fun deleteItem(itemId: Int)
    @Query("DELETE FROM items WHERE drawerId = :drawerId")
    suspend fun deleteItemsByDrawerId(drawerId: Int)
    @Query("SELECT * FROM items WHERE drawerId = :drawerId")
    suspend fun getItemsByDrawer(drawerId: Int): List<FoodItem>

    @Query("SELECT * FROM items")
    suspend fun getAllItems(): List<FoodItem>
}