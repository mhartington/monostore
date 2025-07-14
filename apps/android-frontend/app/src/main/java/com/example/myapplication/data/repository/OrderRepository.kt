package com.example.myapplication.data.repository

import com.example.myapplication.data.model.ShippingAddress
import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.data.remote.CheckoutRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val apiService: ApiService
) {
    
    fun checkout(shippingAddress: ShippingAddress): Flow<Result<String>> = flow {
        try {
            val response = apiService.checkout(
              CheckoutRequest(shippingAddress)
            )
            emit(Result.success(response.orderId))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 
