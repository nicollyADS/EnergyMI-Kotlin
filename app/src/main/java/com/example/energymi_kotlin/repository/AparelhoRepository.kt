package com.example.energymi_kotlin.repository

import android.util.Log
import com.example.energymi_kotlin.model.Aparelho
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class AparelhoRepository {

    private val BASE_URL = "http://10.0.2.2:8080/aparelhos"
    private val cliente = OkHttpClient()
    private val gson = Gson()

    fun buscarAparelhos(callback: (List<Aparelho>?, String?) -> Unit) {
        val request = Request.Builder()
            .url(BASE_URL)
            .get()
            .build()

        cliente.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("APARELHO_REPOSITORY", "Erro ao buscar aparelhos: ${e.message}")
                callback(null, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val respostaBody = response.body?.string()
                Log.i("APARELHO_REPOSITORY", "Resposta: $respostaBody")

                if (response.isSuccessful && !respostaBody.isNullOrEmpty()) {
                    val type = object : TypeToken<List<Aparelho>>() {}.type
                    val listaAparelhos: List<Aparelho> = gson.fromJson(respostaBody, type)
                    callback(listaAparelhos, null)
                } else {
                    callback(emptyList(), "Nenhum aparelho encontrado")
                }
            }
        })
    }

    fun gravarAparelho(aparelho: Aparelho, callback: (Boolean, String?) -> Unit) {
        val aparelhoJson = gson.toJson(aparelho)
        val requestBody = aparelhoJson.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(BASE_URL)
            .post(requestBody)
            .build()

        cliente.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("APARELHO_REPOSITORY", "Erro ao gravar aparelho: ${e.message}")
                callback(false, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val respostaBody = response.body?.string()
                if (response.isSuccessful) {
                    Log.i("APARELHO_REPOSITORY", "Aparelho gravado com sucesso. Resposta: $respostaBody")
                    callback(true, null)
                } else {
                    Log.e("APARELHO_REPOSITORY", "Erro ao gravar aparelho: ${response.message}")
                    callback(false, respostaBody ?: "Erro desconhecido")
                }
            }
        })
    }

    fun editarAparelho(aparelho: Aparelho, callback: (Boolean, String?) -> Unit) {
        val aparelhoJson = gson.toJson(aparelho)
        val requestBody = aparelhoJson.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$BASE_URL/${aparelho.id}")
            .put(requestBody)
            .build()

        cliente.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("APARELHO_REPOSITORY", "Erro ao editar aparelho: ${e.message}")
                callback(false, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val respostaBody = response.body?.string()
                if (response.isSuccessful) {
                    Log.i("APARELHO_REPOSITORY", "Aparelho editado com sucesso. Resposta: $respostaBody")
                    callback(true, null)
                } else {
                    Log.e("APARELHO_REPOSITORY", "Erro ao editar aparelho: ${response.message}")
                    callback(false, respostaBody ?: "Erro desconhecido")
                }
            }
        })
    }

    fun excluirAparelho(id: Long, callback: (Boolean, String?) -> Unit) {
        val request = Request.Builder()
            .url("$BASE_URL/$id")
            .delete()
            .build()

        cliente.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("APARELHO_REPOSITORY", "Erro ao excluir aparelho: ${e.message}")
                callback(false, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.i("APARELHO_REPOSITORY", "Aparelho excluído com sucesso.")
                    callback(true, null)
                } else {
                    Log.e("APARELHO_REPOSITORY", "Erro ao excluir aparelho: ${response.message}")
                    callback(false, response.body?.string() ?: "Erro desconhecido")
                }
            }
        })
    }
}