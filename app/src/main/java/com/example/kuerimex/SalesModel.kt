package com.example.kuerimex

data class Pedido(
    val productos: List<ProductoEnPedido>
)

data class PedidoResponse(
    val id: Int,
    val precio_total: Double,
    val estado: String,
    val fecha_creación: String
)