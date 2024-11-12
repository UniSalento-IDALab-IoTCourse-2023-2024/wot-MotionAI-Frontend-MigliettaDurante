package com.st.migliettadurante.authentication

import IoTAPIs.StoricoDatiClient
import IoTAPIs.model.User
import IoTAPIs.model.UserResponse
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistrationViewModel : ViewModel() {
    private val _registrationResponse = MutableLiveData<UserResponse>()
    val registrationResponse: LiveData<UserResponse> get() = _registrationResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun registration(body: User) {
        viewModelScope.launch {
            try {

                val factory = ApiClientFactory()
                val client = factory.build(StoricoDatiClient::class.java)

                // Esegui la chiamata API
                val response = withContext(Dispatchers.IO) {
                    client.apiV1RegistrationPost(body)
                }

                // Imposta la risposta nel LiveData
                _registrationResponse.postValue(response!!)
            } catch (exception: Exception) {
                // In caso di errore, imposta l'errore nel LiveData
                _error.postValue(exception.message)
            }
        }
    }
}