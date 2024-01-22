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

    @Delete
    suspend fun deleteDrawer(drawer: Drawer)

    @Transaction
    @Query("SELECT * FROM drawers WHERE drawerId = :drawerId")
    suspend fun getDrawerWithItems(drawerId: Int): List<DrawerWithItems>

    @Query("SELECT * FROM drawers")
    suspend fun getAllDrawers(): List<Drawer>
}