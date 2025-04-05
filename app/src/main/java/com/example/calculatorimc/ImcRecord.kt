package com.example.calculatorimc

data class ImcRecord(
    val date: String = "",
    val weight: Float = 0f,
    val height: Float = 0f,
    val imc: Float = 0f,
    val category: String = ""
)