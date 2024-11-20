package com.example.energymi_kotlin.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.energymi_kotlin.R

class HomeActivity : Activity(){

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.home)

        val btnAparelhos = findViewById<Button>(R.id.txtAparelhos)
        val btnInstalacoes = findViewById<Button>(R.id.txtInstalacoes)
        val btnConsumos = findViewById<Button>(R.id.txtConsumos)
        val btnCadInstalacao = findViewById<TextView>(R.id.txtCadInstalacao)
        val btnCadAparelho = findViewById<TextView>(R.id.txtCadAparelho)

        btnAparelhos.setOnClickListener {
            val intent = Intent(this, AparelhoActivity::class.java)
            startActivity(intent)
        }

        btnInstalacoes.setOnClickListener {
            val intent = Intent(this, InstalacaoActivity::class.java)
            startActivity(intent)
        }

        btnConsumos.setOnClickListener {
            val intent = Intent(this, ConsumoActivity::class.java)
            startActivity(intent)
        }

        btnCadAparelho.setOnClickListener {
            val intent = Intent(this, AparelhoCadastrarActivity::class.java)
            startActivity(intent)
        }

        btnCadInstalacao.setOnClickListener {
            val intent = Intent(this, InstalacaoCadastrarActivity::class.java)
            startActivity(intent)
        }
    }
}