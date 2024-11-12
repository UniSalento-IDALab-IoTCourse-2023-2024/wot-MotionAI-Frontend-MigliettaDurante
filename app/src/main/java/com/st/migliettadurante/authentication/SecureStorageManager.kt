package com.st.migliettadurante.authentication

import IoTAPIs.model.UserResponse
import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class SecureStorageManager(context: Context) {

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private val sharedPreferences = EncryptedSharedPreferences.create(
        "MyEncryptedPrefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveJwt(jwt: String) {
        val editor = sharedPreferences.edit()
        editor.putString("jwt_token", jwt)
        editor.apply()
    }

    fun getJwt(): String? {
        return sharedPreferences.getString("jwt_token", null)
    }

    fun clearJwt() {
        val editor = sharedPreferences.edit()
        editor.remove("jwt_token")
        editor.apply()
    }

    fun saveUser(user: UserResponse) {
        val editor = sharedPreferences.edit()
        editor.putString("user_id", user.id)
        editor.putString("user_name", user.nome)
        editor.putString("user_email", user.email)
        editor.putString("user_cognome", user.cognome)
        editor.putString("user_data_nascita", user.dataNascita)
        editor.apply()
    }

    fun getUser(): UserResponse {
        val user = UserResponse()
        user.id = sharedPreferences.getString("user_id", null)
        user.nome = sharedPreferences.getString("user_name", null)
        user.email = sharedPreferences.getString("user_email", null)
        user.cognome = sharedPreferences.getString("user_cognome", null)
        user.dataNascita = sharedPreferences.getString("user_data_nascita", null)

        return user
    }

    fun isUserSaved(): Boolean {
        return sharedPreferences.getString("user_id", null) != null
    }

    fun clearUser() {
        val editor = sharedPreferences.edit()
        editor.remove("user_id")
        editor.remove("user_name")
        editor.remove("user_email")
        editor.remove("user_cognome")
        editor.remove("user_data_nascita")
        editor.apply()
    }

    fun saveDeviceId(deviceId: String) {
        val editor = sharedPreferences.edit()
        editor.putString("device_id", deviceId)
        editor.apply()
    }

    fun getDeviceId(): String? {
        return sharedPreferences.getString("device_id", null)
    }

    fun clearDeviceId() {
        val editor = sharedPreferences.edit()
        editor.remove("device_id")
        editor.apply()
    }

}
