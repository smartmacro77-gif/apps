package com.moderntv.app.ui.live

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.moderntv.app.ui.theme.ModernTvColors

data class LiveGroupUi(val id: String, val name: String, val count: Int)
data class LiveChannelUi(val id: Int, val name: String, val logo: String?, val epgNow: String?, val epgNext: String?, val groupId: String)

/**
 * TV ao Vivo — 3 colunas: Grupos | Canais | Preview+EPG
 * Estética Netflix escura + glassmorphism, mas estrutura Smarters preservada.
 * D-Pad: Cima/Baixo navega, Esquerda/Direita troca coluna, OK abre preview/play.
 */
@Composable
fun LiveTvScreen(
    groups: List<LiveGroupUi>,
    channels: List<LiveChannelUi>,
    selectedGroupId: String?,
    selectedChannel: LiveChannelUi?,
    onGroupSelect: (String) -> Unit,
    onChannelSelect: (LiveChannelUi) -> Unit,
    onPlay: (LiveChannelUi) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(ModernTvColors.Background)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Coluna 1 — Grupos (300dp)
        Card(
            modifier = Modifier.width(300.dp).fillMaxHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141414))
        ) {
            Text("Categorias", modifier = Modifier.padding(16.dp), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Divider(color = Color(0x0FFFFFFF))
            LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(groups) { g ->
                    val isSel = g.id == selectedGroupId
                    var focused by remember { mutableStateOf(false) }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                when {
                                    isSel -> Color(0x14FFFFFF)
                                    focused -> Color(0x0FFFFFFF)
                                    else -> Color.Transparent
                                }
                            )
                            .border(
                                if (isSel) 1.dp else 0.dp,
                                if (isSel) ModernTvColors.Primary else Color.Transparent,
                                RoundedCornerShape(10.dp)
                            )
                            .onFocusChanged { focused = it.isFocused }
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isSel) Box(Modifier.width(3.dp).height(20.dp).clip(RoundedCornerShape(2.dp)).background(ModernTvColors.Primary))
                        Spacer(Modifier.width(if (isSel) 8.dp else 0.dp))
                        Text(g.name, color = if (isSel) Color.White else Color(0xFFBDBDBD), fontSize = 13.sp, maxLines = 1, modifier = Modifier.weight(1f))
                        Text("${g.count}", color = Color(0xFF6E6E6E), fontSize = 11.sp)
                    }
                }
            }
        }

        // Coluna 2 — Canais (420dp)
        Card(
            modifier = Modifier.width(420.dp).fillMaxHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141414))
        ) {
            Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Canais", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.weight(1f))
                Text("${channels.size} canais", color = Color(0xFF6E6E6E), fontSize = 11.sp)
            }
            Divider(color = Color(0x0FFFFFFF))
            LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(channels) { ch ->
                    val isSel = ch.id == selectedChannel?.id
                    var focused by remember { mutableStateOf(false) }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when {
                                    isSel -> Color(0x1AFFFFFF)
                                    focused -> Color(0x0FFFFFFF)
                                    else -> Color(0xFF1E1E1E)
                                }
                            )
                            .border(
                                width = if (isSel || focused) 1.5.dp else 1.dp,
                                color = if (isSel) Color.White else if (focused) Color(0x66FFFFFF) else ModernTvColors.GlassBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .onFocusChanged { focused = it.isFocused }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = ch.logo,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF2A2A2A)),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(ch.name, color = Color.White, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Medium)
                            Spacer(Modifier.height(4.dp))
                            if (ch.epgNow != null) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(Modifier.size(6.dp).clip(RoundedCornerShape(3.dp)).background(ModernTvColors.EpgNow))
                                    Spacer(Modifier.width(6.dp))
                                    Text(ch.epgNow, color = Color(0xFFBDBDBD), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            } else {
                                Text("Sem EPG", color = Color(0xFF6E6E6E), fontSize = 11.sp)
                            }
                            if (ch.epgNext != null) {
                                Text("A seguir: ${ch.epgNext}", color = Color(0xFF6E6E6E), fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                        if (isSel) {
                            Box(Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(ModernTvColors.LiveRed))
                        }
                    }
                }
            }
        }

        // Coluna 3 — Preview + EPG (flex)
        Card(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141414))
        ) {
            if (selectedChannel == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Selecione um canal para pré-visualizar", color = Color(0xFF6E6E6E), fontSize = 13.sp)
                }
            } else {
                Column(Modifier.fillMaxSize().padding(16.dp)) {
                    // Player PiP 16:9
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.Black)
                            .border(1.dp, ModernTvColors.GlassBorder, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Em produção: AndroidView com PlayerView + PlayerWrapper
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("▶ Preview", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(6.dp))
                            Text(selectedChannel.name, color = Color(0xFFBDBDBD), fontSize = 12.sp)
                            Spacer(Modifier.height(10.dp))
                            Button(
                                onClick = { onPlay(selectedChannel) },
                                colors = ButtonDefaults.buttonColors(containerColor = ModernTvColors.Primary),
                                shape = RoundedCornerShape(10.dp)
                            ) { Text("Assistir em Tela Cheia", color = Color.White, fontSize = 12.sp) }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(selectedChannel.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(8.dp))
                    // EPG timeline mock
                    Card(shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Agora", color = ModernTvColors.EpgNow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(selectedChannel.epgNow ?: "Sem informação", color = Color.White, fontSize = 13.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("A seguir", color = Color(0xFF6E6E6E), fontSize = 11.sp)
                            Text(selectedChannel.epgNext ?: "—", color = Color(0xFFBDBDBD), fontSize = 12.sp)
                            Spacer(Modifier.height(12.dp))
                            // Barra de progresso EPG
                            Box(Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFF2A2A2A))) {
                                Box(Modifier.fillMaxWidth(0.42f).fillMaxHeight().background(ModernTvColors.Primary))
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AssistChip(
                            onClick = { onPlay(selectedChannel) },
                            label = { Text("▶ Reproduzir") },
                            colors = AssistChipDefaults.assistChipColors(containerColor = ModernTvColors.Primary, labelColor = Color.White)
                        )
                        AssistChip(
                            onClick = {},
                            label = { Text("♡ Favorito") },
                            colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF1E1E1E), labelColor = Color.White)
                        )
                        AssistChip(
                            onClick = {},
                            label = { Text("EPG Completo") },
                            colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF1E1E1E), labelColor = Color.White)
                        )
                    }
                }
            }
        }
    }
}
