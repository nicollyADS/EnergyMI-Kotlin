package com.example.energymi_kotlin.activity

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.example.energymi_kotlin.R
import com.example.energymi_kotlin.model.Consumo
import com.example.energymi_kotlin.repository.ConsumoRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ConsumoActivity : Activity() {

    private lateinit var consumoRepository: ConsumoRepository
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.consumo)

        val listViewConsumos = findViewById<ListView>(R.id.listViewConsumo)

        sharedPreferences = getSharedPreferences("ConsumoPrefs", Context.MODE_PRIVATE)

        consumoRepository = ConsumoRepository()

        atualizarListaConsumos(listViewConsumos)
    }

    private fun atualizarListaConsumos(listView: ListView) {
        consumoRepository.buscarConsumos() { consumos, erro ->
            runOnUiThread {
                if (erro != null) {
                    Toast.makeText(this, "Erro ao buscar consumos: $erro", Toast.LENGTH_SHORT).show()
                    val consumosSalvos = recuperarConsumosDoSharedPreferences()
                    atualizarListView(listView, consumosSalvos)
                } else {
                    if (consumos != null) {
                        salvarConsumosNoSharedPreferences(consumos)
                        atualizarListView(listView, consumos.map {
                            "Data: ${it.data}\nNúmero: ${it.numero}\nCusto: ${it.custo}\nObservações: ${it.observacoes}"
                        })
                    }
                }
            }
        }
    }

    private fun salvarConsumosNoSharedPreferences(consumos: List<Consumo>) {
        val editor = sharedPreferences.edit()
        val jsonConsumos = gson.toJson(consumos)
        editor.putString("consumos", jsonConsumos)
        editor.apply()
    }

    private fun recuperarConsumosDoSharedPreferences(): List<String> {
        val jsonConsumos = sharedPreferences.getString("consumos", null)
        return if (jsonConsumos != null) {
            val type = object : TypeToken<List<Consumo>>() {}.type
            val consumos: List<Consumo> = gson.fromJson(jsonConsumos, type)
            consumos.map { "Data: ${it.data}\nNúmero: ${it.numero}\nCusto: ${it.custo}\nObservações: ${it.observacoes}"
            }
        } else {
            emptyList()
        }
    }

    private fun atualizarListView(listView: ListView, listaConsumosStr: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaConsumosStr)
        listView.adapter = adapter
    }
}