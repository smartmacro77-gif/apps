package com.moderntv.app.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.moderntv.app.ui.theme.ModernTvColors
import kotlinx.coroutines.delay

data class SearchResultUi(val id: String, val title: String, val type: String, val cover: String?, val subtitle: String?)

/**
 * Busca Global — varre TODAS as categorias de uma só vez (live + VOD + series)
 * com debounce 300ms e filtro unificado. D-Pad navega no grid 4 colunas.
 */
@Composable
fun GlobalSearchScreen(
    allItemsProvider: suspend (query: String) -> List<SearchResultUi>,
    onResultClick: (SearchResultUi) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<SearchResultUi>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    val focusSearch = remember { FocusRequester() }

    LaunchedEffect(query) {
        if (query.length < 2) { results = emptyList(); return@LaunchedEffect }
        loading = true
        delay(300) // debounce
        results = try { allItemsProvider(query) } catch (_: Exception) { emptyList() }
        loading = false
    }

    LaunchedEffect(Unit) { focusSearch.requestFocus() }

    Column(Modifier.fillMaxSize().background(ModernTvColors.Background).padding(16.dp)) {
        // Barra de busca
        var focused by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Buscar em TV ao Vivo, Filmes e Séries...", color = Color(0xFF6E6E6E), fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = Color(0xFF9E9E9E)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusSearch)
                .onFocusChanged { focused = it.isFocused }
                .border(if (focused) 1.5.dp else 1.dp, if (focused) Color.White else ModernTvColors.GlassBorder, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1E1E1E),
                unfocusedContainerColor = Color(0xFF1E1E1E),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = ModernTvColors.Primary,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )
        Spacer(Modifier.height(12.dp))

        if (loading) {
            Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ModernTvColors.Primary, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
            }
        }

        if (query.length >= 2 && !loading) {
            Text("${results.size} resultados para \"$query\"", color = Color(0xFF9E9E9E), fontSize = 11.sp, modifier = Modifier.padding(bottom = 8.dp))
        }

        if (results.isEmpty() && query.length >= 2 && !loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nenhum resultado encontrado.", color = Color(0xFF6E6E6E))
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(results) { item ->
                SearchCard(item, onClick = { onResultClick(item) })
            }
        }
    }
}

@Composable
private fun SearchCard(item: SearchResultUi, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .height(220.dp)
            .onFocusChanged { focused = it.isFocused }
            .then(if (focused) Modifier.border(2.dp, Color.White, RoundedCornerShape(12.dp)) else Modifier.border(1.dp, ModernTvColors.GlassBorder, RoundedCornerShape(12.dp)))
            .clip(RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        onClick = onClick
    ) {
        Column(Modifier.fillMaxSize()) {
            AsyncImage(
                model = item.cover,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(140.dp).background(Color(0xFF2A2A2A)),
                contentScale = ContentScale.Crop
            )
            Column(Modifier.padding(10.dp)) {
                Text(item.type.uppercase(), color = ModernTvColors.Primary, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                Text(item.title, color = Color.White, fontSize = 12.sp, maxLines = 2, fontWeight = FontWeight.Medium)
                if (item.subtitle != null) Text(item.subtitle, color = Color(0xFF9E9E9E), fontSize = 10.sp, maxLines = 1)
            }
        }
    }
}
