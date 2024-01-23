package com.example.freezer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
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
import com.example.freezer.model.DrawerWithItems
import com.example.freezer.ui.theme.FreezerTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: DrawerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DrawerViewModel::class.java)

        //viewModel.addDrawer("new Drawer 2")
        //viewModel.addItemToDrawer(2, "Potato")

        viewModel.getAllDrawers()
        viewModel.getAllDrawersWithItems()

        setContent {
            FreezerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FreezerScreen(viewModel)
                }
            }
        }
        viewModel.drawers.observe(this) { drawers ->
            // Update your UI here based on the list of drawers
        }


    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreezerScreen(viewModel: DrawerViewModel) {
    //val drawers by viewModel.drawers.observeAsState(listOf())

    val drawersWithItems by viewModel.drawersWithItems.observeAsState(listOf())

    val isItemDialogOpen = remember { mutableStateOf(false) }
    val isDrawerDialogOpen = remember { mutableStateOf(false) }


    val foodItemName = remember { mutableStateOf("") }
    val foodItemCount = remember { mutableStateOf("") }
    val selectedDrawer = remember { mutableStateOf<DrawerWithItems?>(null) }
    val drawers = viewModel.drawersWithItems.observeAsState(listOf()).value
    val expanded = remember { mutableStateOf(false) } // State to handle dropdown menu visibility

    AddDrawerDialog(viewModel, isDrawerDialogOpen)

    Scaffold(
        bottomBar = { BottomNavigationBar() },
        floatingActionButton = { AddDrawerFAB { isItemDialogOpen.value = true } },
        floatingActionButtonPosition = FabPosition.Center
        ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(drawersWithItems) { drawerWithItems ->
                DrawerCard(drawerWithItems = drawerWithItems)
            }
            item {
                AddDrawer(onClick = {
                    isDrawerDialogOpen.value = true
                })
            }

            item { Spacer(modifier = Modifier.height(150.dp)) }
        }
    }


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
fun BottomNavigationBar() {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = false,
            onClick = { /* Handle Home click */ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            label = { Text("Search") },
            selected = false,
            onClick = { /* Handle Search click */ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = false,
            onClick = { /* Handle Profile click */ }
        )
    }

}

@Composable
fun DrawerCard(drawerWithItems: DrawerWithItems) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 8.dp)
    ) {
        Column {
            Text("Drawer ${drawerWithItems.drawer.name}",
                style = MaterialTheme.typography.titleLarge,
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

/*
@Preview(showBackground = true)
@Composable
fun FreezerPreview() {
    FreezerTheme {
        FreezerScreen()
    }
}
 */
