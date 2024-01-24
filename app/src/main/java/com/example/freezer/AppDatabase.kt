package com.example.freezer

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.freezer.dao.DrawerDao
import com.example.freezer.dao.ItemDao
import com.example.freezer.model.Drawer
import com.example.freezer.model.FoodItem
import androidx.room.TypeConverters
import com.example.freezer.model.Converters

@Database(entities = [Drawer::class, FoodItem::class], version = 5)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun drawerDao(): DrawerDao
    abstract fun itemDao(): ItemDao
}