package com.example.kuerimex

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val imagen_url: String,
    val precio: Float,
    val sku: String,
    val stock: Int,
    val categoria: String
) : Parcelable

@Parcelize
data class ProductCreate(
    val nombre: String,
    val descripcion: String,
    val imagen_url: String,
    val precio: Float,
    val sku: String,
    val stock: Int,
    val categoria: String
) : Parcelable

data class ProductoEnPedido(
    val id_producto: Int,
    val cantidad: Int
)