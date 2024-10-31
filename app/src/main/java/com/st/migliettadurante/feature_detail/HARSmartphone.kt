package com.st.migliettadurante.feature_detail

import MqttManager
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import com.har.migliettadurante.R
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import kotlin.toString

@SuppressLint("MissingPermission")
@Composable
fun HARSmartphone(
    navController: NavHostController,
    viewModel: FeatureDetailViewModel?,
    deviceId: String,
    featureName: String
) {
    val backHandlingEnabled by remember { mutableStateOf(true) }

    val mqttManager = MqttManager("tcp://test.mosquitto.org:1883", "test_publisher")
    mqttManager.connect()

    fun sendPredictionToCloud(prediction: String) {
        val json = JSONObject()
        json.put("device_id", deviceId)
        json.put("activity", prediction)

        mqttManager.publish("accelerometer/prediction", json.toString())
    }

    LaunchedEffect(Unit) {
        viewModel?.startCalibration(deviceId, "Accelerometer")
        viewModel?.startCalibration(deviceId, featureName)
    }

    BackHandler(enabled = backHandlingEnabled) {
        viewModel?.disconnectFeature(deviceId = deviceId, featureName = "Accelerometer")
        viewModel?.disconnectFeature(deviceId = deviceId, featureName = featureName)

        navController.popBackStack()
    }

    val features = viewModel?.featureUpdates

    val currentActivity = remember { mutableStateOf("") }
    val randomForestActivity = remember { mutableStateOf("") }

    if (features != null) {
        LaunchedEffect(features.value) {
            val dataString = features.value?.data?.toString() ?: ""
            currentActivity.value = extractPrevisionFromLoggable(dataString)
        }

        val xValues = remember { mutableStateListOf<Float>() }
        val yValues = remember { mutableStateListOf<Float>() }
        val zValues = remember { mutableStateListOf<Float>() }

        val context = LocalContext.current
        LaunchedEffect(features.value) {
            val dataString = features.value?.data?.toString() ?: ""
            val (x, y, z) = extractCoordinatesFromLoggable(dataString)
            Log.d("Accelerometer", "Coordinates: X=$x, Y=$y, Z=$z")

            xValues.add(x)
            yValues.add(y)
            zValues.add(z)

            val n_rows = 60

            if (xValues.size == n_rows && yValues.size == n_rows && zValues.size == n_rows) {
                Log.d("Accelerometer", "Collected 33 values for x, y, and z")

                val xStats = calculateStatistics(xValues.toFloatArray())
                val yStats = calculateStatistics(yValues.toFloatArray())
                val zStats = calculateStatistics(zValues.toFloatArray())

                val stats = FloatArray(12)

                for (i in 0 until 4) {
                    val j = i * 3
                    stats[j] = xStats[i]
                    stats[j + 1] = yStats[i]
                    stats[j + 2] = zStats[i]
                }

                Log.d("Accelerometer", "Statistics: ${stats.contentToString()}")

                val prediction = sendToRandomForestModel(context, stats)
                randomForestActivity.value = prediction

                sendPredictionToCloud(randomForestActivity.value)

                xValues.clear()
                yValues.clear()
                zValues.clear()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {

        Text(
            text = stringResource(R.string.st_feature_featureNameLabel, "HAR Smartphone"),
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

        var name = ""
        if (currentActivity.value != "") {
            if(currentActivity.value == "Jogging") {
                name = "Running"
            } else {
                name = currentActivity.value
            }
        } else {
            name = "Attività non rilevata"
        }

        RecognitionCard(
            activityName = name,
            imageRes = when (currentActivity.value) {
                "Jogging" -> R.drawable.run_image
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
            activityName = if (randomForestActivity.value != "") randomForestActivity.value else "Attività non rilevata",
            imageRes = when (randomForestActivity.value) {
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
            text = "Random Forest in Cloud:",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            color = Color(0xFF374151),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val cloudActivity = remember { mutableStateOf("") }

        RecognitionCard(
            activityName = if (cloudActivity.value != "") cloudActivity.value else "Attività non rilevata",
            imageRes = when (cloudActivity.value) {
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
        viewModel?.observeFeature(deviceId = deviceId, featureName = featureName)
        viewModel?.sendExtendedCommand(featureName = featureName, deviceId = deviceId)
        viewModel?.observeFeature(deviceId = deviceId, featureName = "Accelerometer")
        viewModel?.sendExtendedCommand(featureName = "Accelerometer", deviceId = deviceId)
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
            .height(100.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
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
    return matchResult?.groupValues?.get(1) ?: "Stationary"
}

fun extractCoordinatesFromLoggable(dataString: String): Array<Float> {
    // Regex per estrarre i valori delle coordinate dell'accelerometro
    val xRegex = Regex("X\\s*=\\s*([-+]?\\d*\\.?\\d+)\\s*mg")
    val yRegex = Regex("Y\\s*=\\s*([-+]?\\d*\\.?\\d+)\\s*mg")
    val zRegex = Regex("Z\\s*=\\s*([-+]?\\d*\\.?\\d+)\\s*mg")

    val xResult = xRegex.find(dataString)
    val yResult = yRegex.find(dataString)
    val zResult = zRegex.find(dataString)

    val xValue = xResult?.groupValues?.get(1)?.toFloat() ?: 0.0f
    val yValue = yResult?.groupValues?.get(1)?.toFloat() ?: 0.0f
    val zValue = zResult?.groupValues?.get(1)?.toFloat() ?: 0.0f

    return arrayOf(xValue, yValue, zValue)
}

fun calculateStatistics(data: FloatArray): FloatArray {
    val mean = data.average().toFloat()
    val variance = data.map { (it - mean) * (it - mean) }.average().toFloat()
    val peakToPeak = data.maxOrNull()!! - data.minOrNull()!!

    var zeroCrossings = 0
    for (i in 0 until data.size - 1) {
        if (data[i] * data[i + 1] < 0) {
            zeroCrossings++
        }
    }

    return floatArrayOf(mean, variance, peakToPeak, zeroCrossings.toFloat())
}

fun sendToRandomForestModel(context: Context, stats: FloatArray): String {
    // Inizializza l'ambiente ONNX Runtime
    val env = OrtEnvironment.getEnvironment()

    // Carica il modello ONNX
    val inputStream = context.resources.openRawResource(R.raw.random_forest)
    val modelFile = File(context.cacheDir, "random_forest.onnx")

    // Scrivi il file nella cache
    FileOutputStream(modelFile).use { outputStream ->
        inputStream.copyTo(outputStream)
    }

    // Ora puoi passare il percorso del file come stringa a createSession
    val session = env.createSession(modelFile.absolutePath, OrtSession.SessionOptions())

    // Prepara i dati di input per il modello
    val inputTensor = OnnxTensor.createTensor(env, arrayOf(stats))

    // Esegui l'inferenza
    val results = session.run(mapOf("float_input" to inputTensor))

    // Ottieni il risultato come stringa
    val outputTensor = results[0].value as Array<*>
    val output = outputTensor[0]

    // Rilascia le risorse
    inputTensor.close()
    results.close()
    session.close()
    env.close()

    Log.d("RandomForest", "Prediction: $output")

    return output.toString()
}

@Preview
@Composable
private fun HARSmartphonePreview() {
    HARSmartphone(
        navController = rememberNavController(),
        viewModel = null,
        deviceId = "deviceID",
        featureName = "featureName"
    )
}