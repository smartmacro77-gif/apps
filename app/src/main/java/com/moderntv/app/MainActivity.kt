package com.moderntv.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.moderntv.app.data.api.XtreamApiFactory
import com.moderntv.app.ui.dashboard.DashboardScreen
import com.moderntv.app.ui.live.LiveChannelUi
import com.moderntv.app.ui.live.LiveGroupUi
import com.moderntv.app.ui.live.LiveTvScreen
import com.moderntv.app.ui.login.LoginScreen
import com.moderntv.app.ui.player.PlayerScreen
import com.moderntv.app.data.player.PlayerWrapper
import com.moderntv.app.ui.search.GlobalSearchScreen
import com.moderntv.app.ui.theme.ModernTvTheme
import com.moderntv.app.ui.theme.ModernTvColors
import com.moderntv.app.ui.vod.VodCarouselUi
import com.moderntv.app.ui.vod.VodItemUi
import com.moderntv.app.ui.vod.VodScreen

class MainActivity : ComponentActivity() {

    private var host by mutableStateOf("")
    private var user by mutableStateOf("")
    private var pass by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Player preservado
        val playerWrapper = PlayerWrapper(this)

        setContent {
            ModernTvTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = ModernTvColors.Background) {
                    var isLogged by remember { mutableStateOf(false) }
                    var selectedNav by remember { mutableStateOf("home") }
                    var playingUrl by remember { mutableStateOf<String?>(null) }
                    var playingTitle by remember { mutableStateOf("") }

                    // Try restore session (EncryptedSharedPreferences)
                    // val prefs = EncryptedSharedPreferences.create(...)

                    if (playingUrl != null) {
                        PlayerScreen(
                            url = playingUrl!!,
                            title = playingTitle,
                            playerWrapper = playerWrapper,
                            onBack = { playingUrl = null }
                        )
                    } else if (!isLogged) {
                        LoginScreen(onAuthenticated = { h, u, p ->
                            host = h; user = u; pass = p
                            // Aqui entra validação real:
                            // val api = XtreamApiFactory.create(h)
                            // api.authenticate(u,p) -> se auth==1 => isLogged=true
                            isLogged = true
                        })
                    } else {
                        DashboardScreen(
                            selectedId = selectedNav,
                            onSelect = { selectedNav = it }
                        ) {
                            when (selectedNav) {
                                "live" -> LiveTvScreen(
                                    groups = sampleGroups,
                                    channels = sampleChannels,
                                    selectedGroupId = "all",
                                    selectedChannel = sampleChannels.firstOrNull(),
                                    onGroupSelect = {},
                                    onChannelSelect = {},
                                    onPlay = { ch ->
                                        // Monta URL Xtream preservando backend
                                        playingUrl = XtreamApiFactory.buildStreamUrl(host, user, pass, ch.id, "m3u8")
                                        playingTitle = ch.name
                                        playerWrapper.playUrl(playingUrl!!)
                                    }
                                )
                                "movies", "series" -> VodScreen(
                                    hero = VodItemUi(1, "Destaque Smarters", "https://via.placeholder.com/300x450", "https://via.placeholder.com/1280x720", "8.4", "2024"),
                                    carousels = sampleCarousels,
                                    onItemClick = {},
                                    onHeroPlay = { item ->
                                        playingUrl = XtreamApiFactory.buildVodUrl(host, user, pass, item.id, "mp4")
                                        playingTitle = item.title
                                        playerWrapper.playUrl(playingUrl!!)
                                    }
                                )
                                "search" -> GlobalSearchScreen(
                                    allItemsProvider = { q ->
                                        // Busca unificada: live + vod + series (exemplo)
                                        // val api = XtreamApiFactory.create(host)
                                        // val lives = api.getLiveStreams(user, pass)
                                        // val vods = api.getVodStreams(user, pass)
                                        // filtrar por q
                                        sampleChannels.filter { it.name.contains(q, true) }.map {
                                            com.moderntv.app.ui.search.SearchResultUi(it.id.toString(), it.name, "TV Ao Vivo", it.logo, it.epgNow)
                                        }
                                    },
                                    onResultClick = {}
                                )
                                else -> VodScreen( // Home = mix de tudo
                                    hero = VodItemUi(99, "Bem-vindo ao Modern TV", "https://via.placeholder.com/300x450", "https://via.placeholder.com/1280x720", "—", "2026"),
                                    carousels = sampleCarousels,
                                    onItemClick = {},
                                    onHeroPlay = {}
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // playerWrapper.release() se necessário
    }
}

// Dados de exemplo para preview (remova e plugue API real)
private val sampleGroups = listOf(
    LiveGroupUi("all", "Todos os Canais", 142),
    LiveGroupUi("1", "Filmes", 24),
    LiveGroupUi("2", "Esportes", 18),
    LiveGroupUi("3", "Notícias", 12),
    LiveGroupUi("4", "Infantil", 16),
)
private val sampleChannels = listOf(
    LiveChannelUi(1, "Globo HD", null, "Jornal Nacional", "Fantástico", "all"),
    LiveChannelUi(2, "ESPN Brasil", null, "Futebol Ao Vivo", "Mesa Redonda", "2"),
    LiveChannelUi(3, "Telecine Premium", null, "Velozes e Furiosos 10", "Missão Impossível", "1"),
    LiveChannelUi(4, "Cartoon Network", null, "Hora de Aventura", "Gumball", "4"),
)
private val sampleCarousels = listOf(
    VodCarouselUi("Continuar Assistindo", List(8) { VodItemUi(it, "Filme ${it+1}", "https://via.placeholder.com/300x450/141414/E50914?text=Filme+${it+1}", null, "7.${it+2}", "2023") }),
    VodCarouselUi("Mais Vistos", List(8) { VodItemUi(10+it, "Série ${it+1}", "https://via.placeholder.com/300x450/1F1F1F/FFFFFF?text=Série+${it+1}", null, "8.${it}", "2024") }),
    VodCarouselUi("Recomendados", List(8) { VodItemUi(20+it, "Ação ${it+1}", "https://via.placeholder.com/300x450/0A0A0B/E50914?text=Ação+${it+1}", null, "8.0", "2022") }),
)
