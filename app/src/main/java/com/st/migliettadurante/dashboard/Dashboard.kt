package com.st.migliettadurante.dashboard

import IoTAPIs.model.UserResponse
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import com.st.migliettadurante.authentication.SecureStorageManager
import com.har.migliettadurante.R.drawable.*
import com.st.blue_sdk.models.NodeState
import com.st.migliettadurante.device_detail.BleDeviceDetailViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun Dashboard(
    dashboardViewModel: DashboardViewModel,
    bleDeviceDetailViewModel: BleDeviceDetailViewModel?,
    navController: NavController?,
) {
    val userResponse = dashboardViewModel.getUserResponse.observeAsState()
    val errorGetUserResponse = dashboardViewModel.errorGetUser.observeAsState()

    val secureStorageManager = SecureStorageManager(LocalContext.current)
    var deviceId = secureStorageManager.getDeviceId()

    var connected = false
    if (deviceId != null) {
        var bleDevice =
            bleDeviceDetailViewModel?.bleDevice(deviceId = deviceId)?.collectAsState(null)
        bleDevice?.value?.connectionStatus?.current.let {
            Log.d("Dashboard", "Connected: $it")
            connected = it == NodeState.Ready
        }
    }

    errorGetUserResponse.value?.let {
        secureStorageManager.clearJwt()
        secureStorageManager.clearUser()
        navController?.navigate("login")
    }

    val jwtToken = remember {
        mutableStateOf(secureStorageManager.getJwt())
    }
    var user = UserResponse()

    val isUserSaved = secureStorageManager.isUserSaved()
    val currentUserEmail = dashboardViewModel.decodeJWT(jwtToken.value!!).getString("sub")
    val savedUserEmail = secureStorageManager.getUser().email

    if (isUserSaved && currentUserEmail == savedUserEmail) {

        user.id = secureStorageManager.getUser().id
        user.nome = secureStorageManager.getUser().nome
        user.cognome = secureStorageManager.getUser().cognome
        user.dataNascita = secureStorageManager.getUser().dataNascita
        user.email = secureStorageManager.getUser().email

    } else {

        dashboardViewModel.getUser(jwtToken.value!!)
        userResponse.value?.let {
            secureStorageManager.saveUser(it)
        }
        user = userResponse.value ?: UserResponse()
    }

    val activitiesResponse = dashboardViewModel.getActivityDurationResponse.observeAsState()

    var walkingStats = "0 sec"
    var stationaryStats = "0 sec"
    var runningStats = "0 sec"
    var drivingStats = "0 sec"

    val weeklyResponse = dashboardViewModel.getWeeklyEstimateResponse.observeAsState()

    var walkingWeekly = "0%"
    var stationaryWeekly = "0%"
    var runningWeekly = "0%"
    var drivingWeekly = "0%"

    LaunchedEffect(Unit) {
        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

        dashboardViewModel.getActivityDuration(date, jwtToken.value!!)
        dashboardViewModel.getWeeklyEstimate(jwtToken.value!!)
    }

    activitiesResponse.value?.let {
        walkingStats = calculateDuration(it.walking)
        stationaryStats = calculateDuration(it.stationary)
        runningStats = calculateDuration(it.running)
        drivingStats = calculateDuration(it.driving)
    }

    weeklyResponse.value?.let {
        walkingWeekly = it.walking
        stationaryWeekly = it.stationary
        runningWeekly = it.running
        drivingWeekly = it.driving
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .verticalScroll(rememberScrollState())
    ) {

        Text(
            text = "Ciao, ${user.nome ?: ""}",
            modifier = Modifier
                .padding(start = 16.dp, top = 32.dp)
                .align(Alignment.Start),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold, fontSize = 26.sp
            ),
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(4.dp, shape = RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE3F2FD)
            )
        ) {
            Column(
                modifier = Modifier.background(color = Color.Transparent)
            ) {

                Text(
                    text = "Attività svolta oggi",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold, fontSize = 20.sp
                    ),
                    modifier = Modifier.padding(16.dp)
                )

                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = walk_image),
                        contentDescription = "Walking Icon",
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = "Walking: $walkingStats",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Questa settimana $walkingWeekly rispetto alla settimana scorsa",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }

                }
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = stop_image),
                        contentDescription = "Stationary Icon",
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = "Stationary: $stationaryStats",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Questa settimana $stationaryWeekly rispetto alla settimana scorsa",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                }
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = run_image),
                        contentDescription = "Running Icon",
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = "Running: $runningStats",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Questa settimana $runningWeekly rispetto alla settimana scorsa",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                }
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = drive_image),
                        contentDescription = "Driving Icon",
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = "Driving: $drivingStats",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Questa settimana $drivingWeekly rispetto alla settimana scorsa",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        ) {

            Text(
                text = "Accedi alle funzionalità",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold, fontSize = 24.sp
                ),
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Button(
                    onClick = {
                        navController?.navigate("archive")
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A7BD5)),
                    modifier = Modifier
                        .padding(8.dp)
                        .height(100.dp)
                        .weight(1f),
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = archive),
                            contentDescription = "Icona Archivio",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Storico attività"
                        )
                    }
                }

                Button(
                    onClick = {
                        navController?.navigate("updateUser")
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A7BD5)),
                    modifier = Modifier
                        .padding(8.dp)
                        .height(100.dp)
                        .weight(1f),
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = ic_user),
                            contentDescription = "Icona user",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Modifica dati"
                        )
                    }

                }
            }

            if (deviceId != null && connected) {
                Button(
                    onClick = {
                        navController?.navigate("detail/$deviceId")
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A7BD5)),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = ic_bluetooth),
                            contentDescription = "Icona Dispositivo",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Dettagli dispositivo"
                        )
                    }
                }
            } else {
                Button(
                    onClick = {
                        navController?.navigate("list")
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A7BD5)),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = ic_search),
                            contentDescription = "Icona Scan",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Cerca dispositivi"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (deviceId != null) {
                        bleDeviceDetailViewModel?.disconnect(deviceId = deviceId)
                        secureStorageManager.clearDeviceId()
                    }
                    secureStorageManager.clearJwt()
                    navController?.navigate("welcome")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A7BD5)),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Image(
                    painter = painterResource(id = off),
                    contentDescription = "Icona logout",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Logout"
                )
            }
        }
    }
}

fun calculateDuration(duration: Int?): String {
    if (duration == null) return "0 sec"

    var result = "$duration sec"
    if (duration >= 60) {
        val minutes = duration / 60
        val seconds = duration % 60
        result = "$minutes min $seconds sec"
        if (minutes > 60) {
            val hours = minutes / 60
            val minutes = minutes % 60
            result = "$hours h $minutes min $seconds sec"
        }
    }
    return result
}

@Preview
@Composable
fun PreviewDashboard() {
    Dashboard(DashboardViewModel(), null, null)
}