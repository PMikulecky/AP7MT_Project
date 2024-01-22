package com.example.freezer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.freezer.model.FoodItem
import com.example.freezer.model.Drawer
import com.example.freezer.model.DrawerWithItems
import java.util.Date

class DrawerViewModel(application: Application) : AndroidViewModel(application) {
    // Get a database instance
    private val db = DatabaseBuilder.getDatabase(application)

    // Get the DAOs
    private val drawerDao = db.drawerDao()
    private val itemDao = db.itemDao()

    // LiveData to observe drawers
    private val _drawers = MutableLiveData<List<Drawer>>()
    val drawers: LiveData<List<Drawer>> = _drawers

    // Function to add a drawer to the database
    fun addDrawer(name: String) {
        viewModelScope.launch {
            val newDrawerId = drawerDao.insertDrawer(Drawer(name = name))
            // Do something with the newDrawerId if needed
        }
    }

    // Function to add an item to a drawer
    fun addItemToDrawer(drawerId: Int, itemName: String, dateStored: Int, quantity: Int, shelfLife: Int) {
        viewModelScope.launch {
            val newItem = FoodItem(
                drawerId = drawerId,
                name = itemName,
                dateStored = dateStored,
                quantity = quantity,
                shelfLife = shelfLife
            )
            itemDao.insertItem(newItem)
        }
    }

    // Function to get all drawers
    fun getAllDrawers() {
        viewModelScope.launch {
            _drawers.value = drawerDao.getAllDrawers()
        }
    }

    // Function to get items for a specific drawer
    fun getItemsForDrawer(drawerId: Int) {
        viewModelScope.launch {
            val drawerWithItems = drawerDao.getDrawerWithItems(drawerId)

        }
    }
}
