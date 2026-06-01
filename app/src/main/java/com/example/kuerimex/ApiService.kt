package com.example.kuerimex

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("productos/")
    suspend fun crearProducto(
        @Body producto: ProductCreate
    ): Response<Product>

    @GET("productos/")
    suspend fun obtenerProductos(): Response<List<Product>>

    @GET("productos/{id_producto}")
    suspend fun obtenerProducto(
        @Path("id_producto") idProducto: Int
    ): Response<Product>

    @POST("pedidos/fisico")
    suspend fun crearPedidoFisico(
        @Body pedido: Pedido
    ): Response<PedidoResponse>
}