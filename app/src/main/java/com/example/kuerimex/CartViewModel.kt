package com.example.kuerimex

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CartViewModel : ViewModel() {
    // Lista del carrito
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())

    // Lista pública para manipular
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    // Añadir al carrito
    fun addToCart(product: Product) {
        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst() { it.product.id == product.id }

        if (index != -1) {
            val existingItem = currentList[index]
            currentList[index] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            currentList.add(CartItem(product, 1))
        }

        _cartItems.value = currentList
    }

    // calcular total
    fun calculateTotal(): Double {
        return _cartItems.value.sumOf { (it.product.precio * it.quantity).toDouble() }
    }

    // aumentar cantidad de producto
    fun increaseQuantity(productId: Int) {
        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.id == productId }
        if (index != -1) {
            val item = currentList[index]
            currentList[index] = item.copy(quantity = item.quantity + 1)
            _cartItems.value = currentList
        }
    }

    // disminuir cantidad de producto
    fun decreaseQuantity(productId: Int) {
        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.id == productId }

        if (index != -1) {
            val item = currentList[index]
            if (item.quantity > 1) {
                currentList[index] = item.copy(quantity = item.quantity - 1)
                _cartItems.value = currentList
            }
        } else {
            // se elimina el producto del carrito con confirmación
        }
    }

    // remover productos del carrito
    fun removeItem(productId: Int) {
        val currentList = _cartItems.value.filter { it.product.id != productId }
        _cartItems.value = currentList
    }


    // limpiar carrito
    fun clearCart() {
        _cartItems.value = emptyList()
    }

}