package com.example.myapplication.data.repository

import com.example.myapplication.data.model.Product
import com.example.myapplication.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val apiService: ApiService
) {
    
    fun getProducts(category: String? = null): Flow<Result<List<Product>>> = flow {
        try {
            val response = apiService.getProducts(category)
            emit(Result.success(response.products))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun getProduct(id: String): Flow<Result<Product>> = flow {
        try {
            val response = apiService.getProduct(id)
            emit(Result.success(response.product))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 
