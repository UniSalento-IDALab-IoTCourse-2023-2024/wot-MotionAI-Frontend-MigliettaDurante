package com.st.migliettadurante.feature_detail

import android.annotation.SuppressLint
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
import android.util.Log
import com.har.migliettadurante.R

@SuppressLint("MissingPermission")
@Composable
fun ActivityRecognition(
    navController: NavHostController,
    viewModel: FeatureDetailViewModel,
    deviceId: String,
    featureName: String
) {

    val activityRecognitionIndex = 0
    val backHandlingEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.startCalibration(deviceId, featureName)
    }

    BackHandler(enabled = backHandlingEnabled) {
        viewModel.disconnectFeature(deviceId = deviceId, featureName = featureName, index = activityRecognitionIndex)
        navController.popBackStack()
    }

    val activityRecognitionFeature = viewModel.featureUpdates.value.get(activityRecognitionIndex)
    val currentActivity = remember { mutableStateOf("") }

    LaunchedEffect(activityRecognitionFeature) {
        val dataString = activityRecognitionFeature?.data?.toString() ?: ""
        currentActivity.value = extractActivityFromLoggable(dataString)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {

        Text(
            text = "HAR su SensorTile.box PRO",
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
            ActivityCard(
                activityName = "Running",
                imageRes = R.drawable.run_image,
                isSelected = currentActivity.value == "Jogging",
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            ActivityCard(
                activityName = "Walking",
                imageRes = R.drawable.walk_image,
                isSelected = currentActivity.value == "Walking",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            ActivityCard(
                activityName = "Stationary",
                imageRes = R.drawable.stop_image,
                isSelected = currentActivity.value == "Stationary",
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            ActivityCard(
                activityName = "Driving",
                imageRes = R.drawable.drive_image,
                isSelected = currentActivity.value == "Driving",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

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
            Text("Indietro",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

    LaunchedEffect(true) {
        viewModel.observeFeature(deviceId = deviceId, featureName = featureName, index = activityRecognitionIndex)
        viewModel.sendExtendedCommand(featureName = featureName, deviceId = deviceId)
    }
}

@Composable
fun ActivityCard(
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
        colors = androidx.compose.material3.CardDefaults.cardColors(
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

fun extractActivityFromLoggable(dataString: String): String {
    val regex = Regex("Activity\\s*=\\s*(\\w+)")
    val matchResult = regex.find(dataString)
    return matchResult?.groupValues?.get(1) ?: "Stationary"
}