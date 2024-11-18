package com.st.migliettadurante.feature_detail

import IoTAPIs.model.InsertPrediction
import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import android.util.Log
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.har.migliettadurante.R
import com.st.migliettadurante.authentication.SecureStorageManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@SuppressLint("MissingPermission")
@Composable
fun SensorTileAndSmartphone(
    navController: NavHostController,
    featureViewModel: FeatureDetailViewModel,
    recognitionViewModel: RecognitionViewModel,
    deviceId: String,
    featureName: String
) {
    val activityRecognitionIndex = 0
    val accelerometerIndex = 1
    val backHandlingEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        featureViewModel.startCalibration(deviceId, featureName)
        featureViewModel.startCalibration(deviceId, "Accelerometer")
    }

    BackHandler(enabled = backHandlingEnabled) {
        featureViewModel.disconnectFeature(
            deviceId = deviceId,
            featureName = featureName,
            index = activityRecognitionIndex
        )
        featureViewModel.disconnectFeature(
            deviceId = deviceId,
            featureName = "Accelerometer",
            index = accelerometerIndex
        )

        navController.popBackStack()
    }

    val activityRecognitionFeature =
        featureViewModel.featureUpdates.value.get(activityRecognitionIndex)
    val accelerometerFeature = featureViewModel.featureUpdates.value.get(accelerometerIndex)

    val currentActivity = remember { mutableStateOf("") }

    LaunchedEffect(activityRecognitionFeature) {
        val dataString = activityRecognitionFeature?.data?.toString() ?: ""
        currentActivity.value = extractPrevisionFromLoggable(dataString)

        if (currentActivity.value == "Jogging") {
            currentActivity.value = "Running"
        }
    }

    val recognitionInsertResponse =
        recognitionViewModel.insertRecognitionResponse.observeAsState()
    LaunchedEffect(recognitionInsertResponse.value) {
        recognitionInsertResponse.value?.let {
            Log.d("Smartphone Recognition", "Attività memorizzata, Response: ${it.message}")
        }
    }

    val errorRecognitionInsertResponse = recognitionViewModel.error.observeAsState()
    LaunchedEffect(errorRecognitionInsertResponse.value) {
        errorRecognitionInsertResponse.value?.let {
            Log.e("Smartphone Recognition", "Errore nell'invio dell'attività $it")
        }
    }

    val context = LocalContext.current
    val jwtToken = SecureStorageManager(context).getJwt()

    LaunchedEffect(accelerometerFeature) {
        val dataString = accelerometerFeature?.data.toString()

        recognitionViewModel.executeAccelerometerEffect(
            context = context,
            dataString = dataString,
            jwtToken = jwtToken!!,
            deviceId = deviceId
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {

        Text(
            text = "HAR su SensorTile.box PRO e Smartphone",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp
            ),
            color = Color(0xFF374151),
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        Text(
            text = "Attività svolta",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            ),
            color = Color(0xFF374151),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "SensorTile.box PRO:",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            color = Color(0xFF374151),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        RecognitionCard(
            activityName = if (currentActivity.value != "") currentActivity.value else "Attività non rilevata",
            imageRes = when (currentActivity.value) {
                "Running" -> R.drawable.run_image
                "Walking" -> R.drawable.walk_image
                "Stationary" -> R.drawable.stop_image
                "Driving" -> R.drawable.drive_image
                else -> R.drawable.no_activity
            },
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Random Forest su smartphone:",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            color = Color(0xFF374151),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        RecognitionCard(
            activityName = (if (RecognitionData.activity.value != "") RecognitionData.activity.value else "Attività non rilevata").toString(),
            imageRes = when (RecognitionData.activity.value) {
                "Running" -> R.drawable.run_image
                "Walking" -> R.drawable.walk_image
                "Stationary" -> R.drawable.stop_image
                "Driving" -> R.drawable.drive_image
                else -> R.drawable.no_activity
            },
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                // Salvo l'ultimo riconoscimento
                if (recognitionViewModel.startTime.longValue != 0L && RecognitionData.activity.value != "") {

                    val endTime = System.currentTimeMillis()
                    val duration = (endTime - recognitionViewModel.startTime.longValue) / 1000
                    val date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    val recognition = InsertPrediction().apply {
                        this.prediction = RecognitionData.activity.value
                        this.deviceId = deviceId
                        this.date = date
                        this.duration = duration.toInt()
                    }
                    recognitionViewModel.saveRecognition(jwtToken!!, recognition)

                    RecognitionData.xValues.clear()
                    RecognitionData.yValues.clear()
                    RecognitionData.zValues.clear()
                    RecognitionData.updateActivity("")
                }
                navController.popBackStack()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2F3F4)),
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Icona Indietro",
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Indietro",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

    LaunchedEffect(true) {
        featureViewModel.observeFeature(
            deviceId = deviceId,
            featureName = featureName,
            index = activityRecognitionIndex
        )
        featureViewModel.sendExtendedCommand(featureName = featureName, deviceId = deviceId)
        featureViewModel.observeFeature(
            deviceId = deviceId,
            featureName = "Accelerometer",
            index = accelerometerIndex
        )
        featureViewModel.sendExtendedCommand(featureName = "Accelerometer", deviceId = deviceId)
    }

}

@Composable
fun RecognitionCard(
    activityName: String,
    imageRes: Int,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .padding(8.dp)
            .height(100.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = activityName,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = activityName.uppercase(),
                fontSize = 18.sp,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

fun extractPrevisionFromLoggable(dataString: String): String {
    // Cerchiamo il pattern "Activity = XYZ"
    val regex = Regex("Activity\\s*=\\s*(\\w+)")
    val matchResult = regex.find(dataString)

    // Ritorna l'attività trovata, altrimenti una stringa vuota
    return matchResult?.groupValues?.get(1) ?: ""
}

@Preview
@Composable
private fun HARSmartphonePreview() {
    SensorTileAndSmartphone(
        navController = rememberNavController(),
        featureViewModel = hiltViewModel(),
        recognitionViewModel = hiltViewModel(),
        deviceId = "deviceID",
        featureName = "featureName"
    )
}