package com.example.androidhw1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.collections.filter
import androidx.compose.material3.Button

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AnimeApp()
                }
            }
        }
    }
}

enum class WatchStatus { Planned, Watching, Done }
data class Anime(
    val id: Int,
    val title: String,
    val year: Int,
    val genre: String,
    val episodes: Int,
    var status: WatchStatus = WatchStatus.Planned
)

val sampleAnimeList = listOf(
    Anime(1, "Naruto", 2000, "mech", 720),
    Anime(2, "Jujutsu kaisen", 2000, "mech", 72),
    Anime(3, "Dr.Stone", 2000, "mech", 80),
    Anime(5, "Blue lock", 2000, "mech", 44),
    Anime(6, "Demon slayer", 2000, "mech", 120),
    Anime(7, "Dragon Ball", 2000, "mech", 800)
)

class AnimeStateHolder(
    private val allAnime: List<Anime>
) {
    var searchQuery by mutableStateOf("")
    var filterStatus by mutableStateOf<WatchStatus?>(null)

    val filteredAnime: List<Anime>
        get() = allAnime
            .filter { anime ->
                searchQuery.isBlank() || anime.title.contains(searchQuery, ignoreCase = true)
            }
            .filter { anime ->
                filterStatus == null || anime.status == filterStatus
            }

    fun onSearchChange(newValue: String) {
        searchQuery = newValue
    }

    fun onFilterChange(newStatus: WatchStatus?) {
        filterStatus = newStatus
    }

    fun nextStatus(anime: Anime) {
        anime.status = when (anime.status) {
            WatchStatus.Planned -> WatchStatus.Watching
            WatchStatus.Watching -> WatchStatus.Done
            WatchStatus.Done -> WatchStatus.Planned
        }
    }

    val totalCount get() = allAnime.size
    val plannedCount get() = allAnime.count { it.status == WatchStatus.Planned }
    val watchingCount get() = allAnime.count { it.status == WatchStatus.Watching }
    val doneCount get() = allAnime.count { it.status == WatchStatus.Done }
}

@Composable
fun AnimeApp() {
    val stateHolder = remember {
        AnimeStateHolder(sampleAnimeList)
    }

    AnimeListScreen(stateHolder)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeListScreen(state: AnimeStateHolder) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Watchlist Tracker") }) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = state::onSearchChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search by title") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            FilterRow(
                onFilterChange = state::onFilterChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Total: ${state.totalCount}  Planned: ${state.plannedCount}  Watching: ${state.watchingCount}  Done: ${state.doneCount}"
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.filteredAnime.isEmpty()) {
                Text("Nothing found!")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.filteredAnime, key = { it.id }) { anime ->
                        AnimeCard(
                            anime = anime,
                            onNextStatus = { state.nextStatus(anime) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterRow(
    onFilterChange: (WatchStatus?) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

        Button(onClick = { onFilterChange(null) }) {
            Text("All")
        }

        WatchStatus.entries.forEach { status ->
            Button(onClick = { onFilterChange(status) }) {
                Text(status.name)
            }
        }
    }
}

@Composable
fun AnimeCard(anime: Anime, onNextStatus: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = anime.title, fontWeight = FontWeight.Bold)
            Text("${anime.year} - ${anime.genre}")
            Text("Episodes ${anime.episodes}")
            Text("Status: ${anime.status}")
        }
        Button(onClick = onNextStatus) {
            Text("Next status")
        }
    }
}