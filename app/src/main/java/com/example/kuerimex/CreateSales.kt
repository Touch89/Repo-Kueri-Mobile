package com.example.kuerimex

data class SalesRequest(
    val items: List<CartItem>,
    val total: Double
)

data class CartItem(
    val product: Product,
    var quantity: Int
)
