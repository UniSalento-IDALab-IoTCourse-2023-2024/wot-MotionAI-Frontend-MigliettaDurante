package com.st.migliettadurante.authentication

import IoTAPIs.StoricoDatiClient
import IoTAPIs.model.LoginResponse
import IoTAPIs.model.LoginUser
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel : ViewModel() {
    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> get() = _loginResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun login(body: LoginUser) {
        viewModelScope.launch {
            try {

                val factory = ApiClientFactory()
                val client = factory.build(StoricoDatiClient::class.java)

                // Esegui la chiamata API
                val response = withContext(Dispatchers.IO) {
                    client.apiV1LoginPost(body)
                }

                // Imposta la risposta nel LiveData
                _loginResponse.postValue(response!!)
            } catch (exception: Exception) {
                // In caso di errore, imposta l'errore nel LiveData
                _error.postValue(exception.message)
            }
        }
    }
}