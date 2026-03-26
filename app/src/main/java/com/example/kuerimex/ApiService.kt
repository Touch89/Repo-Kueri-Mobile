package com.example.kuerimex

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("productos/")
    suspend fun crearProducto(
        @Body producto: ProductRequest
    ): Response<ProductRequest>
}