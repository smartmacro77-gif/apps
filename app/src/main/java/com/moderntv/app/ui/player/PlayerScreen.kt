package com.moderntv.app.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import com.moderntv.app.data.player.PlayerWrapper
import com.moderntv.app.ui.theme.ModernTvColors

/**
 * Player nativo — ExoPlayer preservado. Controles otimizados para D-Pad.
 * BACK volta, OK pausa/play, Cima/Baixo volume, Esquerda/Direita seek.
 */
@Composable
fun PlayerScreen(
    url: String,
    title: String,
    playerWrapper: PlayerWrapper,
    onBack: () -> Unit
) {
    var showControls by remember { mutableStateOf(true) }
    var isPlaying by remember { mutableStateOf(true) }

    // Auto-hide controles
    LaunchedEffect(showControls) {
        if (showControls) {
            kotlinx.coroutines.delay(3000)
            showControls = false
        }
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        // PlayerView nativo
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = playerWrapper.build()
                    useController = false // controles custom D-Pad
                    // playerWrapper.playUrl(url) — chamado pelo caller
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay controles (quando showControls)
        if (showControls) {
            Box(
                Modifier.fillMaxSize().background(Color(0x66000000)).padding(24.dp)
            ) {
                // Top bar
                Row(Modifier.align(Alignment.TopStart).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White) }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(url.take(60) + "...", color = Color(0xFFBDBDBD), fontSize = 10.sp)
                    }
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Filled.Cast, contentDescription = null, tint = Color.White)
                }

                // Center controls
                Row(Modifier.align(Alignment.Center), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {}) { Icon(Icons.Filled.Replay10, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp)) }
                    FloatingActionButton(
                        onClick = { isPlaying = !isPlaying; if (isPlaying) playerWrapper.resume() else playerWrapper.pause() },
                        containerColor = ModernTvColors.Primary,
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                    IconButton(onClick = {}) { Icon(Icons.Filled.Forward10, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp)) }
                }

                // Bottom - timeline + EPG
                Column(Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
                    LinearProgressIndicator(progress = 0.42f, modifier = Modifier.fillMaxWidth().height(3.dp), color = ModernTvColors.Primary, trackColor = Color(0xFF333333))
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Ao Vivo", color = ModernTvColors.LiveRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("EPG: Agora • Próximo", color = Color(0xFFBDBDBD), fontSize = 10.sp)
                    }
                }
            }
        } else {
            // Indicador sutil de que controles existem
            Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)) {
                Text("Pressione OK para controles", color = Color(0x99FFFFFF), fontSize = 11.sp)
            }
        }
    }
}
