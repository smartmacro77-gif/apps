# Changelog Refatoração — Smarters + Netflix TV

## Login (Xtream Codes)
- REMOVIDO: QrActivity, ActivationActivity, fragments de código numérico, rotas /qr e /activation
- ADICIONADO: LoginScreen com 3 campos obrigatórios (Host, Username, Password) + validação + glow
- PRESERVADO: XtreamApiService.authenticate() e EncryptedSharedPreferences

## Navegação
- REMOVIDO: Drawer antigo + bottom nav
- ADICIONADO: DashboardScreen com drawer retrátil hambúrguer 80/320dp + D-Pad MENU/BACK
- ADICIONADO: FocusRequester + nextFocus* em todos os composables

## TV ao Vivo
- PRESERVADO: get_live_categories, get_live_streams, get_simple_data_table (EPG), libijkplayer, librtmp
- ALTERADO: Layout 3 colunas com skin Netflix (fundo #141414, glassmorphism, bordas iluminadas, glow)

## VOD (Filmes/Séries)
- PRESERVADO: get_vod_categories, get_vod_streams, get_vod_info, get_series_categories, get_series, get_series_info, metadados completos
- ALTERADO: Exibição em carrosséis horizontais TvLazyRow + Hero Billboard + posters 160x240 scale 1.08 ao focar

## Busca
- NOVO: GlobalSearchScreen com debounce 300ms varrendo live+vod+series simultaneamente

## Player
- PRESERVADO: ExoPlayer + IJK fallback (libijkplayer.so), RTMP, OpenVPN assets
- ALTERADO: PlayerScreen com controles D-Pad custom (play/pause, seek, auto-hide)

## Estabilidade
- Apenas UI/UX alterada — parsers e players intactos
- Testado D-Pad em todas as telas
