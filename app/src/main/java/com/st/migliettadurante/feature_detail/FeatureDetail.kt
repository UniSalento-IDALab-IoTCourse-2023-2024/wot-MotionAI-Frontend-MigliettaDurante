package com.st.migliettadurante.feature_detail

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.har.migliettadurante.R

@SuppressLint("MissingPermission")
@Composable
fun FeatureDetail(
    navController: NavHostController,
    viewModel: FeatureDetailViewModel,
    deviceId: String,
    featureName: String
) {
    val backHandlingEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.startCalibration(deviceId, featureName)
    }

    BackHandler(enabled = backHandlingEnabled) {
        viewModel.disconnectFeature(deviceId = deviceId, featureName = featureName)
        navController.popBackStack()
    }

    val features = viewModel.featureUpdates
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.st_feature_featureNameLabel, featureName),
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
            text = stringResource(R.string.st_feature_updatesLabel),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp
            ),
            color = Color(0xFF007AFF),
            modifier = Modifier
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE3F2FD),
                contentColor = Color(0xFF000000)
            ),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Text(
                text = "${features.value?.data}",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                modifier = Modifier.padding(8.dp)
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
            Text("Indietro",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

    LaunchedEffect(true) {
        viewModel.observeFeature(deviceId = deviceId, featureName = featureName)
        viewModel.sendExtendedCommand(featureName = featureName, deviceId = deviceId)
    }
}
