package com.st.migliettadurante.dashboard

import IoTAPIs.StoricoDatiClient
import IoTAPIs.model.ActivityDuration
import IoTAPIs.model.Estimate
import IoTAPIs.model.UserResponse
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class DashboardViewModel : ViewModel() {
    private val _getUserResponse = MutableLiveData<UserResponse>()
    val getUserResponse: LiveData<UserResponse> get() = _getUserResponse

    private val _getActivityDurationResponse = MutableLiveData<ActivityDuration>()
    val getActivityDurationResponse: LiveData<ActivityDuration> get() = _getActivityDurationResponse

    private val _getWeeklyEstimateResponse = MutableLiveData<Estimate>()
    val getWeeklyEstimateResponse: LiveData<Estimate> get() = _getWeeklyEstimateResponse

    private val _errorGetUser = MutableLiveData<String>()
    val errorGetUser: LiveData<String> get() = _errorGetUser

    private val _errorGetActivities = MutableLiveData<String>()
    val errorGetActivities: LiveData<String> get() = _errorGetActivities

    private val _errorGetWeeklyEstimate = MutableLiveData<String>()
    val errorGetWeeklyEstimate: LiveData<String> get() = _errorGetWeeklyEstimate

    fun decodeJWT(jwt: String): JSONObject {
        val split = jwt.split(".")
        val base64EncodedBody = split[1]
        val body = String(Base64.decode(base64EncodedBody, Base64.DEFAULT))
        return JSONObject(body)
    }

    fun getUser(jwtToken: String) {
        viewModelScope.launch {
            try {

                val factory = ApiClientFactory()
                val client = factory.build(StoricoDatiClient::class.java)

                // Esegui la chiamata API
                val response = withContext(Dispatchers.IO) {
                    client.apiV1UserGetGet("Bearer ${jwtToken}")
                }

                // Imposta la risposta nel LiveData
                _getUserResponse.postValue(response!!)
            } catch (exception: Exception) {
                // In caso di errore, imposta l'errore nel LiveData
                _errorGetUser.postValue(exception.message)
            }
        }
    }

    fun getActivityDuration(date: String, jwtToken: String) {
        viewModelScope.launch {
            try {

                val factory = ApiClientFactory()
                val client = factory.build(StoricoDatiClient::class.java)

                // Esegui la chiamata API
                val response = withContext(Dispatchers.IO) {
                    client.apiV1PredictionActivityDurationGet(date, "Bearer $jwtToken")
                }

                // Imposta la risposta nel LiveData
                _getActivityDurationResponse.postValue(response!!)
            } catch (exception: Exception) {
                // In caso di errore, imposta l'errore nel LiveData
                _errorGetActivities.postValue(exception.message)
            }
        }
    }

    fun getWeeklyEstimate(jwtToken: String) {
        viewModelScope.launch {
            try {

                val factory = ApiClientFactory()
                val client = factory.build(StoricoDatiClient::class.java)

                // Esegui la chiamata API
                val response = withContext(Dispatchers.IO) {
                    client.apiV1PredictionEstimateGet("Bearer ${jwtToken}")
                }

                // Imposta la risposta nel LiveData
                _getWeeklyEstimateResponse.postValue(response!!)
            } catch (exception: Exception) {
                // In caso di errore, imposta l'errore nel LiveData
                _errorGetWeeklyEstimate.postValue(exception.message)
            }
        }
    }
}