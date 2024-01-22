package com.example.freezer.model

import java.util.Date

data class FoodItem (
    val id: Int,
    val name: String,
    val quantity: String, // Může být v jednotkách, jako jsou kusy, gramy atd.
    val dateStored: Date,
    val shelfLife: Int // Doba úschovy v dnech
)