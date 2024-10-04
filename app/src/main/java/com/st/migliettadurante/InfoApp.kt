package com.st.migliettadurante

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.har.migliettadurante.R
import com.st.migliettadurante.ui.theme.StDemoTheme

@Composable
fun InfoApp(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f) // Occupa lo spazio disponibile in alto
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                BasicText(
                    text = "In breve..",
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_info),
                                contentDescription = "Icona informazioni",
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "A cosa serve?",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = "Questa applicazione utilizza una SensorTile.Box PRO per monitorare e riconoscere le attività svolte dall'utente, tra cui:",
                            fontSize = 17.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 50.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ActivityList()
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_features),
                                contentDescription = "Icona funzionalità",
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Quali funzionalità offre?",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• Rilevamento attività in tempo reale tramite Machine Learning.\n" +
                                    "• Connessione rapida ai dispositivi Bluetooth.\n" +
                                    "• Visualizzazione intuitiva e moderna.",
                            fontSize = 16.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 56.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Button(
                onClick = { navController.navigate("welcome") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2F3F4)),
                modifier = Modifier
                    .padding(end = 8.dp)
                    .weight(1f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Icona Indietro",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Indietro",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = { navController.navigate("list") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A7BD5)),
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "Icona Ricerca",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Scansiona",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun ActivityList() {
    Column(modifier = Modifier.padding(start = 56.dp)) {
        ActivityItem(text = "Walk", iconResId = R.drawable.walk_image)
        ActivityItem(text = "Run", iconResId = R.drawable.run_image)
        ActivityItem(text = "Stop", iconResId = R.drawable.stop_image)
        ActivityItem(text = "Drive", iconResId = R.drawable.drive_image)
    }
}

@Composable
fun ActivityItem(text: String, iconResId: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = "$text icon",
            modifier = Modifier.size(42.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 16.sp, color = Color.Black)
    }
}

@Preview
@Composable
fun InfoAppPreview() {
    StDemoTheme {
        InfoApp(navController = rememberNavController())
    }
}
