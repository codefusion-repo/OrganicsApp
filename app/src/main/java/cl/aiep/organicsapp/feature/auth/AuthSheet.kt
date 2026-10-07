package cl.aiep.organicsapp.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cl.aiep.organicsapp.data.auth.LocalAuthRepository

enum class AuthMode { LOGIN, REGISTER }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthSheet(
    error: String?,
    onDismiss: () -> Unit,
    onLogin: (email: String, password: String, receiveOffers: Boolean) -> Unit,
    onRegister: (name: String, email: String, password: String, receiveOffers: Boolean) -> Unit,
    onContinueAsGuest: () -> Unit
) {
    var mode by remember { mutableStateOf(AuthMode.LOGIN) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(LocalAuthRepository.DEMO_EMAIL) }
    var password by remember { mutableStateOf(LocalAuthRepository.DEMO_PASSWORD) }
    var receiveOffers by remember { mutableStateOf(true) }
    var showPassword by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = if (mode == AuthMode.LOGIN) "Bienvenido de vuelta" else "Crea tu cuenta",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = if (mode == AuthMode.LOGIN) {
                    "Inicia sesión para asociar pedidos y preferencias a tu perfil."
                } else {
                    "Regístrate para guardar tus preferencias y recibir novedades."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (mode == AuthMode.LOGIN) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { mode = AuthMode.LOGIN }
                    ) { Text("Iniciar sesión") }
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            mode = AuthMode.REGISTER
                            email = ""
                            password = ""
                        }
                    ) { Text("Registrarse") }
                } else {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            mode = AuthMode.LOGIN
                            email = LocalAuthRepository.DEMO_EMAIL
                            password = LocalAuthRepository.DEMO_PASSWORD
                        }
                    ) { Text("Iniciar sesión") }
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { mode = AuthMode.REGISTER }
                    ) { Text("Registrarse") }
                }
            }

            if (mode == AuthMode.REGISTER) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Nombre completo") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                )
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Correo electrónico") },
                leadingIcon = { Icon(Icons.Default.Mail, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (showPassword) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = receiveOffers,
                    onCheckedChange = { receiveOffers = it }
                )
                Text(
                    text = "Recibir notificaciones sobre ofertas especiales",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (!error.isNullOrBlank()) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (mode == AuthMode.LOGIN) {
                DemoCredentialsCard()
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (mode == AuthMode.LOGIN) {
                        onLogin(email.trim(), password, receiveOffers)
                    } else {
                        onRegister(name.trim(), email.trim(), password, receiveOffers)
                    }
                }
            ) {
                Text(if (mode == AuthMode.LOGIN) "Ingresar" else "Crear cuenta")
            }

            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onContinueAsGuest
            ) {
                Text("Seguir como invitado")
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun DemoCredentialsCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Usuario de prueba",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Correo: ${LocalAuthRepository.DEMO_EMAIL}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Contraseña: ${LocalAuthRepository.DEMO_PASSWORD}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
