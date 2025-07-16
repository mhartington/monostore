package com.monostore.data.model

data class Cart(
  val items: List<CartItem>,
  val total: Double
) 
