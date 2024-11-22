package com.example.energymi_kotlin.activity

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.energymi_kotlin.R
import com.example.energymi_kotlin.model.Aparelho
import com.example.energymi_kotlin.repository.AparelhoRepository

class AparelhoCadastrarActivity : Activity() {
    private lateinit var aparelhoRepository: AparelhoRepository
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.cadastrar_aparelho)

        sharedPreferences = getSharedPreferences("AparelhoPrefs", Context.MODE_PRIVATE)

        val edtNome = findViewById<EditText>(R.id.edtNome)
        val edtTipo = findViewById<EditText>(R.id.edtTipo)
        val edtWatts = findViewById<EditText>(R.id.edtWatts)
        val btnEnviar = findViewById<Button>(R.id.btnEnviar)

        edtNome.setText(sharedPreferences.getString("nome", ""))
        edtTipo.setText(sharedPreferences.getString("tipo", ""))

        val watts = sharedPreferences.getInt("watts", 0)
        edtWatts.setText(watts.toString())

        aparelhoRepository = AparelhoRepository()

        btnEnviar.setOnClickListener {
            val nome = edtNome.text.toString()
            val tipo = edtTipo.text.toString()

            val watts = edtWatts.text.toString().toIntOrNull() ?: 0

            val aparelho = Aparelho(null, nome, tipo, watts)

            with(sharedPreferences.edit()) {
                putString("nome", nome)
                putString("tipo", tipo)
                putInt("watts", watts)
                apply()
            }

            aparelhoRepository.gravarAparelho(aparelho) { sucesso, mensagem ->
                runOnUiThread {
                    if (sucesso) {
                        Toast.makeText(this, "Aparelho gravado com sucesso!", Toast.LENGTH_SHORT).show()
                        edtNome.text.clear()
                        edtTipo.text.clear()
                        edtWatts.text.clear()

                        sharedPreferences.edit().clear().apply()
                    } else {
                        Toast.makeText(this, "Erro ao gravar aparelho: $mensagem", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
