package com.st.migliettadurante.authentication

import IoTAPIs.model.LoginUser
import android.text.InputType
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(
    viewModel: LoginViewModel,
    navController: NavController?,
) {
    var email = remember { mutableStateOf("") }
    var password = remember { mutableStateOf("") }

    var errorMessage = remember { mutableStateOf("") }
    val errorColor = Color(0xFFD32F2F)
    var fieldsIndicatorColor = remember { mutableStateOf(Color.Transparent) }
    var passwordVisibility = remember { mutableStateOf(false) }
    var isLoggingIn = remember { mutableStateOf(false) }

    val loginResponse = viewModel.loginResponse.observeAsState()
    val errorLogin = viewModel.error.observeAsState()

    val context = LocalContext.current
    loginResponse.value?.let {
        if (it.jwt != null) {
            val secureStorageManager = SecureStorageManager(context)
            secureStorageManager.saveJwt(it.jwt)
            errorMessage.value = ""
            fieldsIndicatorColor.value = Color.Transparent
            navController?.navigate("dashboard")
        } else {
            errorMessage.value = "Errore durante il login"
            fieldsIndicatorColor.value = errorColor
            isLoggingIn.value = false
        }
    }
    errorLogin.value?.let {
        if (it.contains("Status Code: 403")) {
            errorMessage.value = "Email o password errati"
        } else {
            errorMessage.value = "Errore durante il login"
        }
        isLoggingIn.value = false
        fieldsIndicatorColor.value = errorColor
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp)
            .verticalScroll(state = rememberScrollState()),
    ) {
        Text(
            text = "Accedi",
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold, fontSize = 26.sp
            ),
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .shadow(16.dp, shape = RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE3F2FD),
            ),
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Email,
                    ),
                    singleLine = true,
                    label = { Text("Email") },
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
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

                Spacer(modifier = Modifier.height(16.dp))

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
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
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

                if (errorMessage.value.isNotEmpty()) {
                    Text(
                        text = errorMessage.value,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold, fontSize = 16.sp,
                            color = errorColor,
                        ),
                        modifier = Modifier
                            .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                            .align(Alignment.CenterHorizontally),
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    isLoggingIn.value = true
                    if (email.value.isEmpty() || password.value.isEmpty()) {
                        errorMessage.value = "Inserisci email e password"
                        fieldsIndicatorColor.value = errorColor
                        isLoggingIn.value = false
                        return@Button
                    }
                    errorMessage.value = ""
                    fieldsIndicatorColor.value = Color.Transparent

                    val body = LoginUser()
                    body.email = email.value
                    body.password = password.value

                    viewModel.login(body)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3A7BD5),
                ),
                enabled = !isLoggingIn.value,
            ) {
                Text(if (!isLoggingIn.value) "Accedi" else "Caricamento...")
            }

            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .align(Alignment.End),
            ) {
                Text(
                    text = "Non hai un account? ",
                    modifier = Modifier.padding(vertical = 16.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold, fontSize = 14.sp
                    ),
                )

                Text(
                    text = "Registrati",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold, fontSize = 14.sp,
                        color = Color(0xFF3A7BD5),
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier
                        .padding(top = 16.dp, end = 16.dp)
                        .clickable {
                            navController?.navigate("registration")
                        },
                )
            }
//            Text(
//                text = "Password dimenticata?",
//                style = MaterialTheme.typography.bodyMedium.copy(
//                    fontWeight = FontWeight.Bold, fontSize = 14.sp,
//                    color = Color(0xFF3A7BD5),
//                    textDecoration = TextDecoration.Underline,
//                ),
//                modifier = Modifier
//                    .padding(end = 32.dp)
//                    .align(Alignment.End)
//                    .clickable {
//                        navController?.navigate("reset_password")
//                    },
//            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview
@Composable
fun PreviewLogin() {
    Login(
        viewModel = LoginViewModel(),
        navController = null,
    )
}