package com.st.migliettadurante.feature_detail

import IoTAPIs.model.InsertPrediction
import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import com.har.migliettadurante.R
import com.st.migliettadurante.authentication.SecureStorageManager
import com.st.migliettadurante.foreground_service.AccelerometerService
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@SuppressLint("MissingPermission")
@Composable
fun SmartphoneActivityRecognition(
    navController: NavHostController,
    featureDetailViewModel: FeatureDetailViewModel,
    recognitionViewModel: RecognitionViewModel,
    deviceId: String,
    featureName: String
) {

    val featureIndex = 0
    val backHandlingEnabled by remember { mutableStateOf(true) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        featureDetailViewModel.startCalibration(deviceId, featureName)

        // Avvia il servizio di riconoscimento in background
        val intent = Intent(context, AccelerometerService::class.java)
        intent.putExtra("deviceId", deviceId)
        context.startService(intent)
    }

    BackHandler(enabled = backHandlingEnabled) {
        featureDetailViewModel.disconnectFeature(
            deviceId = deviceId,
            featureName = featureName,
            index = featureIndex
        )
        navController.popBackStack()
    }

    val feature = featureDetailViewModel.featureUpdates.value.get(featureIndex)

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

    val jwtToken = SecureStorageManager(context).getJwt()
    LaunchedEffect(feature) {
        val dataString = feature?.data.toString()

        recognitionViewModel.executeAccelerometerEffect(
            context = context,
            dataString = dataString,
            jwtToken = jwtToken!!,
            deviceId = deviceId,
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {

        Text(
            text = "HAR su Smartphone",
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            SmartphoneActivityCard(
                activityName = "Running",
                imageRes = R.drawable.run_image,
                isSelected = RecognitionData.activity.value == "Running",
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            SmartphoneActivityCard(
                activityName = "Walking",
                imageRes = R.drawable.walk_image,
                isSelected = RecognitionData.activity.value == "Walking",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            SmartphoneActivityCard(
                activityName = "Stationary",
                imageRes = R.drawable.stop_image,
                isSelected = RecognitionData.activity.value == "Stationary",
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            SmartphoneActivityCard(
                activityName = "Driving",
                imageRes = R.drawable.drive_image,
                isSelected = RecognitionData.activity.value == "Driving",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (RecognitionData.activity.value == "") {
            Text(
                text = "In attesa di dati...",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }

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
        featureDetailViewModel.observeFeature(
            deviceId = deviceId,
            featureName = featureName,
            index = featureIndex
        )
        featureDetailViewModel.sendExtendedCommand(featureName = featureName, deviceId = deviceId)
    }
}

@Composable
fun SmartphoneActivityCard(
    activityName: String,
    imageRes: Int,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) Color(0xFF42A5F5) else Color.Transparent

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .padding(8.dp)
            .border(4.dp, borderColor, RoundedCornerShape(16.dp))
            .height(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Unspecified
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = activityName,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = activityName,
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
}