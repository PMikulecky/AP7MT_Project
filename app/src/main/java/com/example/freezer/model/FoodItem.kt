package com.example.freezer.model

import java.util.Date
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Relation

@Entity(tableName = "items")
data class FoodItem (
    @PrimaryKey(autoGenerate = true) val itemId: Int = 0,
    val drawerId: Int,
    val name: String,
    val quantity: Int,
    val dateStored: Int,
    val shelfLife: Int
)