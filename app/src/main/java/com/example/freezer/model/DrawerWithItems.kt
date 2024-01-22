package com.example.freezer.model

import androidx.room.Embedded
import androidx.room.Relation

data class DrawerWithItems(
    @Embedded val drawer: Drawer,
    @Relation(
        parentColumn = "drawerId",
        entityColumn = "drawerId"
    )
    val items: List<FoodItem>
)