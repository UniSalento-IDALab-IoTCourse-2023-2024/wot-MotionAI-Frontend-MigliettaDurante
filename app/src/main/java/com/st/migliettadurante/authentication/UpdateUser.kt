package com.st.migliettadurante.authentication

import IoTAPIs.model.EditUser
import IoTAPIs.model.LoginUser
import IoTAPIs.model.UserResponse
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
fun UpdateUser(
    updateUserViewModel: UpdateUserViewModel,
    loginViewModel: LoginViewModel,
    navController: NavController?,
) {

    val secureStorageManager = SecureStorageManager(LocalContext.current)
    val oldUser = secureStorageManager.getUser()

    var errorMessage = remember { mutableStateOf("") }
    val errorColor = Color(0xFFD32F2F)
    var fieldsIndicatorColor = remember { mutableStateOf(Color.Transparent) }
    var oldPasswordIndicatorColor = remember { mutableStateOf(Color.Transparent) }
    var passwordIndicatorColor = remember { mutableStateOf(Color.Transparent) }
    var oldPasswordVisibility = remember { mutableStateOf(false) }
    var passwordVisibility = remember { mutableStateOf(false) }
    var confirmPasswordVisibility = remember { mutableStateOf(false) }
    var isUpdating = remember { mutableStateOf(false) }

    val errorUpdateUserResponse = updateUserViewModel.errorEditUser.observeAsState()
    errorUpdateUserResponse.value?.let {

        errorMessage.value = "Errore generico, rieffettua il login"
        fieldsIndicatorColor.value = errorColor
        oldPasswordIndicatorColor.value = errorColor
        passwordIndicatorColor.value = errorColor
        isUpdating.value = false
    }

    val errorLoginResponse = loginViewModel.error.observeAsState()
    errorLoginResponse.value?.let {

        errorMessage.value = "Vecchia password errata"
        oldPasswordIndicatorColor.value = errorColor
        isUpdating.value = false
    }

    val errorDeleteUserResponse = updateUserViewModel.errorDeleteUser.observeAsState()
    errorDeleteUserResponse.value?.let {
        Log.e("UpdateUser", "Delete User error: $it")
        errorMessage.value = "Errore generico, riprova più tardi"
        isUpdating.value = false
    }

    val deleteUserResponse = updateUserViewModel.deleteUserResponse.observeAsState()
    deleteUserResponse.value?.let {

        updateUserViewModel.deleteRecognitions(secureStorageManager.getJwt()!!)
        updateUserViewModel.deleteActivities(secureStorageManager.getJwt()!!)

        secureStorageManager.clearJwt()
        secureStorageManager.clearUser()
        navController?.navigate("welcome")
    }

    var nameState = remember { mutableStateOf(oldUser.nome) }
    var surnameState = remember { mutableStateOf(oldUser.cognome) }
    var birthDateState = remember { mutableStateOf(LocalDate.parse(oldUser.dataNascita)) }

    var oldPassword = remember { mutableStateOf("") }
    var password = remember { mutableStateOf("") }
    var confirmPassword = remember { mutableStateOf("") }

    val showDialog = remember { mutableStateOf(false) }

    val loginResponse = loginViewModel.loginResponse.observeAsState()
    loginResponse.value?.let {

        secureStorageManager.clearJwt()
        secureStorageManager.saveJwt(it.jwt)

        val body = EditUser()
        body.nome = nameState.value
        body.cognome = surnameState.value
        body.dataNascita = birthDateState.value.toString()

        if (password.value.isNotEmpty())
            body.password = password.value
        else
            body.password = oldPassword.value

        updateUserViewModel.updateUser(body, secureStorageManager.getJwt()!!)
    }

    val updateUserResponse = updateUserViewModel.editUserResponse.observeAsState()
    updateUserResponse.value?.let {

        secureStorageManager.clearUser()

        val updatedUser = UserResponse()
        updatedUser.id = oldUser.id
        updatedUser.nome = nameState.value
        updatedUser.cognome = surnameState.value
        updatedUser.email = oldUser.email
        updatedUser.dataNascita = birthDateState.value.toString()

        secureStorageManager.saveUser(updatedUser)

        navController?.popBackStack()
    }

    val selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return utcTimeMillis <= System.currentTimeMillis()
        }
    }

    val initialDateMillis =
        birthDateState.value.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
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
            text = "Aggiorna i tuoi dati",
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
                    value = nameState.value,
                    onValueChange = { nameState.value = it },
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
                    value = surnameState.value,
                    onValueChange = { surnameState.value = it },
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
                            text = birthDateState.value.let { dateFormat.format(it) }
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
                    birthDateState.value = selectedDate
                }

                OutlinedTextField(
                    value = oldPassword.value,
                    onValueChange = { oldPassword.value = it },
                    visualTransformation = if (!oldPasswordVisibility.value) PasswordVisualTransformation() else VisualTransformation.None,
                    trailingIcon = {
                        val icon = if (oldPasswordVisibility.value) {
                            Icons.Default.VisibilityOff
                        } else {
                            Icons.Default.Visibility
                        }
                        IconButton(onClick = { oldPasswordVisibility.value = !oldPasswordVisibility.value }) {
                            Icon(icon, contentDescription = "Visibility")
                        }
                    },
                    singleLine = true,
                    label = { Text("Vecchia password") },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        cursorColor = Color(0xFF3A7BD5),
                        unfocusedContainerColor = Color(0xFFE3F2FD),
                        focusedContainerColor = Color.White,
                        unfocusedLabelColor = Color.Black,
                        unfocusedIndicatorColor = oldPasswordIndicatorColor.value,
                        focusedIndicatorColor = oldPasswordIndicatorColor.value,
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
                    label = { Text("Nuova password") },
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
                    label = { Text("Conferma nuova password") },
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
            }

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

            Button(
                onClick = {
                    isUpdating.value = true
                    if (nameState.value.isEmpty() || surnameState.value.isEmpty()) {
                        errorMessage.value = "Compila tutti i campi"
                        fieldsIndicatorColor.value = errorColor
                        isUpdating.value = false
                        return@Button
                    }
                    fieldsIndicatorColor.value = Color.Transparent

                    if (oldPassword.value.isEmpty()) {
                        errorMessage.value = "Devi inserire la vecchia password"
                        oldPasswordIndicatorColor.value = errorColor
                        isUpdating.value = false
                        return@Button
                    }
                    oldPasswordIndicatorColor.value = Color.Transparent

                    if (password.value.isNotEmpty() || confirmPassword.value.isNotEmpty()) {

                        if (password.value != confirmPassword.value) {
                            errorMessage.value = "Le password non corrispondono"
                            passwordIndicatorColor.value = errorColor
                            isUpdating.value = false
                            return@Button
                        }

                        if (password.value.length < 8) {
                            errorMessage.value = "La password deve contenere almeno 8 caratteri"
                            passwordIndicatorColor.value = errorColor
                            isUpdating.value = false
                            return@Button
                        }

                        if (password.value.contains(" ")) {
                            errorMessage.value = "La password non può contenere spazi"
                            passwordIndicatorColor.value = errorColor
                            isUpdating.value = false
                            return@Button
                        }

                        if (!password.value.any { it.isDigit() }) {
                            errorMessage.value = "La password deve contenere almeno un numero"
                            passwordIndicatorColor.value = errorColor
                            isUpdating.value = false
                            return@Button
                        }

                        if (!password.value.any { it.isLowerCase() }) {
                            errorMessage.value =
                                "La password deve contenere almeno una lettera minuscola"
                            passwordIndicatorColor.value = errorColor
                            isUpdating.value = false
                            return@Button
                        }

                        if (!password.value.any { it.isUpperCase() }) {
                            errorMessage.value =
                                "La password deve contenere almeno una lettera maiuscola"
                            passwordIndicatorColor.value = errorColor
                            isUpdating.value = false
                            return@Button
                        }
                    }
                    passwordIndicatorColor.value = Color.Transparent

                    val loginBody = LoginUser()
                    loginBody.email = oldUser.email
                    loginBody.password = oldPassword.value

                    loginViewModel.login(loginBody)
                },
                enabled = !isUpdating.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, end = 32.dp, top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3A7BD5),
                ),
            ) {
                Text(if (!isUpdating.value) "Aggiorna dati" else "Caricamento...")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        Column {
            Button(
                onClick = {
                    navController?.popBackStack()
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE3E3E3),
                ),
            ) {
                Text("Annulla", color = Color.Black)
            }

            Button(
                onClick = {
                    showDialog.value = true
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F),
                ),
            ) {
                Text("Elimina account", color = Color.White)
            }
        }

        if(showDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    showDialog.value = false
                },
                title = {
                    Text("Conferma eliminazione account")
                },
                text = {
                    Text("Sei sicuro di voler eliminare il tuo account?")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            updateUserViewModel.deleteUser(secureStorageManager.getJwt()!!)
                            showDialog.value = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD32F2F),
                        ),
                    ) {
                        Text("Conferma")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showDialog.value = false
                        }
                    ) {
                        Text("Annulla")
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun PreviewUpdateUser() {
    UpdateUser(
        updateUserViewModel = UpdateUserViewModel(),
        loginViewModel = LoginViewModel(),
        navController = null
    )
}