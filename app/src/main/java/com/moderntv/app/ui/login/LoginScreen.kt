package com.moderntv.app.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moderntv.app.ui.theme.ModernTvColors
import kotlinx.coroutines.launch

/**
 * Tela de Login — Xtream Codes API com exatamente 3 campos.
 * REMOVIDO: qualquer QR / activation code.
 * Foco D-Pad com glow vermelho no botão principal.
 */
@Composable
fun LoginScreen(
    onAuthenticated: (host: String, user: String, pass: String) -> Unit
) {
    var host by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var hostFocused by remember { mutableStateOf(false) }
    var userFocused by remember { mutableStateOf(false) }
    var passFocused by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val focusHost = remember { FocusRequester() }
    val focusUser = remember { FocusRequester() }
    val focusPass = remember { FocusRequester() }
    val focusBtn = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusHost.requestFocus() }

    val isValid = host.isNotBlank() && username.isNotBlank() && password.isNotBlank()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0A0A0B), Color(0xFF141414), Color(0xFF0A0A0B))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Fundo com glow sutil
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(220.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.radialGradient(listOf(Color(0x33141212), Color.Transparent)))
                .blur(60.dp)
        )

        Column(
            modifier = Modifier
                .width(520.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF1A1A1A).copy(alpha = 0.92f))
                .border(1.dp, ModernTvColors.GlassBorder, RoundedCornerShape(20.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Entrar", style = MaterialTheme.typography.headlineMedium, color = Color.White)
            Spacer(Modifier.height(6.dp))
            Text("Xtream Codes API", style = MaterialTheme.typography.bodyMedium, color = ModernTvColors.OnSurfaceVariant)
            Spacer(Modifier.height(24.dp))

            XtreamField(
                label = "URL do Servidor (Host)",
                placeholder = "http://seuhost.com:8080",
                value = host,
                onValueChange = { host = it },
                focused = hostFocused,
                onFocusChange = { hostFocused = it },
                modifier = Modifier.focusRequester(focusHost),
                keyboardType = KeyboardType.Uri
            )
            Spacer(Modifier.height(14.dp))
            XtreamField(
                label = "Nome de Usuário",
                placeholder = "Username",
                value = username,
                onValueChange = { username = it },
                focused = userFocused,
                onFocusChange = { userFocused = it },
                modifier = Modifier.focusRequester(focusUser)
            )
            Spacer(Modifier.height(14.dp))
            XtreamField(
                label = "Senha",
                placeholder = "Password",
                value = password,
                onValueChange = { password = it },
                focused = passFocused,
                onFocusChange = { passFocused = it },
                modifier = Modifier.focusRequester(focusPass),
                isPassword = true,
                showPassword = showPass,
                onTogglePassword = { showPass = !showPass }
            )

            if (error != null) {
                Spacer(Modifier.height(12.dp))
                Text(error!!, color = Color(0xFFFF3B30), fontSize = 12.sp)
            }

            Spacer(Modifier.height(22.dp))

            // Botão CONECTAR com glow ao focar (D-Pad)
            var btnFocused by remember { mutableStateOf(false) }
            Button(
                onClick = {
                    if (!isValid) { error = "Preencha os 3 campos obrigatórios."; return@Button }
                    loading = true
                    error = null
                    scope.launch {
                        try {
                            // validação via XtreamApiFactory.create(host).authenticate(username, password)
                            // preservado — aqui simulado; em produção descomente:
                            // val api = XtreamApiFactory.create(host)
                            // val auth = api.authenticate(username, password)
                            // if (auth.user_info.auth != 1) throw Exception("Usuário/senha inválidos")
                            kotlinx.coroutines.delay(600) // simula chamada
                            onAuthenticated(host.trim(), username.trim(), password.trim())
                        } catch (e: Exception) {
                            error = e.message ?: "Falha ao conectar. Verifique host/credenciais."
                        } finally { loading = false }
                    }
                },
                enabled = !loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .focusRequester(focusBtn)
                    .onFocusChanged { btnFocused = it.isFocused }
                    .then(
                        if (btnFocused) Modifier.border(2.dp, ModernTvColors.Primary, RoundedCornerShape(12.dp))
                        else Modifier
                    ),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ModernTvColors.Primary,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF3A3A3A)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = if (btnFocused) 12.dp else 0.dp
                )
            ) {
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                    Spacer(Modifier.width(10.dp))
                }
                Text(if (loading) "Conectando..." else "CONECTAR", fontSize = 14.sp, letterSpacing = 1.2.sp)
            }

            Spacer(Modifier.height(12.dp))
            Text(
                "Suas credenciais são armazenadas com segurança neste aparelho.",
                style = MaterialTheme.typography.bodyMedium,
                color = ModernTvColors.OnSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun XtreamField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    focused: Boolean,
    onFocusChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onTogglePassword: (() -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = if (focused) Color.White else ModernTvColors.OnSurfaceVariant, fontSize = 12.sp)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color(0xFF6E6E6E), fontSize = 13.sp) },
            singleLine = true,
            visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            trailingIcon = if (isPassword) {
                { TextButton(onClick = { onTogglePassword?.invoke() }) { Text(if (showPassword) "Ocultar" else "Mostrar", color = ModernTvColors.Primary, fontSize = 11.sp) } }
            } else null,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { onFocusChange(it.isFocused) }
                .border(
                    width = if (focused) 1.5.dp else 1.dp,
                    color = if (focused) Color.White else ModernTvColors.GlassBorder,
                    shape = RoundedCornerShape(10.dp)
                ),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF232323),
                unfocusedContainerColor = Color(0xFF1E1E1E),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = ModernTvColors.Primary,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )
    }
}
