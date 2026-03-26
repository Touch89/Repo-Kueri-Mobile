package com.example.kuerimex

data class ProductRequest(
    val nombre: String,
    val descripcion: String,
    val imagen: String,
    val precio: Float,
    val sku: String
)