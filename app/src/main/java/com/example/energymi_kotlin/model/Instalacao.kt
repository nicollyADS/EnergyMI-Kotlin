package com.example.energymi_kotlin.model

data class Instalacao(
    var id: Long? = null,
    var estado: String = "",
    var cidade: String = "",
    var bairro: String = "",
    var rua: String = "",
    var endereco: Int = 0,
    var observacoes: String = ""
)