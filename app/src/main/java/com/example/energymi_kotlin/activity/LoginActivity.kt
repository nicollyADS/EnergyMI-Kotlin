package com.example.energymi_kotlin.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.energymi_kotlin.R
import com.example.energymi_kotlin.model.Login
import com.example.energymi_kotlin.repository.LoginRepository

class LoginActivity : Activity() {

    private lateinit var loginRepository: LoginRepository
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.login)

        loginRepository = LoginRepository()
        sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)

        val edtLogin = findViewById<EditText>(R.id.edtLogin)
        val edtSenha = findViewById<EditText>(R.id.edtSenha)
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)

        edtLogin.setText(sharedPreferences.getString("email", ""))

        btnEntrar.setOnClickListener {
            val email = edtLogin.text.toString().trim()
            val senha = edtSenha.text.toString().trim()

            if (email.isNotEmpty() && senha.isNotEmpty()) {
                val login = Login(null, email, senha)
                loginRepository.fazerLogin(login) { sucesso, erro ->
                    runOnUiThread {
                        if (sucesso) {
                            Toast.makeText(this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show()
                            salvarEmailNoSharedPreferences(email)
                            startActivity(Intent(this, HomeActivity::class.java))
                        } else {
                            Toast.makeText(this, "Falha no login: $erro", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun salvarEmailNoSharedPreferences(email: String) {
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.apply()
    }
}

