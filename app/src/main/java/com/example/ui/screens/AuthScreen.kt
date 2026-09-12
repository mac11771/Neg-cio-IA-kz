package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandPrimaryBlue
import com.example.ui.theme.BrandPurpleAi
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(viewModel: AppViewModel) {
    var isRegisterMode by remember { mutableStateOf(false) }

    // Common fields
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Register fields
    var fullName by remember { mutableStateOf("") }
    var businessName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var province by remember { mutableStateOf("Luanda") }
    var municipality by remember { mutableStateOf("Maianga") }
    var businessType by remember { mutableStateOf("Loja / Varejo") }

    var showForgotDialog by remember { mutableStateOf(false) }

    val angolanProvinces = listOf(
        "Luanda", "Benguela", "Huambo", "Huíla", "Cabinda",
        "Cuanza Sul", "Cuanza Norte", "Uíge", "Malanje", "Namibe", "Zaire"
    )

    val businessTypes = listOf(
        "Loja / Varejo", "Restaurante / Alimentos", "Salão de Beleza / Barbearia",
        "Designer / Fotógrafo", "Técnico de Informática", "Prestador de Serviços",
        "Vendedor Online / WhatsApp", "Construção & Reformas", "Outro"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (isRegisterMode) "Criar Nova Conta" else "Aceder à Plataforma",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isRegisterMode)
                    "Registe o seu negócio e ganhe 50 créditos grátis."
                else
                    "Introduza o seu e-mail e palavra-passe para continuar.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (isRegisterMode) {
            item {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nome Completo") },
                    leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("auth_fullname_input")
                )

                OutlinedTextField(
                    value = businessName,
                    onValueChange = { businessName = it },
                    label = { Text("Nome do Seu Negócio") },
                    leadingIcon = { Icon(Icons.Outlined.Store, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("auth_business_name_input")
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Telefone / WhatsApp (ex: 923 XXX XXX)") },
                    leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("auth_phone_input")
                )

                // Province Selector
                var provinceExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = provinceExpanded,
                    onExpandedChange = { provinceExpanded = !provinceExpanded },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    OutlinedTextField(
                        value = province,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Província") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = provinceExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = provinceExpanded,
                        onDismissRequest = { provinceExpanded = false }
                    ) {
                        angolanProvinces.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p) },
                                onClick = {
                                    province = p
                                    provinceExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = municipality,
                    onValueChange = { municipality = it },
                    label = { Text("Município / Bairro") },
                    leadingIcon = { Icon(Icons.Outlined.LocationOn, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                // Business Type Selector
                var typeExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    OutlinedTextField(
                        value = businessType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de Negócio") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        businessTypes.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t) },
                                onClick = {
                                    businessType = t
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-mail") },
                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("auth_email_input")
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Palavra-passe") },
                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("auth_password_input")
            )

            if (!isRegisterMode) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { showForgotDialog = true }) {
                        Text("Esqueceu a palavra-passe?", style = MaterialTheme.typography.bodySmall, color = BrandPrimaryBlue)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (isRegisterMode) {
                        if (email.isNotBlank() && password.isNotBlank() && fullName.isNotBlank()) {
                            viewModel.register(
                                fullName, businessName, phone, email, password, province, municipality, businessType
                            )
                        } else {
                            viewModel.showSnackbar("Por favor preencha todos os campos obrigatórios.")
                        }
                    } else {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            viewModel.login(email, password)
                        } else {
                            viewModel.showSnackbar("Introduza o seu e-mail e palavra-passe.")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("auth_submit_button")
            ) {
                Text(
                    text = if (isRegisterMode) "Criar Conta & Ganhar Créditos" else "Entrar na Minha Conta",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = { isRegisterMode = !isRegisterMode },
                modifier = Modifier.testTag("auth_switch_mode_button")
            ) {
                Text(
                    text = if (isRegisterMode)
                        "Já possui uma conta? Iniciar Sessão"
                    else
                        "Não tem uma conta? Registe o seu negócio aqui",
                    color = BrandPrimaryBlue,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Demo Fast Access
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ACESSO RÁPIDO DE DEMONSTRAÇÃO",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.login("manuel@kilamba.ao", "123456")
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Store, contentDescription = null, tint = BrandPrimaryBlue)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Empreendedor", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = {
                        viewModel.login("admin@negocioia.ao", "admin123")
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = BrandPurpleAi)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Administrador", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    if (showForgotDialog) {
        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            title = { Text("Recuperação de Palavra-passe") },
            text = {
                Text("Foi enviado um link de redefinição para o seu e-mail cadastrado ou WhatsApp. Siga as instruções recebidas.")
            },
            confirmButton = {
                TextButton(onClick = { showForgotDialog = false }) {
                    Text("Entendido")
                }
            }
        )
    }
}
