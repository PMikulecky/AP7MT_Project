package com.example.freezer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.freezer.ui.theme.FreezerTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: DrawerViewModel
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DrawerViewModel::class.java)

        viewModel.getAllDrawers()

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
    private fun showAddItemDialog() {

    }

    private fun showAddDrawerDialog() {

    }
}

@Composable
fun FreezerScreen(viewModel: DrawerViewModel) {
    val drawers by viewModel.drawers.observeAsState(listOf())

    Scaffold(
        bottomBar = { BottomNavigationBar() },
        floatingActionButton = { AddDrawerFAB() },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(drawers) { drawer ->
                DrawerCard(drawerNumber = drawer.drawerId, items = drawer.name)
                // Optionally add an interaction to each card
            }

            item { Spacer(modifier = Modifier.height(150.dp)) }
        }
    }
}

@Composable
fun AddDrawerFAB() {
    FloatingActionButton(
        onClick = { /* Handle add drawer action */ },
        containerColor = Color.White
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Drawer"
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
fun DrawerCard(drawerNumber: Int, items: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Drawer $drawerNumber", style = MaterialTheme.typography.titleMedium)
            Text(items, style = MaterialTheme.typography.bodyMedium)
        }
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
