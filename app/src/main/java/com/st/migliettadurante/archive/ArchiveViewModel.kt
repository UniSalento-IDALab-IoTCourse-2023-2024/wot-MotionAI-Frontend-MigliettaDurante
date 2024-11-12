package com.st.migliettadurante.archive

import IoTAPIs.StoricoDatiClient
import IoTAPIs.model.ActivityHistory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ArchiveViewModel : ViewModel() {
    private val _activitiesResponse = MutableLiveData<ActivityHistory>()
    val activitiesResponse: LiveData<ActivityHistory> get() = _activitiesResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun getAllActivities(jwtToken: String) {
        viewModelScope.launch {
            try {

                val factory = ApiClientFactory()
                val client = factory.build(StoricoDatiClient::class.java)

                // Esegui la chiamata API
                val response = withContext(Dispatchers.IO) {
                    client.apiV1PredictionGetAllByUserGet("Bearer $jwtToken")
                }

                // Imposta la risposta nel LiveData
                _activitiesResponse.postValue(response!!)
            } catch (exception: Exception) {
                // In caso di errore, imposta l'errore nel LiveData
                _error.postValue(exception.message)
            }
        }
    }
}