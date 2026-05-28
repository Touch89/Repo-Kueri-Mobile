package com.example.kuerimex

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SalesViewModel : ViewModel() {
    // Lista del carrito
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())

    // Lista pública para manipular
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    // Añadir al carrito
    fun addToCart(product: Product) {
        val currentList = _cartItems.value.toMutableList()
        val existingItem = currentList.find { it.product.id == product.id }

        if (existingItem != null) {
            existingItem.quantity++
        } else {
            currentList.add(CartItem(product, 1))
        }

        _cartItems.value = currentList
    }

    // calcular total
    fun calculateTotal(): Double {
        return _cartItems.value.sumOf { it.product.precio * it.quantity }
    }

    // limpiar carrito
    fun clearCart() {
        _cartItems.value = emptyList()
    }

}