package com.example.energymi_kotlin.model

data class Consumo(
    var id: Long? = null,
    var data: String = "",
    var numero: Int = 0,
    var custo: Int = 0,
    var observacoes: String = ""
)