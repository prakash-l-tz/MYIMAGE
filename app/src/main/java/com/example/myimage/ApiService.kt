package com.example.myimage

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

object ApiService {

    private val client = OkHttpClient()
    
    // Use 10.0.2.2 for Emulator. Use your PC IP (e.g., 192.168.x.x) for physical device.
    private const val BASE_URL = "http://10.0.2.2:3000"

    fun sendOtp(email: String, callback: (Boolean) -> Unit) {
        val json = JSONObject().apply {
            put("email", email)
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$BASE_URL/send-otp")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ApiService", "Network Error: ${e.message}")
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("ApiService", "Response Code: ${response.code}")
                callback(response.isSuccessful)
            }
        })
    }

    fun verifyOtp(email: String, otp: String, callback: (Boolean) -> Unit) {
        val json = JSONObject().apply {
            put("email", email)
            put("otp", otp)
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$BASE_URL/verify-otp")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response.isSuccessful)
            }
        })
    }
}
