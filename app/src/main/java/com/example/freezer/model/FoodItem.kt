package com.example.freezer.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import androidx.room.TypeConverter

import java.time.format.DateTimeFormatter

@Entity(tableName = "items")
data class FoodItem (
    @PrimaryKey(autoGenerate = true) val itemId: Int = 0,
    val drawerId: Int,
    val name: String,
    val itemCount: String,
    val quantityType: String,
    val dateAdded: String

)

class Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? {
        return value?.format(formatter)
    }

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it, formatter) }
    }
}