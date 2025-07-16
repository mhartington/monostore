package com.monostore.ui.viewmodel

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
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow().also { flow ->
        viewModelScope.launch {
            flow.collect { state ->
                println("DEBUG: CartUiState updated - cart items: ${state.cart?.items?.size}, isLoading: ${state.isLoading}, error: ${state.error}")
            }
        }
    }

    // Exposes the total count of items in the cart (across all item types)
    val cartItemCount: StateFlow<Int> = uiState
        .map { state ->
            val count = state.cart?.items?.sumOf { it.quantity } ?: 0
            println("DEBUG: Cart item count updated to $count")
            count
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
        println("DEBUG: Updating item quantity - productId: $productId, quantity: $quantity")
        viewModelScope.launch {
            cartRepository.updateCartItem(productId, quantity).collect { result ->
                result.fold(
                    onSuccess = { cart ->
                        println("DEBUG: Cart update success - items count: ${cart.items.size}, total items: ${cart.items.sumOf { it.quantity }}")
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
