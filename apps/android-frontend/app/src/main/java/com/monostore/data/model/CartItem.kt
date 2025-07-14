package com.monostore.data.model

data class CartItem(
    val product: CartProduct,
    val quantity: Int,
    val subtotal: Double
)

data class CartProduct(
    val id: String,
    val name: String,
    val price: Double,
    val image: String
) 
