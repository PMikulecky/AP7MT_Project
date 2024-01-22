package com.example.freezer.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Relation

@Entity(tableName = "drawers")
data class Drawer (
    @PrimaryKey(autoGenerate = true) val drawerId: Int = 0,
    val name: String,
)