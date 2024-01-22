package com.example.freezer.model

data class Drawer (
    val id: Int,
    val name: String, // Například "Horní šuplík", "Dolní šuplík" atd.
    val items: List<FoodItem>
)