package com.example.energymi_kotlin.activity

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.energymi_kotlin.R
import com.example.energymi_kotlin.model.Instalacao
import com.example.energymi_kotlin.repository.InstalacaoRepository

class InstalacaoCadastrarActivity : Activity(){
    private lateinit var instalacaoRepository: InstalacaoRepository
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.cadastrar_instalacao)

        sharedPreferences = getSharedPreferences("InstalacaoPrefs", Context.MODE_PRIVATE)

        val edtEstado = findViewById<EditText>(R.id.edtEstado)
        val edtCidade = findViewById<EditText>(R.id.edtCidade)
        val edtBairro = findViewById<EditText>(R.id.edtBairro)
        val edtRua = findViewById<EditText>(R.id.edtRua)
        val edtEndereco = findViewById<EditText>(R.id.edtEndereco)
        val edtObservacoes = findViewById<EditText>(R.id.edtObservacoes)
        val btnEnviar = findViewById<Button>(R.id.btnEnviar)

        edtEstado.setText(sharedPreferences.getString("estado", ""))
        edtCidade.setText(sharedPreferences.getString("cidade", ""))
        edtBairro.setText(sharedPreferences.getString("bairro", ""))
        edtRua.setText(sharedPreferences.getString("rua", ""))
        edtEndereco.setText(sharedPreferences.getInt("endereco", 0).toString())
        edtObservacoes.setText(sharedPreferences.getString("observacoes", ""))

        instalacaoRepository = InstalacaoRepository()

        btnEnviar.setOnClickListener {
            val estado = edtEstado.text.toString()
            val cidade = edtCidade.text.toString()
            val bairro = edtBairro.text.toString()
            val rua = edtRua.text.toString()
            val endereco = edtEndereco.text.toString().toIntOrNull() ?: 0
            val observacoes = edtObservacoes.text.toString()

            val instalacao = Instalacao(
                id = null,
                estado = estado,
                cidade = cidade,
                bairro = bairro,
                rua = rua,
                endereco = endereco,
                observacoes = observacoes
            )

            with(sharedPreferences.edit()) {
                putString("estado", estado)
                putString("cidade", cidade)
                putString("bairro", bairro)
                putString("rua", rua)
                putInt("endereco", endereco)
                putString("observacoes", observacoes)
                apply()
            }

            instalacaoRepository.gravarInstalacao(instalacao) { sucesso, mensagem ->
                runOnUiThread {
                    if (sucesso) {
                        Toast.makeText(this, "Instalação gravada com sucesso!", Toast.LENGTH_SHORT).show()

                        edtEstado.text.clear()
                        edtCidade.text.clear()
                        edtBairro.text.clear()
                        edtRua.text.clear()
                        edtEndereco.text.clear()
                        edtObservacoes.text.clear()

                        sharedPreferences.edit().clear().apply()
                    } else {
                        Toast.makeText(this, "Erro ao gravar instalação: $mensagem", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

}