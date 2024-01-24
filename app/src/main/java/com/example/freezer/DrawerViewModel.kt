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

    private val _drawersWithItems = MutableLiveData<List<DrawerWithItems>>()
    val drawersWithItems: LiveData<List<DrawerWithItems>> = _drawersWithItems

    private val _searchResults = MutableLiveData<List<FoodItem>>()
    val searchResults: LiveData<List<FoodItem>> = _searchResults

    init {
        getAllDrawers()
        getAllDrawersWithItems()
    }

    // Function to add a drawer to the database
    fun addDrawer(name: String) {
        viewModelScope.launch {
            val newDrawerId = drawerDao.insertDrawer(Drawer(name = name))
            // Do something with the newDrawerId if needed

            refreshDrawersWithItems()
        }
    }

    // Function to add an item to a drawer
    // fun addItemToDrawer(drawerId: Int, itemName: String, dateStored: Int, quantity: Int, shelfLife: Int) {
    fun addItemToDrawer(drawerId: Int, itemName: String) {
        viewModelScope.launch {
            val newItem = FoodItem(
                drawerId = drawerId,
                name = itemName
                //dateStored = dateStored,
                //quantity = quantity,
                //shelfLife = shelfLife
            )
            itemDao.insertItem(newItem)

            refreshDrawersWithItems()
        }
    }

    private fun refreshDrawersWithItems() {
        viewModelScope.launch {
            val allDrawersWithItems = drawerDao.getAllDrawersWithItems()
            _drawersWithItems.postValue(allDrawersWithItems)
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

    fun getAllDrawersWithItems() {
        viewModelScope.launch {
            val allDrawersWithItems = drawerDao.getAllDrawersWithItems() // Assuming such a method exists in your DAO
            _drawersWithItems.postValue(allDrawersWithItems)
        }
    }

    // Function to remove an item
    fun deleteItem(itemId: Int) {
        viewModelScope.launch {
            itemDao.deleteItem(itemId) // Assuming such a method exists in your DAO
            refreshDrawersWithItems()
        }
    }

    // Function to remove a drawer and all items in it
    fun deleteDrawer(drawerId: Int) {
        viewModelScope.launch {
            itemDao.deleteItemsByDrawerId(drawerId) // Assuming such a method exists in your DAO
            drawerDao.deleteDrawer(drawerId) // Assuming such a method exists in your DAO
            refreshDrawersWithItems()
        }
    }

    fun updateDrawerName(drawerId: Int, newName: String) {
        viewModelScope.launch {
            drawerDao.updateDrawerName(drawerId, newName)
            refreshDrawersWithItems()  // Refresh the list of drawers with items
        }
    }

    fun searchItems(query: String) {
        viewModelScope.launch {
            val allItems = itemDao.getAllItems() // Assuming you have a method to get all items
            _searchResults.value = allItems.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }
    }
}
