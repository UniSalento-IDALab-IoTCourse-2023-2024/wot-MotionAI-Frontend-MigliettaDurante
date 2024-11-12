package com.st.migliettadurante.feature_detail

import IoTAPIs.StoricoDatiClient
import IoTAPIs.model.InsertPrediction
import IoTAPIs.model.MessageResponse
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory
import com.har.migliettadurante.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class RecognitionViewModel : ViewModel() {
    private val _insertRecognitionResponse = MutableLiveData<MessageResponse>()
    val insertRecognitionResponse: LiveData<MessageResponse> get() = _insertRecognitionResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun saveRecognition(jwtToken: String, body: InsertPrediction) {
        viewModelScope.launch {
            try {

                val factory = ApiClientFactory()
                val client = factory.build(StoricoDatiClient::class.java)

                // Esegui la chiamata API
                val response = withContext(Dispatchers.IO) {
                    client.apiV1PredictionInsertPost("Bearer $jwtToken", body)
                }

                // Imposta la risposta nel LiveData
                _insertRecognitionResponse.postValue(response!!)
            } catch (exception: Exception) {
                // In caso di errore, imposta l'errore nel LiveData
                _error.postValue(exception.message)
            }
        }
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

    fun getRecognitionFromRandomForest(context: Context, stats: FloatArray): String {
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
}