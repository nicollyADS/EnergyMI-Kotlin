package com.example.energymi_kotlin.repository

import android.util.Log
import com.example.energymi_kotlin.model.Instalacao
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

class InstalacaoRepository {
    private val BASE_URL = "http://10.0.2.2:8080/instalacoes"
    private val cliente = OkHttpClient()
    private val gson = Gson()

    fun buscarInstalacoes(callback: (List<Instalacao>?, String?) -> Unit) {
        val request = Request.Builder()
            .url(BASE_URL)
            .get()
            .build()

        cliente.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("INSTALACAO_REPOSITORY", "Erro ao buscar instalacoes: ${e.message}")
                callback(null, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val respostaBody = response.body?.string()
                Log.i("INSTALACAO_REPOSITORY", "Resposta: $respostaBody")

                if (response.isSuccessful && !respostaBody.isNullOrEmpty()) {
                    val type = object : TypeToken<List<Instalacao>>() {}.type
                    val listaInstalacoes: List<Instalacao> = gson.fromJson(respostaBody, type)
                    callback(listaInstalacoes, null)
                } else {
                    callback(emptyList(), "Nenhuma instalacao encontrada")
                }
            }
        })
    }

    fun gravarInstalacao(instalacao: Instalacao, callback: (Boolean, String?) -> Unit) {
        val instalacaoJson = gson.toJson(instalacao)
        val requestBody = instalacaoJson.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(BASE_URL)
            .post(requestBody)
            .build()

        cliente.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("INSTALACAO_REPOSITORY", "Erro ao gravar instalacao: ${e.message}")
                callback(false, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val respostaBody = response.body?.string()
                if (response.isSuccessful) {
                    Log.i("INSTALACAO_REPOSITORY", "Instalacao gravada com sucesso. Resposta: $respostaBody")
                    callback(true, null)
                } else {
                    Log.e("INSTALACAO_REPOSITORY", "Erro ao gravar instalacao: ${response.message}")
                    callback(false, respostaBody ?: "Erro desconhecido")
                }
            }
        })
    }

    fun editarInstalacao(instalacao: Instalacao, callback: (Boolean, String?) -> Unit) {
        val instalacaoJson = gson.toJson(instalacao)
        val requestBody = instalacaoJson.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$BASE_URL/${instalacao.id}")
            .put(requestBody)
            .build()

        cliente.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("INSTALACAO_REPOSITORY", "Erro ao editar instalacao: ${e.message}")
                callback(false, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val respostaBody = response.body?.string()
                if (response.isSuccessful) {
                    Log.i("INSTALACAO_REPOSITORY", "Instalacao editada com sucesso. Resposta: $respostaBody")
                    callback(true, null)
                } else {
                    Log.e("INSTALACAO_REPOSITORY", "Erro ao editar instalacao: ${response.message}")
                    callback(false, respostaBody ?: "Erro desconhecido")
                }
            }
        })
    }

    fun excluirInstalacao(id: Long, callback: (Boolean, String?) -> Unit) {
        val request = Request.Builder()
            .url("$BASE_URL/$id")
            .delete()
            .build()

        cliente.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("INSTALACAO_REPOSITORY", "Erro ao excluir instalacao: ${e.message}")
                callback(false, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.i("INSTALACAO_REPOSITORY", "Instalacao excluída com sucesso.")
                    callback(true, null)
                } else {
                    Log.e("INSTALACAO_REPOSITORY", "Erro ao excluir instalacao: ${response.message}")
                    callback(false, response.body?.string() ?: "Erro desconhecido")
                }
            }
        })
    }
}