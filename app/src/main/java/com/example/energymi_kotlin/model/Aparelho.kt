package com.example.energymi_kotlin.model

data class Aparelho(
    var id: Long? = null,
    var nome: String = "",
    var tipo: String = "",
    var watts: Int = 0
)