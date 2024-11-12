package com.st.migliettadurante.authentication

import IoTAPIs.model.User
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Registration(
    viewModel: RegistrationViewModel,
    navController: NavController?,
) {
    var name = remember { mutableStateOf("") }
    var surname = remember { mutableStateOf("") }
    var birthDate = remember { mutableStateOf<LocalDate>(LocalDate.now()) }
    var email = remember { mutableStateOf("") }
    var password = remember { mutableStateOf("") }
    var confirmPassword = remember { mutableStateOf("") }

    var errorMessage = remember { mutableStateOf("") }
    val errorColor = Color(0xFFD32F2F)
    var fieldsIndicatorColor = remember { mutableStateOf(Color.Transparent) }
    var emailIndicatorColor = remember { mutableStateOf(Color.Transparent) }
    var passwordIndicatorColor = remember { mutableStateOf(Color.Transparent) }
    var passwordVisibility = remember { mutableStateOf(false) }
    var confirmPasswordVisibility = remember { mutableStateOf(false) }
    var isRegistering = remember { mutableStateOf(false) }

    val userResponse = viewModel.registrationResponse.observeAsState()
    val errorRegistration = viewModel.error.observeAsState()

    userResponse.value?.let {
        errorMessage.value = ""
        fieldsIndicatorColor.value = Color.Transparent
        emailIndicatorColor.value = Color.Transparent
        passwordIndicatorColor.value = Color.Transparent
        navController?.navigate("login")
    }
    errorRegistration.value?.let {
        if (it.contains("Status Code: 400")) {
            errorMessage.value = "Email già registrata"
            emailIndicatorColor.value = errorColor
        } else {
            errorMessage.value = "Errore durante la registrazione"
            fieldsIndicatorColor.value = errorColor
            emailIndicatorColor.value = errorColor
            passwordIndicatorColor.value = errorColor
        }
        isRegistering.value = false
    }

    val selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return utcTimeMillis <= System.currentTimeMillis()
        }
    }

    val initialDateMillis = birthDate.value.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    val dataPickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis,
        selectableDates = selectableDates,
    )
    val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(horizontal = 16.dp)
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState()),
    ) {
        Text(
            text = "Registrati",
            modifier = Modifier
                .padding(vertical = 32.dp)
                .align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold, fontSize = 26.sp
            ),
        )

        Card(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 32.dp)
                .shadow(16.dp, shape = RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE3F2FD),
            ),
        ) {

            Column(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                OutlinedTextField(
                    value = name.value,
                    onValueChange = { name.value = it },
                    singleLine = true,
                    label = { Text("Nome") },
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        cursorColor = Color(0xFF3A7BD5),
                        unfocusedContainerColor = Color(0xFFE3F2FD),
                        focusedContainerColor = Color.White,
                        unfocusedLabelColor = Color.Black,
                        unfocusedIndicatorColor = fieldsIndicatorColor.value,
                        focusedIndicatorColor = fieldsIndicatorColor.value,
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 32.dp),
                )

                OutlinedTextField(
                    value = surname.value,
                    onValueChange = { surname.value = it },
                    singleLine = true,
                    label = { Text("Cognome") },
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        cursorColor = Color(0xFF3A7BD5),
                        unfocusedContainerColor = Color(0xFFE3F2FD),
                        focusedContainerColor = Color.White,
                        unfocusedLabelColor = Color.Black,
                        unfocusedIndicatorColor = fieldsIndicatorColor.value,
                        focusedIndicatorColor = fieldsIndicatorColor.value,
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 32.dp),
                )

                DatePicker(
                    title = {
                        Text(
                            text = "Data di nascita",
                            modifier = Modifier
                                .padding(start = 32.dp, end = 32.dp, top = 32.dp)
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 16.sp
                            ),
                        )
                    },
                    headline = {
                        Text(
                            text = birthDate.value.let { dateFormat.format(it) }
                                ?: "Seleziona una data",
                            modifier = Modifier
                                .padding(horizontal = 46.dp, vertical = 16.dp)
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp
                            )
                        )
                    },
                    showModeToggle = true,
                    state = dataPickerState,
                    colors = DatePickerDefaults.colors(
                        selectedDayContainerColor = Color(0xFF3A7BD5),
                        titleContentColor = Color.Black,
                        dateTextFieldColors = TextFieldDefaults.colors(
                            cursorColor = Color(0xFF3A7BD5),
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedLabelColor = Color.Black,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                        ),
                    )
                )

                dataPickerState.selectedDateMillis?.let {
                    val selectedDate =
                        LocalDateTime.ofEpochSecond(it / 1000, 0, ZoneOffset.UTC).toLocalDate()
                    birthDate.value = selectedDate
                }

                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Email,
                    ),
                    singleLine = true,
                    label = { Text("Email") },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        cursorColor = Color(0xFF3A7BD5),
                        unfocusedContainerColor = Color(0xFFE3F2FD),
                        focusedContainerColor = Color.White,
                        unfocusedLabelColor = Color.Black,
                        unfocusedIndicatorColor = emailIndicatorColor.value,
                        focusedIndicatorColor = emailIndicatorColor.value,
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 32.dp),
                )

                OutlinedTextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    visualTransformation = if (!passwordVisibility.value) PasswordVisualTransformation() else VisualTransformation.None,
                    trailingIcon = {
                        val icon = if (passwordVisibility.value) {
                            Icons.Default.VisibilityOff
                        } else {
                            Icons.Default.Visibility
                        }
                        IconButton(onClick = { passwordVisibility.value = !passwordVisibility.value }) {
                            Icon(icon, contentDescription = "Visibility")
                        }
                    },
                    singleLine = true,
                    label = { Text("Password") },
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        cursorColor = Color(0xFF3A7BD5),
                        unfocusedContainerColor = Color(0xFFE3F2FD),
                        focusedContainerColor = Color.White,
                        unfocusedLabelColor = Color.Black,
                        unfocusedIndicatorColor = passwordIndicatorColor.value,
                        focusedIndicatorColor = passwordIndicatorColor.value,
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 32.dp),
                )

                OutlinedTextField(
                    value = confirmPassword.value,
                    onValueChange = { confirmPassword.value = it },
                    visualTransformation = if (!confirmPasswordVisibility.value) PasswordVisualTransformation() else VisualTransformation.None,
                    trailingIcon = {
                        val icon = if (confirmPasswordVisibility.value) {
                            Icons.Default.VisibilityOff
                        } else {
                            Icons.Default.Visibility
                        }
                        IconButton(onClick = { confirmPasswordVisibility.value = !confirmPasswordVisibility.value }) {
                            Icon(icon, contentDescription = "Visibility")
                        }
                    },
                    singleLine = true,
                    label = { Text("Conferma password") },
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        cursorColor = Color(0xFF3A7BD5),
                        unfocusedContainerColor = Color(0xFFE3F2FD),
                        focusedContainerColor = Color.White,
                        unfocusedLabelColor = Color.Black,
                        unfocusedIndicatorColor = passwordIndicatorColor.value,
                        focusedIndicatorColor = passwordIndicatorColor.value,
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 32.dp),
                )

                if (errorMessage.value.isNotEmpty()) {
                    Text(
                        text = errorMessage.value,
                        modifier = Modifier
                            .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                            .align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = errorColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        ),
                    )
                }
            }

            Button(
                onClick = {
                    isRegistering.value = true
                    if (name.value.isEmpty() || surname.value.isEmpty()) {
                        errorMessage.value = "Compila tutti i campi"
                        fieldsIndicatorColor.value = errorColor
                        isRegistering.value = false
                        return@Button
                    }
                    fieldsIndicatorColor.value = Color.Transparent

                    if (!email.value.contains("@") || !email.value.contains(".")) {
                        errorMessage.value = "Inserisci un'email valida"
                        emailIndicatorColor.value = errorColor
                        isRegistering.value = false
                        return@Button
                    }
                    emailIndicatorColor.value = Color.Transparent

                    if (password.value.isEmpty() || confirmPassword.value.isEmpty()) {
                        errorMessage.value = "Compila tutti i campi"
                        passwordIndicatorColor.value = errorColor
                        isRegistering.value = false
                        return@Button
                    }

                    if (password.value != confirmPassword.value) {
                        errorMessage.value = "Le password non corrispondono"
                        passwordIndicatorColor.value = errorColor
                        isRegistering.value = false
                        return@Button
                    }

                    if (password.value.length < 8) {
                        errorMessage.value = "La password deve contenere almeno 8 caratteri"
                        passwordIndicatorColor.value = errorColor
                        isRegistering.value = false
                        return@Button
                    }

                    if (password.value.contains(" ")) {
                        errorMessage.value = "La password non può contenere spazi"
                        passwordIndicatorColor.value = errorColor
                        isRegistering.value = false
                        return@Button
                    }

                    if (!password.value.any { it.isDigit() }) {
                        errorMessage.value = "La password deve contenere almeno un numero"
                        passwordIndicatorColor.value = errorColor
                        isRegistering.value = false
                        return@Button
                    }

                    if (!password.value.any { it.isLowerCase() }) {
                        errorMessage.value =
                            "La password deve contenere almeno una lettera minuscola"
                        passwordIndicatorColor.value = errorColor
                        isRegistering.value = false
                        return@Button
                    }

                    if (!password.value.any { it.isUpperCase() }) {
                        errorMessage.value =
                            "La password deve contenere almeno una lettera maiuscola"
                        passwordIndicatorColor.value = errorColor
                        isRegistering.value = false
                        return@Button
                    }
                    passwordIndicatorColor.value = Color.Transparent
                    errorMessage.value = ""

                    val body = User()

                    body.nome = name.value
                    body.cognome = surname.value
                    body.dataNascita = birthDate.value.toString()
                    body.email = email.value
                    body.password = password.value

                    viewModel.registration(body)

                },
                enabled = !isRegistering.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, end = 32.dp, top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3A7BD5),
                ),
            ) {
                Text(if (!isRegistering.value) "Registrati" else "Caricamento...")
            }

            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .align(Alignment.End),
            ) {
                Text(
                    text = "Hai già un account? ",
                    modifier = Modifier.padding(vertical = 16.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold, fontSize = 14.sp
                    ),
                )

                Text(
                    text = "Accedi",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold, fontSize = 14.sp,
                        color = Color(0xFF3A7BD5),
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier
                        .padding(top = 16.dp, end = 16.dp)
                        .clickable {
                            navController?.navigate("login")
                        },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview
@Composable
fun PreviewRegistration() {
    Registration(
        viewModel = RegistrationViewModel(),
        navController = null,
    )
}