package com.monostore.data.repository

import com.monostore.data.model.Cart
import com.monostore.data.remote.AddToCartRequest
import com.monostore.data.remote.ApiService
import com.monostore.data.remote.UpdateCartItemRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val apiService: ApiService
) {
    
    fun getCart(): Flow<Result<Cart>> = flow {
        try {
            val response = apiService.getCart()
            emit(Result.success(response.cart))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun addToCart(productId: String, quantity: Int): Flow<Result<Cart>> = flow {
        try {
            val response = apiService.addToCart(
                AddToCartRequest(productId, quantity)
            )
            emit(Result.success(response.cart))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun updateCartItem(productId: String, quantity: Int): Flow<Result<Cart>> = flow {
        try {
            val response = apiService.updateCartItem(
                productId,
                UpdateCartItemRequest(quantity)
            )
            emit(Result.success(response.cart))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun removeFromCart(productId: String): Flow<Result<Cart>> = flow {
        try {
            val response = apiService.removeFromCart(productId)
            emit(Result.success(response.cart))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 
