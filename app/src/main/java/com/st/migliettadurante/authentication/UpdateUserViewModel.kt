package com.st.migliettadurante.authentication

import IoTAPIs.StoricoDatiClient
import IoTAPIs.model.EditUser
import IoTAPIs.model.MessageResponse
import IoTAPIs.model.User
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateUserViewModel : ViewModel() {
    private val _editUserResponse = MutableLiveData<User>()
    val editUserResponse: LiveData<User> get() = _editUserResponse

    private val _deleteUserResponse = MutableLiveData<MessageResponse>()
    val deleteUserResponse: LiveData<MessageResponse> get() = _deleteUserResponse

    private val _deleteRecognitionsResponse = MutableLiveData<MessageResponse>()
    val deleteRecognitionsResponse: LiveData<MessageResponse> get() = _deleteRecognitionsResponse

    private val _deleteActivitiesResponse = MutableLiveData<MessageResponse>()
    val deleteActivitiesResponse: LiveData<MessageResponse> get() = _deleteActivitiesResponse

    private val _errorEditUser = MutableLiveData<String>()
    val errorEditUser: LiveData<String> get() = _errorEditUser

    private val _errorDeleteUser = MutableLiveData<String>()
    val errorDeleteUser: LiveData<String> get() = _errorDeleteUser

    private val _errorDeleteRecognitions = MutableLiveData<String>()
    val errorDeleteRecognitions: LiveData<String> get() = _errorDeleteRecognitions

    private val _errorDeleteActivities = MutableLiveData<String>()
    val errorDeleteActivities: LiveData<String> get() = _errorDeleteActivities

    fun updateUser(body: EditUser, jwtToken: String) {
        viewModelScope.launch {
            try {

                val factory = ApiClientFactory()
                val client = factory.build(StoricoDatiClient::class.java)

                // Esegui la chiamata API
                val response = withContext(Dispatchers.IO) {
                    client.apiV1UserPutPut("Bearer $jwtToken", body)
                }

                // Imposta la risposta nel LiveData
                _editUserResponse.postValue(response!!)
            } catch (exception: Exception) {
                // In caso di errore, imposta l'errore nel LiveData
                _errorEditUser.postValue(exception.message)
            }
        }
    }

    fun deleteUser(jwtToken: String) {
        viewModelScope.launch {
            try {

                val factory = ApiClientFactory()
                val client = factory.build(StoricoDatiClient::class.java)

                // Esegui la chiamata API
                val response = withContext(Dispatchers.IO) {
                    client.apiV1UserDeleteDelete("Bearer $jwtToken")
                }

                // Imposta la risposta nel LiveData
                _deleteUserResponse.postValue(response!!)
            } catch (exception: Exception) {
                // In caso di errore, imposta l'errore nel LiveData
                _errorDeleteUser.postValue(exception.message)
            }
        }
    }

    fun deleteRecognitions(jwtToken: String) {
        viewModelScope.launch {
            try {

                val factory = ApiClientFactory()
                val client = factory.build(StoricoDatiClient::class.java)

                // Esegui la chiamata API
                val response = withContext(Dispatchers.IO) {
                    client.apiV1PredictionDeleteDelete("Bearer $jwtToken")
                }

                // Imposta la risposta nel LiveData
                _deleteRecognitionsResponse.postValue(response!!)
            } catch (exception: Exception) {
                // In caso di errore, imposta l'errore nel LiveData
                _errorDeleteRecognitions.postValue(exception.message)
            }
        }
    }

    fun deleteActivities(jwtToken: String) {
        viewModelScope.launch {
            try {

                val factory = ApiClientFactory()
                val client = factory.build(StoricoDatiClient::class.java)

                // Esegui la chiamata API
                val response = withContext(Dispatchers.IO) {
                    client.apiV1PredictionDeleteActivityDelete("Bearer $jwtToken")
                }

                // Imposta la risposta nel LiveData
                _deleteActivitiesResponse.postValue(response!!)
            } catch (exception: Exception) {
                // In caso di errore, imposta l'errore nel LiveData
                _errorDeleteActivities.postValue(exception.message)
            }
        }
    }
}