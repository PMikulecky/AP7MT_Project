package com.example.freezer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.freezer.model.DrawerWithItems
import com.example.freezer.ui.theme.FreezerTheme

class MainActivity : ComponentActivity() {


    private lateinit var viewModel: DrawerViewModel
    private lateinit var apiViewModel: ApiViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DrawerViewModel::class.java)
        apiViewModel = ViewModelProvider(this).get(ApiViewModel::class.java)

        //viewModel.addDrawer("new Drawer 2")
        //viewModel.addItemToDrawer(2, "Potato")

        viewModel.getAllDrawers()
        viewModel.getAllDrawersWithItems()

        setContent {
            FreezerTheme {
                val navController = rememberNavController()

                Scaffold(
                    bottomBar = { BottomNavigationBar(navController) },

                    // ... include other Scaffold parameters if needed
                ) { innerPadding ->
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") { HomeScreen(viewModel, innerPadding) }
                        composable("search") { SearchScreen(viewModel) }
                        composable("profile") { AIScreen(apiViewModel) }
                        // Add other composable routes if necessary
                    }
                }
            }
        }
        viewModel.drawers.observe(this) { drawers ->
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: DrawerViewModel, innerPadding: PaddingValues) {
    //val drawers by viewModel.drawers.observeAsState(listOf())

    val selectedCard = remember { mutableStateOf<DrawerWithItems?>(null) }
    val navController = rememberNavController()

    val drawersWithItems by viewModel.drawersWithItems.observeAsState(listOf())

    val isItemDialogOpen = remember { mutableStateOf(false) }
    val isDrawerDialogOpen = remember { mutableStateOf(false) }
    val isEditDialogOpen = remember { mutableStateOf(false) }


    val foodItemName = remember { mutableStateOf("") }
    val foodItemCount = remember { mutableStateOf("") }
    val selectedDrawer = remember { mutableStateOf<DrawerWithItems?>(null) }
    val drawers = viewModel.drawersWithItems.observeAsState(listOf()).value
    val expanded = remember { mutableStateOf(false) } // State to handle dropdown menu visibility
    val editDrawerName = remember { mutableStateOf("") }



    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = { AddDrawerFAB{isItemDialogOpen.value = true} },
        floatingActionButtonPosition = FabPosition.Center,
        content = { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item { Text(
                "Freezer",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            ) }
            items(drawersWithItems) { drawerWithItems ->
                DrawerCard(
                    drawerWithItems = drawerWithItems,
                    onClick = { selectedCard.value = drawerWithItems }
                )

            }
            item {
                AddDrawer(onClick = {
                    isDrawerDialogOpen.value = true
                })
            }

            item { Spacer(modifier = Modifier.height(150.dp)) }
        }
        if (selectedCard.value != null) {
            selectedCard.value?.let {
                DrawerDetailScreen(
                    drawerWithItems = it,
                    onClose = { selectedCard.value = null },
                    onEdit = {
                        editDrawerName.value = it.drawer.name
                        isEditDialogOpen.value = true
                    },
                    onDeleteItem = { itemId ->
                        viewModel.deleteItem(itemId)
                    }
                )

                EditDrawerDialog(
                    drawer = it,
                    isEditDialogOpen = isEditDialogOpen,
                    editDrawerName = editDrawerName,
                    onDismiss = { isEditDialogOpen.value = false },
                    onSave = { newDrawerName ->
                        viewModel.updateDrawerName(it.drawer.drawerId, newDrawerName)
                        isEditDialogOpen.value = false
                    },
                    onDelete = {

                        viewModel.deleteDrawer(it.drawer.drawerId)
                        selectedCard.value = null
                        isEditDialogOpen.value = false
                    }
                )
            }
        }
    }
    )

    if (isItemDialogOpen.value) {
        AlertDialog(
            onDismissRequest = {
                isItemDialogOpen.value = false
            },
            title = { Text("Add New Item") },
            text = {
                Column {
                    OutlinedTextField(
                        value = foodItemName.value,
                        onValueChange = { foodItemName.value = it },
                        label = { Text("Food Item Name") }
                    )
                    OutlinedTextField(
                        value = foodItemCount.value,
                        onValueChange = {
                            // Update the state to the new value or revert to "1" if it's not a number
                            foodItemCount.value = it.filter { it.isDigit() }
                        },
                        label = { Text("Quantity") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    ExposedDropdownMenuBox(
                        expanded = expanded.value,
                        onExpandedChange = { expanded.value = !expanded.value }
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = selectedDrawer.value?.drawer?.name ?: "Select a drawer",
                            onValueChange = {},
                            label = { Text("Drawer") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
                            },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded.value,
                            onDismissRequest = { expanded.value = false }
                        ) {
                            drawersWithItems.forEach { drawerWithItems ->
                                DropdownMenuItem(
                                    text = { Text(text = drawerWithItems.drawer.name) },
                                    onClick = {
                                        selectedDrawer.value = drawerWithItems
                                        expanded.value = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.addItemToDrawer(
                            selectedDrawer.value?.drawer?.drawerId ?: 0,
                            foodItemName.value,
                            //foodItemCount.value
                        )
                        // Reset the state and close the dialog
                        foodItemName.value = ""
                        //foodItemCount.value = 1
                        selectedDrawer.value = null
                        isItemDialogOpen.value = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { isItemDialogOpen.value = false }
                ) {
                    Text("Cancel")
                }
            }
        )

    }
    if (isDrawerDialogOpen.value) {
        AddDrawerDialog(viewModel, isDrawerDialogOpen)
    }
}

@Composable
fun SearchScreen(viewModel: DrawerViewModel) {
    // State for search query
    val searchQuery = remember { mutableStateOf("") }

    // Observe search results from ViewModel
    val searchResults by viewModel.searchResults.observeAsState(listOf())

    // Update search results in ViewModel based on the query
    LaunchedEffect(searchQuery.value) {
        viewModel.searchItems(searchQuery.value)
    }

    Column {
        // Search Bar
        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = { searchQuery.value = it },
            label = { Text("Search Food Items") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        // List of Search Results
        LazyColumn {
            items(searchResults) { item ->
                Text(
                    text = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                Divider()
            }
        }
    }
}

@Composable
fun AIScreen(apiViewModel: ApiViewModel) {
    // State to hold the input text
    val inputText = remember { mutableStateOf("") }

    // State to hold the response from the API
    val apiResponse = apiViewModel.chatResponse.observeAsState("")
    val isLoading = apiViewModel.isLoading.observeAsState(initial = false)


    Column(modifier = Modifier.padding(16.dp)) {
        // Text field for user input
        OutlinedTextField(
            value = inputText.value,
            onValueChange = { inputText.value = it },
            label = { Text("Enter items") },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Button to trigger API call
        Button(
            onClick = {
                apiViewModel.getChatResponseFromAPI(inputText.value)
            }
        ) {
            Text("Get Response")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display the API response
        Text("Response: ${apiResponse.value}")

        if (isLoading.value) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@Composable
fun AddDrawerFAB(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onClick() },
        containerColor = Color.White
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Drawer"
        )
    }
}

@Composable
fun AddDrawerDialog(viewModel: DrawerViewModel, isDialogOpen: MutableState<Boolean>) {
    val drawerName = remember { mutableStateOf("") }

    if (isDialogOpen.value) {
        AlertDialog(
            onDismissRequest = { isDialogOpen.value = false },
            title = { Text("Add New Drawer") },
            text = {
                OutlinedTextField(
                    value = drawerName.value,
                    onValueChange = { drawerName.value = it },
                    label = { Text("Drawer Name") }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.addDrawer(drawerName.value)
                        drawerName.value = ""
                        isDialogOpen.value = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { isDialogOpen.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun EditDrawerDialog(drawer: DrawerWithItems,
                     isEditDialogOpen: MutableState<Boolean>,
                     editDrawerName: MutableState<String>,
                     onDismiss: () -> Unit,
                     onSave: (String) -> Unit,
                     onDelete: () -> Unit) {
    if (isEditDialogOpen.value) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Edit Drawer") },
            text = {
                OutlinedTextField(
                    value = editDrawerName.value,
                    onValueChange = { editDrawerName.value = it },
                    label = { Text("New Name") }
                )
            },
            confirmButton = {
                TextButton(onClick = { onSave(editDrawerName.value) }) {
                    Text("Save")
                }
            },
            dismissButton = {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = { onDelete() }) {
                        Text("Delete Drawer")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = { onDismiss() }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = navController.currentDestination?.route == "home",
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            label = { Text("Search") },
            selected = navController.currentDestination?.route == "search",
            onClick = { navController.navigate("search") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = navController.currentDestination?.route == "profile",
            onClick = { navController.navigate("profile") }
        )
    }

}

@Composable
fun DrawerCard(drawerWithItems: DrawerWithItems, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 4.dp)
    ) {
        Column {
            Text("Drawer ${drawerWithItems.drawer.name}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
                    .wrapContentWidth(align = Alignment.Start),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                drawerWithItems.items.forEach { item ->
                    Text(item.name,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun AddDrawer(onClick: () -> Unit) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, Color.Gray),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(onClick = onClick)
    ) {
        Text(
            "Add New Drawer",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerDetailScreen(drawerWithItems: DrawerWithItems, onClose: () -> Unit, onEdit: () -> Unit, onDeleteItem: (Int) -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text("Details for ${drawerWithItems.drawer.name}") },
                actions = {
                    TextButton(onClick = onEdit) {
                        Text("Edit")
                    }
                }
            )
            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {
                items(drawerWithItems.items) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.name, style = MaterialTheme.typography.bodyMedium)
                        IconButton(onClick = { onDeleteItem(item.itemId) }) {  // Use item's ID here
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                    Divider()
                }
            }
        }
    }
}

fun deleteItem(itemName: String, drawerId: Int) {
    // Logic to delete the item from the database
}

/*
@Preview(showBackground = true)
@Composable
fun FreezerPreview() {
    FreezerTheme {
        FreezerScreen()
    }
}
 */
