package com.st.migliettadurante.feature_detail

import IoTAPIs.model.InsertPrediction
import android.annotation.SuppressLint
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import com.har.migliettadurante.R
import com.st.migliettadurante.authentication.SecureStorageManager
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

    LaunchedEffect(Unit) {
        featureDetailViewModel.startCalibration(deviceId, featureName)
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
    val randomForestActivity = remember { mutableStateOf("") }

    val xValues = remember { mutableStateListOf<Float>() }
    val yValues = remember { mutableStateListOf<Float>() }
    val zValues = remember { mutableStateListOf<Float>() }

    var lastActivity = remember { mutableStateOf("") }

    val context = LocalContext.current

    val recognitionInsertResponse =
        recognitionViewModel.insertRecognitionResponse.observeAsState()
    val errorRecognitionInsertResponse = recognitionViewModel.error.observeAsState()

    errorRecognitionInsertResponse.value?.let {
        Log.e("RecognitionViewModel", it)
        navController.popBackStack()
    }


    val jwtToken = SecureStorageManager(LocalContext.current).getJwt()
    var startTime = remember { mutableStateOf(0L) }

    LaunchedEffect(feature) {

        val dataString = feature?.data.toString()
        val (x, y, z) = recognitionViewModel.extractCoordinatesFromLoggable(dataString)

        xValues.add(x)
        yValues.add(y)
        zValues.add(z)

        val n_rows = 60

        if (xValues.size == n_rows && yValues.size == n_rows && zValues.size == n_rows) {

            val xStats = recognitionViewModel.calculateStatistics(xValues.toFloatArray())
            val yStats = recognitionViewModel.calculateStatistics(yValues.toFloatArray())
            val zStats = recognitionViewModel.calculateStatistics(zValues.toFloatArray())

            val stats = FloatArray(12)

            for (i in 0 until 4) {
                val j = i * 3
                stats[j] = xStats[i]
                stats[j + 1] = yStats[i]
                stats[j + 2] = zStats[i]
            }

            val date =
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

            val recognition = InsertPrediction()
            recognition.prediction =
                recognitionViewModel.getRecognitionFromRandomForest(context, stats)
            recognition.deviceId = deviceId
            recognition.date = date

            randomForestActivity.value = recognition.prediction
            if (lastActivity.value == "") {
                Log.d("Smartphone Recognition", "Timer started")
                startTime.value = System.currentTimeMillis()
            }

            Log.d(
                "Smartphone Recognition",
                "Activity: ${randomForestActivity.value}"
            )
            if (lastActivity.value != randomForestActivity.value) {
                val endTime = System.currentTimeMillis()

                val duration = (endTime - startTime.value) / 1000
                Log.d(
                    "Smartphone Recognition",
                    "Activity: ${lastActivity.value}, Duration: $duration seconds"
                )

                recognition.duration = duration.toInt()

                if (duration > 0)
                    recognitionViewModel.saveRecognition(jwtToken!!, recognition)

                lastActivity.value = randomForestActivity.value

                startTime.value = System.currentTimeMillis()
                Log.d("Smartphone Recognition", "Timer started")

            }

            xValues.clear()
            yValues.clear()
            zValues.clear()
        }
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
                isSelected = randomForestActivity.value == "Running",
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            SmartphoneActivityCard(
                activityName = "Walking",
                imageRes = R.drawable.walk_image,
                isSelected = randomForestActivity.value == "Walking",
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
                isSelected = randomForestActivity.value == "Stationary",
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            SmartphoneActivityCard(
                activityName = "Driving",
                imageRes = R.drawable.drive_image,
                isSelected = randomForestActivity.value == "Driving",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (randomForestActivity.value == "") {
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
            onClick = { navController.popBackStack() },
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