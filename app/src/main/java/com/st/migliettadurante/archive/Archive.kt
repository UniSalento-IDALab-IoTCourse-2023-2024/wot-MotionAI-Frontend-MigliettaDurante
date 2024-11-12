package com.st.migliettadurante.archive

import IoTAPIs.model.ActivityHistoryItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.har.migliettadurante.R
import com.har.migliettadurante.R.drawable.drive_image
import com.har.migliettadurante.R.drawable.run_image
import com.har.migliettadurante.R.drawable.stop_image
import com.har.migliettadurante.R.drawable.walk_image
import com.st.migliettadurante.authentication.SecureStorageManager

@Composable
fun Archive(
    navController: NavController,
    viewModel: ArchiveViewModel,
) {
    val activities = viewModel.activitiesResponse.observeAsState()
    val secureStorageManager = SecureStorageManager(LocalContext.current)

    LaunchedEffect(Unit) {
        val jwtToken = secureStorageManager.getJwt()
        if (jwtToken != null) {
            viewModel.getAllActivities(jwtToken)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Attività",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                ),
                color = Color(0xFF374151),
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(
                        activities.value ?: emptyList<ActivityHistoryItem>()
                    ) { index: Int, activity: ActivityHistoryItem ->
                        ActivityItem(
                            activity = activity,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
    }
}

@Composable
fun ActivityItem(activity: ActivityHistoryItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(4.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = cardColors(
            containerColor = Color(0xFFE3F2FD)
        )
    ) {
        Column(
            modifier = Modifier.background(color = Color.Transparent)
        ) {
            Text(
                text = "Attività svolta in data: ${activity.date}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold, fontSize = 16.sp
                ),
                modifier = Modifier.padding(18.dp)
            )

            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = walk_image),
                    contentDescription = "Walking Icon",
                    modifier = Modifier.size(45.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Walking: ${calculateDuration(activity.activity.walking)}",
                    modifier = Modifier.padding(8.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = stop_image),
                    contentDescription = "Stationary Icon",
                    modifier = Modifier.size(45.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Stationary: ${calculateDuration(activity.activity.stationary)}",
                    modifier = Modifier.padding(8.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = run_image),
                    contentDescription = "Running Icon",
                    modifier = Modifier.size(45.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Running: ${calculateDuration(activity.activity.running)}",
                    modifier = Modifier.padding(8.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = drive_image),
                    contentDescription = "Driving Icon",
                    modifier = Modifier.size(45.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Driving: ${calculateDuration(activity.activity.driving)}",
                    modifier = Modifier.padding(8.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
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
private fun ArchivePreview() {
    Archive(
        navController = rememberNavController(),
        viewModel = ArchiveViewModel(),
    )
}

@Preview
@Composable
private fun ActivityItemPreview() {
    val activity = ActivityHistoryItem()
    activity.date = "10-10-2021"
    activity.activity.walking = 10000
    activity.activity.stationary = 20000
    activity.activity.running = 30000
    activity.activity.driving = 40000

    ActivityItem(
        activity = activity
    )
}