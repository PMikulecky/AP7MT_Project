package com.example.freezer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.view.ContextThemeWrapper
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.freezer.model.DrawerWithItems
import com.example.freezer.ui.theme.FreezerTheme
import java.time.LocalDate

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
                        composable("profile") { AIScreen(apiViewModel, viewModel) }
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
        AddItemDialog(viewModel = viewModel, isItemDialogOpen = isItemDialogOpen)

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

    val selectedCard = remember { mutableStateOf<DrawerWithItems?>(null) }
    val drawerWithItemsLiveData = remember { mutableStateOf<LiveData<DrawerWithItems>?>(null) }



    // Update search results in ViewModel based on the query
    LaunchedEffect(searchQuery.value) {
        viewModel.searchItems(searchQuery.value)
    }

    LaunchedEffect(drawerWithItemsLiveData.value) {
        drawerWithItemsLiveData.value?.observeForever { drawerWithItems ->
            selectedCard.value = drawerWithItems
        }
    }

    if (selectedCard.value != null) {
        selectedCard.value?.let { drawerWithItems ->
            DrawerDetailScreen(
                drawerWithItems = drawerWithItems,
                onClose = { selectedCard.value = null },
                // Add your implementations for onEdit and onDeleteItem
                onEdit = { /* ... */ },
                onDeleteItem = { /* ... */ }
            )
        }
    } else {
        Column {
            Text(
                "Freezer",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
            //Spacer(modifier = Modifier.height(16.dp))
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable {
                                drawerWithItemsLiveData.value = viewModel.getDrawerWithItemsForItem(item)
                            },
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = item.name)
                        Text(text = "${item.drawerId}")
                    }
                    Divider()
                }
            }
        }
    }
}

@Composable
fun AIScreen(apiViewModel: ApiViewModel, viewModel: DrawerViewModel) {
    // State to hold the input text
    val inputText = remember { mutableStateOf("") }

    // State to hold the response from the API
    val apiResponse = apiViewModel.chatResponse.observeAsState("")
    val isLoading = apiViewModel.isLoading.observeAsState(initial = false)

    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        viewModel.formatItemsForAPI()
    }

    val formattedItems = viewModel.formattedItems.observeAsState("")

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = { FloatingActionButton(
            onClick = {
                val prompt = formattedItems.value
                apiViewModel.getChatResponseFromAPI(prompt)
            },
            modifier = Modifier.size(width = 170.dp, height = 56.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Star"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Generate recipe")
            }
        } },
        floatingActionButtonPosition = FabPosition.Center,
        content = {innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {

            Spacer(modifier = Modifier.height(16.dp))

            // Display the API response
            Text(apiResponse.value)

            if (isLoading.value) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
    )

}

@Composable
fun AddDrawerFAB(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onClick() },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier
            .size(120.dp)  // Set the size of the button
            .padding(8.dp)  // Add some padding for spacing
            .clip(CircleShape)

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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemDialog(viewModel: DrawerViewModel,isItemDialogOpen: MutableState<Boolean>){
    val foodItemName = remember { mutableStateOf("") }
    val foodItemCount = remember { mutableStateOf("") }
    val quantityType = remember { mutableStateOf("pieces") }
    val selectedDrawer = remember { mutableStateOf<DrawerWithItems?>(null) }
    val expanded = remember { mutableStateOf(false) }
    val quantityTypeExpanded = remember { mutableStateOf(false) }
    val drawersWithItems by viewModel.drawersWithItems.observeAsState(listOf())


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
                    label = { Text("Food Item Name") },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Row{

                    OutlinedTextField(
                        value = foodItemCount.value,
                        onValueChange = {
                            // Update the state to the new value or revert to "1" if it's not a number
                            foodItemCount.value = it.filter { it.isDigit() }
                        },
                        label = { Text("Quantity") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp)
                            .padding(end = 8.dp)
                    )
                    ExposedDropdownMenuBox(
                        expanded = quantityTypeExpanded.value,
                        onExpandedChange = { quantityTypeExpanded.value = !quantityTypeExpanded.value },
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp)
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = quantityType.value,
                            onValueChange = {},
                            label = { Text("Type") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = quantityTypeExpanded.value)
                            },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = quantityTypeExpanded.value,
                            onDismissRequest = { quantityTypeExpanded.value = false }
                        ) {
                            listOf("pieces", "grams", "milliliters").forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        quantityType.value = type
                                        quantityTypeExpanded.value = false
                                    }
                                )
                            }
                        }
                    }
                }

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
                        modifier = Modifier
                            .menuAnchor()
                            .padding(vertical = 8.dp)
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
                    val currentDate = LocalDate.now()
                    viewModel.addItemToDrawer(
                        selectedDrawer.value?.drawer?.drawerId ?: 0,
                        foodItemName.value,
                        foodItemCount.value,
                        quantityType.value,
                        currentDate // Pass the current date
                    )
                    // Reset the state and close the dialog
                    foodItemName.value = ""
                    foodItemCount.value = 1.toString()
                    quantityType.value = "pieces"
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
@Composable
fun BottomNavigationBar(navController: NavController) {


    NavigationBar{
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

@OptIn(ExperimentalLayoutApi::class)
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
            .padding(vertical = 4.dp),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column {
            Text("Drawer ${drawerWithItems.drawer.name}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
            FlowRow(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
                    .wrapContentWidth(align = Alignment.Start),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                drawerWithItems.items.forEach { item ->
                    Text(item.name,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    val suffix = when (item.quantityType) {
                        "pieces" -> "x,"
                        "grams" -> "g,"
                        "milliliters" -> "ml,"
                        else -> ""
                    }
                    val itemCountText = "${item.itemCount}$suffix"

                    Text(itemCountText,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(end = 8.dp))

                }
            }
        }
    }
}

@Composable
fun AddDrawer(onClick: () -> Unit) {
        OutlinedCard(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
            border = BorderStroke(1.dp, Color.Gray),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable(onClick = onClick)
        ) {
            Text(
                "+  Add New Drawer",
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
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(drawerWithItems.items.size) { index ->
                    val item = drawerWithItems.items[index]
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        border = BorderStroke(1.dp, Color.LightGray)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 16.dp)
                        ) {
                            Text(item.name, style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 16.dp))

                            Spacer(modifier = Modifier.height(4.dp))
                            val suffix = when (item.quantityType) {
                                "pieces" -> "x"
                                "grams" -> "g"
                                "milliliters" -> "ml"
                                else -> ""
                            }
                            val itemCountText = "${item.itemCount}$suffix"
                            Text(itemCountText,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .padding(horizontal = 16.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(16.dp))
                            Row {
                                Column (
                                    modifier = Modifier.padding(start = 16.dp)
                                ){
                                    Text("Added on:", style = MaterialTheme.typography.bodySmall)
                                    Text("${item.dateAdded}", style = MaterialTheme.typography.bodySmall)
                                }
                                Spacer(modifier = Modifier.width(50.dp))

                                IconButton(onClick = { onDeleteItem(item.itemId) },
                                    modifier = Modifier.padding(end = 16.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
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
