package com.st.migliettadurante.foreground_service

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.st.blue_sdk.BlueManager
import com.st.blue_sdk.features.FeatureUpdate
import com.st.migliettadurante.authentication.SecureStorageManager
import com.st.migliettadurante.feature_detail.RecognitionViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlin.collections.set

@HiltWorker
class RecognitionWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val blueManager: BlueManager
) : CoroutineWorker(appContext, workerParams) {

    private val recognitionViewModel = RecognitionViewModel()

    override suspend fun doWork(): Result {
        Log.d("RecognitionWorker", "doWork")

        val deviceId = inputData.getString("deviceId") ?: return Result.failure()
        val secureStorageManager = SecureStorageManager(applicationContext)
        val jwtToken = secureStorageManager.getJwt() ?: return Result.failure()
        val context = applicationContext

        var prevData = ""
        while (true) {
            observeFeature(deviceId)
            val feature = _featureUpdates.value[0]
            var dataString = feature?.data.toString()

            if (dataString != prevData) {

                recognitionViewModel.executeAccelerometerEffect(
                    context = context,
                    dataString = dataString,
                    jwtToken = jwtToken,
                    deviceId = deviceId,
                )
                prevData = dataString
            }
        }
        return Result.success()
    }

    private val _featureUpdates = mutableStateOf(arrayOfNulls<FeatureUpdate<*>>(1))
    private val observeFeatureJobs = mutableMapOf<String, Job>()

    private fun observeFeature(deviceId: String) {
        observeFeatureJobs["Accelerometer"]?.cancel()

        blueManager.nodeFeatures(deviceId).find { it.name == "Accelerometer" }?.let { feature ->
            val job = blueManager.getFeatureUpdates(nodeId = deviceId, features = listOf(feature))
                .flowOn(Dispatchers.IO)
                .onEach {
                    _featureUpdates.value = _featureUpdates.value.copyOf().apply { this[0] = it }
                    Log.d("Accelerometer RecognitionWorker", "Feature update received: ${_featureUpdates.value[0]}")
                }

            observeFeatureJobs["Accelerometer"] = job as Job
        } ?: run {
        }
    }
}