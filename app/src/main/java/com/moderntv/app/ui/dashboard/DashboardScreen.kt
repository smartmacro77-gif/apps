package com.moderntv.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moderntv.app.ui.theme.ModernTvColors

data class NavItem(val id: String, val label: String, val icon: ImageVector)

val NAV_ITEMS = listOf(
    NavItem("home", "Início", Icons.Filled.Home),
    NavItem("live", "TV ao Vivo", Icons.Filled.LiveTv),
    NavItem("movies", "Filmes", Icons.Filled.Movie),
    NavItem("series", "Séries", Icons.Filled.VideoLibrary),
    NavItem("favs", "Favoritos", Icons.Filled.Favorite),
    NavItem("search", "Buscar", Icons.Filled.Search),
    NavItem("settings", "Configurações", Icons.Filled.Settings)
)

/**
 * Dashboard com menu lateral retrátil estilo hambúrguer.
 * D-Pad: ESQUERDA quando foco na borda abre drawer; MENU alterna; BACK fecha.
 */
@Composable
fun DashboardScreen(
    selectedId: String,
    onSelect: (String) -> Unit,
    content: @Composable () -> Unit
) {
    var drawerOpen by remember { mutableStateOf(false) }
    val drawerWidth = if (drawerOpen) 320.dp else 80.dp

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(ModernTvColors.Background)
            .onKeyEvent {
                if (it.type == KeyEventType.KeyDown) {
                    when (it.key) {
                        Key.Menu, Key.DirectionLeftAlt -> { drawerOpen = !drawerOpen; true }
                        Key.Back -> if (drawerOpen) { drawerOpen = false; true } else false
                        else -> false
                    }
                } else false
            }
    ) {
        // Lateral
        Column(
            modifier = Modifier
                .width(drawerWidth)
                .fillMaxHeight()
                .background(Color(0xFF111111))
                .border(1.dp, Color(0x0DFFFFFF), RoundedCornerShape(0.dp))
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Botão hambúrguer
            var hamFocused by remember { mutableStateOf(false) }
            IconButton(
                onClick = { drawerOpen = !drawerOpen },
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (hamFocused) Color(0x14FFFFFF) else Color.Transparent)
                    .border(if (hamFocused) 1.dp else 0.dp, Color.White, RoundedCornerShape(10.dp))
                    .onFocusChanged { hamFocused = it.isFocused }
                    .focusable()
            ) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
            }
            Spacer(Modifier.height(18.dp))

            NAV_ITEMS.forEach { item ->
                val isSelected = item.id == selectedId
                var focused by remember { mutableStateOf(false) }
                val bg = when {
                    isSelected -> ModernTvColors.Primary
                    focused -> Color(0x1FFFFFFF)
                    else -> Color.Transparent
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(bg)
                        .border(if (focused && !isSelected) 1.dp else 0.dp, Color.White, RoundedCornerShape(10.dp))
                        .onFocusChanged { focused = it.isFocused }
                        .focusable()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(item.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                    if (drawerOpen) {
                        Spacer(Modifier.width(12.dp))
                        Text(item.label, color = Color.White, fontSize = 13.sp, maxLines = 1)
                        if (isSelected) {
                            Spacer(Modifier.weight(1f))
                            Box(Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(Color.White))
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))
            if (drawerOpen) {
                Text("v2.0 • Netflix TV Skin", color = Color(0xFF6E6E6E), fontSize = 10.sp)
            }
        }

        // Conteúdo principal
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(ModernTvColors.Background)
                .padding(16.dp)
        ) {
            content()
        }
    }
}
