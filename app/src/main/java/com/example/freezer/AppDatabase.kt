package com.example.freezer

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.freezer.dao.DrawerDao
import com.example.freezer.dao.ItemDao
import com.example.freezer.model.Drawer
import com.example.freezer.model.FoodItem

@Database(entities = [Drawer::class, FoodItem::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun drawerDao(): DrawerDao
    abstract fun itemDao(): ItemDao
}