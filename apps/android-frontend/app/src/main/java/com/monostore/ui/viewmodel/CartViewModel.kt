package com.monostore.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monostore.data.model.Cart
import com.monostore.data.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartUiState(
    val cart: Cart? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    // Exposes the total count of items in the cart (across all item types)
    val cartItemCount: StateFlow<Int> = uiState
        .map { state ->
            state.cart?.items?.sumOf { it.quantity } ?: 0
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        loadCart()
    }

    fun loadCart() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            cartRepository.getCart().collect { result ->
                result.fold(
                    onSuccess = { cart ->
                        _uiState.value = _uiState.value.copy(
                            cart = cart,
                            isLoading = false,
                            error = null
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load cart"
                        )
                    }
                )
            }
        }
    }

    fun updateItemQuantity(productId: String, quantity: Int) {
        viewModelScope.launch {
            cartRepository.updateCartItem(productId, quantity).collect { result ->
                result.fold(
                    onSuccess = { cart ->
                        _uiState.value = _uiState.value.copy(cart = cart)
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            error = error.message ?: "Failed to update item"
                        )
                    }
                )
            }
        }
    }

    fun removeItem(productId: String) {
        viewModelScope.launch {
            cartRepository.removeFromCart(productId).collect { result ->
                result.fold(
                    onSuccess = { cart ->
                        _uiState.value = _uiState.value.copy(cart = cart)
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            error = error.message ?: "Failed to remove item"
                        )
                    }
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun retry() {
        loadCart()
    }
}
