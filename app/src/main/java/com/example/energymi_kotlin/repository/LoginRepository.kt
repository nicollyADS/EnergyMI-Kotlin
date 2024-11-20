package com.example.energymi_kotlin.repository

import com.example.energymi_kotlin.model.Login
import okhttp3.*
import java.io.IOException

class LoginRepository {

    private val BASE_URL = "http://10.0.2.2:8080/logins"
    private val cliente = OkHttpClient()

    fun fazerLogin(login: Login, callback: (Boolean, String?) -> Unit) {
        val requestBody = FormBody.Builder()
            .add("email", login.email)
            .add("senha", login.senha)
            .build()

        val request = Request.Builder()
            .url(BASE_URL)
            .post(requestBody)
            .build()

        cliente.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, "Credenciais inválidas")
                }
            }
        })
    }
}