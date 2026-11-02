# Fluxo e Arquitetura — Detalhe Técnico

## 1. Fluxo de Login (sem QR)

```
[ LoginScreen ] — 3 campos (Host, User, Pass)
      |
      |  Botão CONECTAR (foco glow) -> XtreamApiService.authenticate()
      |  -> GET {host}/player_api.php?username={u}&password={p}
      |  -> user_info.auth == 1 ? SUCCESS : ERROR
      |
      v
[ EncryptedSharedPreferences ] salva host/user/pass (AES256)
      |
      v
[ DashboardScreen ] — drawer fechado 80dp, aberto 320dp
```

**Removido completamente:** `ActivationActivity`, `QrActivity`, `get_activation_code`, rotas `/qr`, deep links de código. Verificado no projeto: não há mais referência a QR.

## 2. Navegação D-Pad — Regra de Ouro

- Cada `Composable` focável tem `Modifier.focusable()` + `FocusRequester`
- `onKeyEvent` intercepta MENU/BACK/DPAD sem propagar para sistema
- `BringIntoViewRequester` garante scroll automático ao focar card fora da viewport
- **Teste obrigatório:** Emulador TV 1080p + D-Pad físico: percorrer todas as telas sem foco perdido

### Mapeamento XML/Compose antigo -> novo
| Antigo Smarters | Novo Netflix TV |
|----------------|----------------|
| `DrawerLayout` + `NavigationView` | `ModalNavigationDrawer` Compose custom (80/320dp) |
| `RecyclerView` vertical | `LazyColumn` + `TvLazyColumn` |
| `RecyclerView` horizontal | `TvLazyRow` + scale 1.08 ao focar |
| `CardView` | `Card` com `border 1dp Glass + blur` |
| `ProgressBar` | `CircularProgressIndicator` com shimmer |

## 3. TV ao Vivo — Estrutura preservada

```
XtreamApiService.getLiveCategories() -> grupos esquerda
XtreamApiService.getLiveStreams(category_id) -> canais centro
XtreamApiService.getShortEpg(stream_id) -> EPG direita + preview

Build URL: http://{host}/live/{user}/{pass}/{streamId}.m3u8
Player: PlayerWrapper (ExoPlayer principal, IJK fallback via libijkplayer.so)
```

**Layout 3 colunas:** `Row` com `width` fixo 300dp + 420dp + weight(1f). Cada coluna é `Card` com fundo `#141414` e borda glass.

## 4. Filmes & Séries — Carrosséis

```
XtreamApiService.getVodCategories() / getSeriesCategories() -> carrosséis por gênero
XtreamApiService.getVodStreams(category_id) / getSeries(category_id) -> itens
XtreamApiService.getVodInfo(vod_id) / getSeriesInfo(series_id) -> detalhe
```

**Hero Billboard:** backdrop + gradiente vertical + botões Assistir / Mais Info  
**Carrossel:** `LazyRow` com `Poster 160x240` + scale ao focar + sombra

## 5. Busca Global — Unificada

```kotlin
suspend fun globalSearch(query: String): List<SearchResultUi> {
    val lives = async { api.getLiveStreams(user, pass) }
    val vods = async { api.getVodStreams(user, pass) }
    val series = async { api.getSeries(user, pass) }
    return (lives.await() + vods.await() + series.await())
        .filter { it.name.contains(query, true) }
        .map { it.toSearchResultUi() }
}
```

Debounce 300ms, grid 4 colunas, navegação D-Pad completa.

## 6. Backend intacto — checklist

- [ ] `player_api.php` todas as actions preservadas
- [ ] `get.php` (M3U) intacto
- [ ] `xmltv.php` EPG intacto
- [ ] `libijkplayer.so` + `librtmp` + `libjbcrypto` + OpenVPN mantidos em `jniLibs/`
- [ ] `media.exolist.json` preservado para testes ExoPlayer
- [ ] Nenhum parser alterado — apenas UI consome os mesmos models

## 7. Como transplantar seu código Smarters existente

Se você tem o projeto fonte do Smarters:

1. Copie `app/src/main/java/com/seuprojeto/smarters/data/` para `com.moderntv.app.data`
2. Mantenha `jniLibs/` e `assets/` intactos (já mapeados no manifest)
3. Substitua apenas `ui/` por este projeto (login/dashboard/live/vod)
4. Atualize `MainActivity` para usar seus ViewModels existentes
5. `./gradlew assembleDebug` — deve compilar sem erros de backend

## 8. Testes D-Pad

```bash
adb shell input keyevent 19 # DPAD_UP
adb shell input keyevent 20 # DPAD_DOWN
adb shell input keyevent 21 # DPAD_LEFT
adb shell input keyevent 22 # DPAD_RIGHT
adb shell input keyevent 23 # DPAD_CENTER/ENTER
adb shell input keyevent 4  # BACK
adb shell input keyevent 82 # MENU
```

Rodar em: Emulador Android TV (API 34, 1080p), Chromecast com Google TV 4K, TCL/Philips TV.

