package com.example.freezer.dao

import androidx.room.*
import com.example.freezer.model.FoodItem

@Dao
interface ItemDao {
    @Insert
    suspend fun insertItem(item: FoodItem)

    @Update
    suspend fun updateItem(item: FoodItem)

    @Delete
    suspend fun deleteItem(item: FoodItem)
    @Query("SELECT * FROM items WHERE drawerId = :drawerId")
    suspend fun getItemsByDrawer(drawerId: Int): List<FoodItem>
}