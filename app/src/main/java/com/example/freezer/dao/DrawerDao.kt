package com.example.freezer.dao

import androidx.room.*
import com.example.freezer.model.Drawer
import com.example.freezer.model.FoodItem
import com.example.freezer.model.DrawerWithItems

@Dao
interface DrawerDao {
    @Insert
    suspend fun insertDrawer(drawer: Drawer): Long

    @Update
    suspend fun updateDrawer(drawer: Drawer)

    //@Delete
    @Query("DELETE FROM drawers WHERE DrawerId = :drawerId")
    suspend fun deleteDrawer(drawerId: Int)

    @Transaction
    @Query("SELECT * FROM drawers WHERE drawerId = :drawerId")
    suspend fun getDrawerWithItems(drawerId: Int): List<DrawerWithItems>

    @Transaction
    @Query("SELECT * FROM drawers")
    suspend fun getAllDrawersWithItems(): List<DrawerWithItems>

    @Query("SELECT * FROM drawers")
    suspend fun getAllDrawers(): List<Drawer>

    @Query("UPDATE drawers SET name = :newName WHERE DrawerId = :drawerId")
    suspend fun updateDrawerName(drawerId: Int, newName: String)
}