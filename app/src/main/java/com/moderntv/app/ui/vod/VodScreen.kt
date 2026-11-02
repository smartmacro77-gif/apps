package com.moderntv.app.ui.vod

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.moderntv.app.ui.theme.ModernTvColors

data class VodItemUi(val id: Int, val title: String, val cover: String?, val backdrop: String?, val rating: String?, val year: String?)
data class VodCarouselUi(val title: String, val items: List<VodItemUi>)

/**
 * Filmes e Séries — carrosséis horizontais cinematográficos estilo Netflix TV.
 * Mantém metadados Smarters (capa, sinopse, temporadas) mas em layout Netflix.
 * D-Pad: Esquerda/Direita no carrossel, Cima/Baixo troca carrossel, OK abre detalhe.
 */
@Composable
fun VodScreen(
    hero: VodItemUi?,
    carousels: List<VodCarouselUi>,
    onItemClick: (VodItemUi) -> Unit,
    onHeroPlay: (VodItemUi) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(ModernTvColors.Background)
    ) {
        // Hero Billboard
        if (hero != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1A1A1A))
            ) {
                AsyncImage(
                    model = hero.backdrop ?: hero.cover,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0x990A0A0B), Color(0xFF0A0A0B))
                            )
                        )
                )
                Column(
                    modifier = Modifier.align(Alignment.BottomStart).padding(24.dp).fillMaxWidth(0.55f)
                ) {
                    Text("★ ${hero.rating ?: "—"}  •  ${hero.year ?: ""}", color = Color(0xFFE5E5E5), fontSize = 11.sp, letterSpacing = 1.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(hero.title, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp, lineHeight = 30.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Disponível em VOD • Herdado do catálogo Smarters com metadados completos.", color = Color(0xFFBDBDBD), fontSize = 12.sp)
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { onHeroPlay(hero) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp)
                        ) { Text("▶ Assistir", fontWeight = FontWeight.Bold) }
                        Button(
                            onClick = { onItemClick(hero) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFFFFF), contentColor = Color.White),
                            shape = RoundedCornerShape(8.dp)
                        ) { Text("ⓘ Mais informações") }
                    }
                }
            }
            Spacer(Modifier.height(18.dp))
        }

        // Carrosséis
        carousels.forEach { carousel ->
            Text(carousel.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
            ) {
                items(carousel.items) { item ->
                    VodPosterCard(item = item, onClick = { onItemClick(item) })
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
fun VodPosterCard(item: VodItemUi, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    val scale = if (focused) 1.08f else 1f
    Column(
        modifier = Modifier
            .width(160.dp)
            .onFocusChanged { focused = it.isFocused }
            .then(
                if (focused) Modifier.border(2.dp, Color.White, RoundedCornerShape(12.dp))
                else Modifier.border(1.dp, ModernTvColors.GlassBorder, RoundedCornerShape(12.dp))
            )
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E1E1E)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = item.cover,
            contentDescription = item.title,
            modifier = Modifier
                .width(160.dp)
                .height(240.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF2A2A2A)),
            contentScale = ContentScale.Crop
        )
        Column(Modifier.padding(8.dp).fillMaxWidth()) {
            Text(item.title, color = Color.White, fontSize = 11.sp, maxLines = 2, lineHeight = 13.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (item.rating != null) {
                    Text("★ ${item.rating}", color = Color(0xFFFFC107), fontSize = 10.sp)
                }
                Text(item.year ?: "", color = Color(0xFF6E6E6E), fontSize = 10.sp)
            }
        }
    }
}
