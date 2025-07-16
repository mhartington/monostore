package com.monostore.data.repository

import com.monostore.data.model.User
import com.monostore.data.remote.ApiService
import com.monostore.data.remote.LoginRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService
) {
    
    fun login(email: String, password: String): Flow<Result<User>> = flow {
        try {
            val response = apiService.login(
                LoginRequest(email, password)
            )
            emit(Result.success(response.user))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun logout(): Flow<Result<String>> = flow {
        try {
            val response = apiService.logout()
            emit(Result.success(response.message))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun getProfile(): Flow<Result<User>> = flow {
        try {
            val response = apiService.getProfile()
            emit(Result.success(response.user))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 
