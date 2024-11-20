package com.example.energymi_kotlin.repository

import android.util.Log
import com.example.energymi_kotlin.model.Consumo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class ConsumoRepository {
    private val BASE_URL = "http://10.0.2.2:8080/consumos"
    private val cliente = OkHttpClient()
    private val gson = Gson()

    fun buscarConsumos(callback: (List<Consumo>?, String?) -> Unit) {
        val request = Request.Builder()
            .url(BASE_URL)
            .get()
            .build()

        cliente.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("CONSUMO_REPOSITORY", "Erro ao buscar consumos: ${e.message}", e)
                callback(null, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    val errorMessage = "Erro na resposta: ${response.message}"
                    Log.e("CONSUMO_REPOSITORY", errorMessage)
                    callback(null, errorMessage)
                    return
                }

                val respostaBody = response.body?.string()
                Log.i("CONSUMO_REPOSITORY", "Resposta: $respostaBody")

                if (respostaBody.isNullOrEmpty()) {
                    callback(emptyList(), "Nenhum consumo encontrado")
                    return
                }

                try {
                    val type = object : TypeToken<List<Consumo>>() {}.type
                    val listaConsumos: List<Consumo> = gson.fromJson(respostaBody, type)
                    callback(listaConsumos, null)
                } catch (e: Exception) {
                    Log.e("CONSUMO_REPOSITORY", "Erro ao processar resposta: ${e.message}", e)
                    callback(emptyList(), "Erro ao processar resposta")
                }
            }
        })
    }
}