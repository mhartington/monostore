package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.User
import com.example.myapplication.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    init {
        checkAuthStatus()
    }
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            authRepository.login(email, password).collect { result ->
                result.fold(
                    onSuccess = { user ->
                        _uiState.value = _uiState.value.copy(
                            user = user,
                            isAuthenticated = true,
                            isLoading = false,
                            error = null
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message ?: "Login failed"
                        )
                    }
                )
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            authRepository.logout().collect { result ->
                result.fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            user = null,
                            isAuthenticated = false,
                            isLoading = false
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message ?: "Logout failed"
                        )
                    }
                )
            }
        }
    }
    
    fun checkAuthStatus() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            authRepository.getProfile().collect { result ->
                result.fold(
                    onSuccess = { user ->
                        _uiState.value = _uiState.value.copy(
                            user = user,
                            isAuthenticated = true,
                            isLoading = false
                        )
                    },
                    onFailure = {
                        _uiState.value = _uiState.value.copy(
                            user = null,
                            isAuthenticated = false,
                            isLoading = false
                        )
                    }
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class AuthUiState(
  val user: User? = null,
  val isAuthenticated: Boolean = false,
  val isLoading: Boolean = false,
  val error: String? = null
)
