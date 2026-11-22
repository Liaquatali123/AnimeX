package com.liaquatali.animex

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.liaquatali.animex.data.AnimeTitle
import com.liaquatali.animex.data.FavoriteEntity
import com.liaquatali.animex.data.WatchHistoryEntity
import com.liaquatali.animex.ui.AnimeViewModel

private val Background = Color(0xFF080A10)
private val SurfaceDark = Color(0xFF141824)
private val Violet = Color(0xFF9B8CFF)
private val Muted = Color(0xFF9AA3B5)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AnimeXApp() }
    }
}

@Composable
private fun AnimeXApp(vm: AnimeViewModel = viewModel()) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var query by remember { mutableStateOf("") }
    var selectedTitle by remember { mutableStateOf<AnimeTitle?>(null) }
    var showMediaDialog by remember { mutableStateOf(false) }
    var streamUrl by remember { mutableStateOf("") }
    var streamUrlError by remember { mutableStateOf(false) }
    val favorites by vm.favorites.collectAsState()
    val history by vm.history.collectAsState()
    val catalogItems = vm.catalog.collectAsLazyPagingItems()
    val searchItems = vm.searchResults.collectAsLazyPagingItems()
    val context = androidx.compose.ui.platform.LocalContext.current

    val localVideoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try { context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: SecurityException) { }
            context.startActivity(Intent(context, LocalPlayerActivity::class.java).setData(uri))
        }
    }

    MaterialTheme(colorScheme = darkColorScheme(primary = Violet, background = Background, surface = SurfaceDark)) {
        Scaffold(
            containerColor = Background,
            bottomBar = {
                NavigationBar(containerColor = SurfaceDark, contentColor = Color.White) {
                    NavigationBarItem(selected = selectedTab == 0, onClick = { selectedTab = 0 }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
                    NavigationBarItem(selected = selectedTab == 1, onClick = { selectedTab = 1 }, icon = { Icon(Icons.Default.Search, null) }, label = { Text("Search") })
                    NavigationBarItem(selected = selectedTab == 2, onClick = { selectedTab = 2 }, icon = { Icon(Icons.Default.FavoriteBorder, null) }, label = { Text("My List") })
                    NavigationBarItem(selected = selectedTab == 3, onClick = { selectedTab = 3 }, icon = { Icon(Icons.Default.History, null) }, label = { Text("History") })
                }
            }
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding)) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("ANIMEX", color = Violet, fontSize = 12.sp, fontWeight = FontWeight.Black, letterSpacing = 4.sp)
                        Text(when (selectedTab) { 1 -> "Find your next anime"; 2 -> "Your collection"; 3 -> "Continue watching"; else -> "Anime, your way." }, color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = { showMediaDialog = true }) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play local video", tint = Violet)
                    }
                }
                if (selectedTab == 1) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
                        placeholder = { Text("Search titles") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { vm.search(query) }),
                        trailingIcon = { IconButton(onClick = { vm.search(query) }) { Icon(Icons.Default.Search, null) } }
                    )
                    Spacer(Modifier.height(12.dp))
                }
                if (selectedTab == 0) {
                    Surface(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp), shape = RoundedCornerShape(26.dp), color = SurfaceDark) {
                        Box(Modifier.background(Brush.linearGradient(listOf(Color(0xFF30265B), SurfaceDark))).padding(22.dp)) {
                            Column {
                                Text("DISCOVER SOMETHING NEW", color = Violet, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                                Spacer(Modifier.height(9.dp))
                                Text("Your anime universe.", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
                                Spacer(Modifier.height(6.dp))
                                Text("Explore titles, save favorites and play your own videos.", color = Muted, fontSize = 13.sp)
                                Spacer(Modifier.height(14.dp))
                                Button(onClick = { selectedTab = 1 }, colors = ButtonDefaults.buttonColors(containerColor = Violet, contentColor = Background)) { Text("Explore catalog") }
                            }
                        }
                    }
                }
                Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(if (selectedTab == 2) "Saved titles" else if (selectedTab == 1) "Search results" else if (selectedTab == 3) "Recently played" else "Popular titles", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    if (selectedTab == 0) TextButton(onClick = { catalogItems.refresh() }) { Text("Refresh", color = Violet) }
                }
                when (selectedTab) {
                    2 -> if (favorites.isEmpty()) EmptyState("Nothing saved yet", "Tap the heart on any title to keep it here.")
                         else TitleGrid(favorites.map { it.toTitle() }, favorites.map { it.id }.toSet(), onOpen = { selectedTitle = it }, onFavorite = { vm.toggleFavorite(it) })
                    3 -> if (history.isEmpty()) EmptyState("No watch history", "Videos you play will appear here with their saved position.")
                         else HistoryList(history) { entry -> context.startActivity(Intent(context, LocalPlayerActivity::class.java).setData(Uri.parse(entry.mediaUri))) }
                    1 -> if (query.isBlank()) EmptyState("Search anime", "Enter a title above and tap the search icon.")
                         else PagedTitleGrid(searchItems, favorites.map { it.id }.toSet(), onOpen = { selectedTitle = it }, onFavorite = { vm.toggleFavorite(it) })
                    else -> PagedTitleGrid(catalogItems, favorites.map { it.id }.toSet(), onOpen = { selectedTitle = it }, onFavorite = { vm.toggleFavorite(it) })
                }
            }
        }

        if (showMediaDialog) {
            AlertDialog(
                onDismissRequest = { showMediaDialog = false },
                containerColor = SurfaceDark,
                title = { Text("Open media", color = Color.White) },
                text = {
                    Column {
                        Button(onClick = { showMediaDialog = false; localVideoPicker.launch(arrayOf("video/*")) }, modifier = Modifier.fillMaxWidth()) {
                            Text("Choose a video on this device")
                        }
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = streamUrl,
                            onValueChange = { streamUrl = it; streamUrlError = false },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Authorized HTTPS MP4 / HLS URL") },
                            singleLine = true,
                            isError = streamUrlError
                        )
                        if (streamUrlError) Text("Enter a valid HTTPS media URL.", color = MaterialTheme.colorScheme.error)
                        Text("Only play media you own or are authorized to access.", color = Muted, fontSize = 11.sp)
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val uri = runCatching { Uri.parse(streamUrl.trim()) }.getOrNull()
                        if (uri?.scheme == "https" && !uri.host.isNullOrBlank()) {
                            context.startActivity(Intent(context, LocalPlayerActivity::class.java).setData(uri))
                            showMediaDialog = false
                            streamUrlError = false
                        } else streamUrlError = true
                    }) { Text("Play stream", color = Violet) }
                },
                dismissButton = { TextButton(onClick = { showMediaDialog = false }) { Text("Cancel", color = Muted) } }
            )
        }

        selectedTitle?.let { title ->
            AlertDialog(
                onDismissRequest = { selectedTitle = null },
                containerColor = SurfaceDark,
                title = { Text(title.title, color = Color.White, fontWeight = FontWeight.Bold) },
                text = { Column {
                    title.poster_url?.let { AsyncImage(model = it, contentDescription = title.title, modifier = Modifier.fillMaxWidth().height(180.dp)) }
                    Spacer(Modifier.height(10.dp))
                    Text(title.overview.ifBlank { "No synopsis is available from the metadata provider." }, color = Muted)
                    title.release_date?.let { Text("Release: $it", color = Violet, modifier = Modifier.padding(top = 8.dp)) }
                } },
                confirmButton = { TextButton(onClick = { vm.toggleFavorite(title) }) { Text("Save / remove", color = Violet) } },
                dismissButton = { TextButton(onClick = { selectedTitle = null }) { Text("Close", color = Muted) } }
            )
        }
    }
}


@Composable
private fun HistoryList(entries: List<WatchHistoryEntity>, onResume: (WatchHistoryEntity) -> Unit) {
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        entries.forEach { entry ->
            val progress = if (entry.durationMs > 0) ((entry.positionMs * 100) / entry.durationMs).coerceIn(0, 100) else 0
            Surface(
                modifier = Modifier.fillMaxWidth().clickable { onResume(entry) },
                shape = RoundedCornerShape(16.dp),
                color = SurfaceDark
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(entry.displayName, color = Color.White, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(5.dp))
                    Text("$progress% watched · Tap to resume", color = Muted, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun PagedTitleGrid(pagedItems: androidx.paging.compose.LazyPagingItems<AnimeTitle>, favoriteIds: Set<String>, onOpen: (AnimeTitle) -> Unit, onFavorite: (AnimeTitle) -> Unit) {
    if (pagedItems.loadState.refresh is LoadState.Loading && pagedItems.itemCount == 0) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Violet) }
        return
    }
    if (items.loadState.refresh is LoadState.Error && items.itemCount == 0) {
        EmptyState("Catalog unavailable", "Check the backend URL, internet connection and provider configuration.")
        return
    }
    if (items.itemCount == 0) {
        EmptyState("No titles found", "The provider returned no matching titles.")
        return
    }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 145.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(items.itemCount) { index ->
            val title = pagedItems[index]
            if (title != null) TitleCard(title, title.id in favoriteIds, onOpen, onFavorite)
        }
        if (items.loadState.append is LoadState.Loading) item { CircularProgressIndicator(color = Violet) }
    }
}

@Composable
private fun TitleGrid(titles: List<AnimeTitle>, favoriteIds: Set<String>, onOpen: (AnimeTitle) -> Unit, onFavorite: (AnimeTitle) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 145.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(titles, key = { it.id }) { title -> TitleCard(title, title.id in favoriteIds, onOpen, onFavorite) }
    }
}

@Composable
private fun TitleCard(title: AnimeTitle, isFavorite: Boolean, onOpen: (AnimeTitle) -> Unit, onFavorite: (AnimeTitle) -> Unit) {
    Column {
        Box {
            Surface(shape = RoundedCornerShape(18.dp), color = SurfaceDark, modifier = Modifier.fillMaxWidth().height(205.dp).clickable { onOpen(title) }) {
                if (title.poster_url != null) AsyncImage(model = title.poster_url, contentDescription = title.title, modifier = Modifier.fillMaxSize())
                else Box(contentAlignment = Alignment.Center) { Text("POSTER UNAVAILABLE", color = Muted, fontSize = 11.sp) }
            }
            IconButton(onClick = { onFavorite(title) }, modifier = Modifier.align(Alignment.TopEnd)) {
                Icon(if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = "Favorite", tint = if (isFavorite) Color(0xFFFF6B91) else Color.White)
            }
        }
        Spacer(Modifier.height(7.dp))
        Text(title.title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 2)
        Text(title.release_date?.take(4) ?: "Anime", color = Muted, fontSize = 11.sp)
    }
}

@Composable
private fun EmptyState(title: String, message: String) {
    Box(Modifier.fillMaxWidth().heightIn(min = 220.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(28.dp)) {
            Text(title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(message, color = Muted)
        }
    }
}

private fun FavoriteEntity.toTitle() = AnimeTitle(id = id, title = title, poster_url = posterUrl, overview = overview)
