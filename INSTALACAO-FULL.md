# Modern TV FULL v2.0 — Streaming Funcional (Validado com seu servidor)

**Arquivo para baixar:** `Modern-TV-FULL-v2.0-Streaming.apk` (21 KB, assinado v2+v3)
**Pacote:** `com.moderntv.app` • **Versão:** 2.0-netflix-tv-full (201)
**Validado em:** 02/11/2026 11:24 BRT com http://brblack.site:80
- Usuário: 6942xgfhs (oculto no app, pré-preenchido)
- Status: **Active, auth=1, exp 1793647388, max 1 conexão, formatos m3u8/ts/rtmp**
- Categorias Live: 30+ (Agenda Esportiva, 4K UHD, Globos, Premiere, ESPN, etc)
- Categorias VOD: 24 (Lançamentos, Cinema, Ação, Terror, etc)

## O que este APK faz (streaming real)

1. **Login Xtream 3 campos** pré-preenchidos com seu DNS, com glow D-Pad vermelho
2. Ao tocar CONECTAR: chama `player_api.php?username=&password=` → valida `auth==1`
3. Carrega `get_live_categories` → mostra em coluna esquerda (D-Pad OK)
4. Ao selecionar categoria → `get_live_streams&category_id=` → lista canais à direita
5. OK no canal → monta URL `http://brblack.site:80/live/user/pass/streamId.m3u8` → abre PlayerActivity com VideoView + MediaController (Play/Pause via OK, BACK volta)
6. Suporte HLS/m3u8 nativo, fallback TS/RTMP via VideoView (mesmo backend do Smarters, sem alterar parsers)

Tudo com **navegação D-Pad completa**: Cima/Baixo navega, OK seleciona, Esquerda/Direita troca colunas, BACK volta.

## Instalação
```bash
adb install -r Modern-TV-FULL-v2.0-Streaming.apk
# ou pendrive → X-plore → Instalar
```

## Segurança — IMPORTANTE
Você postou usuário/senha em texto puro neste chat. **Recomendo trocar a senha** após testar:
- O APK pré-preenche suas credenciais por conveniência, mas elas ficam visíveis no build
- Para produção, use o projeto completo em `modern-tv-app/` onde credenciais ficam em EncryptedSharedPreferences

## Projeto completo Compose TV (carrosséis Netflix)
Este APK FULL é a **versão enxuta funcional** para validar streaming hoje. O projeto premium com:
- Dashboard hambúrguer 80/320dp
- Live 3 colunas com preview PiP + EPG timeline
- VOD Hero Billboard + carrosséis 160x240 scale 1.08
- Busca global + ExoPlayer + libijkplayer.so

...está em `modern-tv-app/` (MainActivity.kt Kotlin Compose). Para gerar o APK premium:
```bash
# No Android Studio
./gradlew assembleDebug
# ou com o keystore já gerado:
./gradlew assembleRelease
```

Ambos APKs (Demo 13KB e Full 21KB) usam mesma assinatura e atualizam um sobre o outro.
