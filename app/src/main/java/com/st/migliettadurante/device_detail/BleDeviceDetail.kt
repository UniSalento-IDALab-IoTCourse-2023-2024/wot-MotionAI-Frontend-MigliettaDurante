package com.st.migliettadurante.device_detail

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.st.blue_sdk.models.NodeState
import com.har.migliettadurante.R
import com.st.migliettadurante.authentication.SecureStorageManager

@SuppressLint("MissingPermission")
@Composable
fun BleDeviceDetail(
    navController: NavHostController,
    viewModel: BleDeviceDetailViewModel?,
    deviceId: String
) {

    val secureStorageManager = SecureStorageManager(LocalContext.current)

    LaunchedEffect(key1 = deviceId) {
        viewModel?.connect(deviceId = deviceId)
        secureStorageManager.saveDeviceId(deviceId)
    }

    val bleDevice = viewModel?.bleDevice(deviceId = deviceId)?.collectAsState(null)
    val features = viewModel?.features?.collectAsState()
    var connected = bleDevice?.value?.connectionStatus?.current == NodeState.Ready

    bleDevice?.value?.connectionStatus?.current.let {
        connected = it == NodeState.Ready
    }


    if (bleDevice?.value?.connectionStatus?.current == NodeState.Ready) {
        viewModel.getFeatures(deviceId = deviceId)
    }

    val backHandlingEnabled by remember { mutableStateOf(true) }

    BackHandler(enabled = backHandlingEnabled) {
        viewModel?.disconnect(deviceId = deviceId)
        navController.popBackStack()
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .scrollable(
                state = scrollState,
                orientation = Orientation.Vertical
            )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (viewModel?.showAudioBtn(deviceId) == true) {
                Text(
                    text = stringResource(R.string.st_testAudio),
                    color = Color(0xFF007AFF),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    enabled = bleDevice?.value?.connectionStatus?.current == NodeState.Ready,
                    onClick = {
                        navController.navigate("audio/$deviceId")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = null,
                        tint = Color(0xFF007AFF)
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_device),
                    contentDescription = "Device Icon",
                    tint = Color(0xFF3B82F6),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Dispositivo collegato:",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = Color(0xFF374151)
                    )
                    Text(
                        text = bleDevice?.value?.device?.name ?: "N/A",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280)
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_status),
                    contentDescription = "Status Icon",
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Stato attuale:",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = Color(0xFF374151)
                    )
                    Text(
                        text = bleDevice?.value?.connectionStatus?.current?.name?.uppercase()
                            ?: "DISCONNECTED",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF6B7280)
                    )
                }
            }
        }

        Text(
            text = "Funzioni disponibili:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .padding(horizontal = 16.dp)
        )

        val filteredFeatures = features?.value?.filter {
            it.name == "Accelerometer" || it.name == "Gyroscope" || it.name == "Magnetometer" || it.name == "Activity Recognition"
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(horizontal = 10.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            if (!filteredFeatures.isNullOrEmpty())
                itemsIndexed(items = filteredFeatures) { index, item ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .padding(vertical = 6.dp, horizontal = 5.dp)
                            .clickable {
                                if (item.name == "Activity Recognition") {
                                    navController.navigate("feature/$deviceId/${item.name}/activity")
                                } else {
                                    navController.navigate("feature/$deviceId/${item.name}")
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE3F2FD),
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_feature),
                                contentDescription = "Feature Icon",
                                tint = Color(0xFF007AFF),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = if(item.name == "Activity Recognition") "HAR SensorTile.box PRO" else item.name,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                    }

                    if (filteredFeatures.lastIndex != index) {
                        Divider(color = Color(0xFFE5E7EB), thickness = 1.dp)
                    }
                }

            if (bleDevice?.value?.connectionStatus?.current == NodeState.Ready) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .padding(vertical = 6.dp, horizontal = 5.dp)
                            .clickable {
                                navController.navigate("feature/$deviceId/Activity Recognition/HARSmartphone")

                            },
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE3F2FD),
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_feature),
                                contentDescription = "Feature Icon",
                                tint = Color(0xFF007AFF),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "HAR Smartphone e SensorTile",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                    }
                }
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .padding(vertical = 6.dp, horizontal = 5.dp)
                            .clickable {
                                navController.navigate("feature/$deviceId/Accelerometer/smartphone")

                            },
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE3F2FD),
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_feature),
                                contentDescription = "Feature Icon",
                                tint = Color(0xFF007AFF),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "HAR su Smartphone",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                    }
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            Button(
                onClick = {
                    navController.navigate("dashboard")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A7BD5)),
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.dashboard),
                    contentDescription = "Icona Dashboard",
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Dashboard",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = {
                    viewModel?.disconnect(deviceId = deviceId)
                    navController.navigate("list")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2F3F4)),
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.disconnect),
                    contentDescription = "Icona Disconnesso",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Disconnetti",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview
@Composable
fun BleDeviceDetailPreview() {
    BleDeviceDetail(
        navController = rememberNavController(),
        viewModel = null,
        deviceId = "00:00:00:00:00:00"
    )
}