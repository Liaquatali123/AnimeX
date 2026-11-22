package com.liaquatali.animex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Ink = Color(0xFF0B0D12)
private val Panel = Color(0xFF171B25)
private val Accent = Color(0xFF8B7CFF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AnimeXApp() }
    }
}

@Composable
private fun AnimeXApp() {
    var selectedTab by remember { mutableIntStateOf(0) }
    var query by remember { mutableStateOf("") }
    MaterialTheme(colorScheme = darkColorScheme(primary = Accent, background = Ink, surface = Panel)) {
        Scaffold(
            containerColor = Ink,
            bottomBar = {
                NavigationBar(containerColor = Panel) {
                    NavigationBarItem(selected = selectedTab == 0, onClick = { selectedTab = 0 }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
                    NavigationBarItem(selected = selectedTab == 1, onClick = { selectedTab = 1 }, icon = { Icon(Icons.Default.Search, null) }, label = { Text("Search") })
                    NavigationBarItem(selected = selectedTab == 2, onClick = { selectedTab = 2 }, icon = { Icon(Icons.Default.FavoriteBorder, null) }, label = { Text("My List") })
                }
            }
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp)) {
                Spacer(Modifier.height(18.dp))
                Text("ANIMEX", color = Accent, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 4.sp)
                Spacer(Modifier.height(8.dp))
                Text(when (selectedTab) { 1 -> "Discover anime"; 2 -> "Your collection"; else -> "Anime, your way." }, color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(14.dp))
                if (selectedTab == 1) {
                    OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Search titles") }, singleLine = true)
                    Spacer(Modifier.height(16.dp))
                }
                if (selectedTab == 2) EmptyState("Your list is empty", "Save titles to find them here.")
                else if (selectedTab == 0) {
                    Surface(color = Panel, shape = MaterialTheme.shapes.extraLarge, modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(20.dp)) {
                            Text("YOUR NEXT FAVORITE", color = Accent, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("Explore the anime universe", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(6.dp))
                            Text("A clean home for your anime discovery and personal library.", color = Color.LightGray)
                        }
                    }
                    Spacer(Modifier.height(22.dp))
                    Text("Popular categories", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    val categories = listOf("Action", "Adventure", "Comedy", "Drama", "Fantasy", "Sci-Fi")
                    LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(categories) { category ->
                            Surface(color = Panel, shape = MaterialTheme.shapes.large) {
                                Text(category, color = Color.White, modifier = Modifier.padding(18.dp), fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                } else EmptyState("Catalog not connected", "Connect an authorized metadata provider to load real titles. AnimeX will not show invented posters or placeholder movies.")
            }
        }
    }
}

@Composable
private fun EmptyState(title: String, message: String) {
    Column(Modifier.fillMaxWidth().padding(top = 54.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(message, color = Color.LightGray)
    }
}
