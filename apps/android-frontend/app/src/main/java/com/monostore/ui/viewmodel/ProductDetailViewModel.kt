package com.monostore.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monostore.data.model.Product
import com.monostore.data.repository.CartRepository
import com.monostore.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val productId: String = checkNotNull(savedStateHandle["id"])
    
    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()
    
    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity.asStateFlow()
    
    init {
        loadProduct()
    }
    
    fun setQuantity(newQuantity: Int) {
        val product = _uiState.value.product
        if (product != null) {
            _quantity.value = newQuantity.coerceIn(1, product.stock)
        }
    }
    
    fun incrementQuantity() {
        val product = _uiState.value.product
        if (product != null) {
            setQuantity(_quantity.value + 1)
        }
    }
    
    fun decrementQuantity() {
        setQuantity(_quantity.value - 1)
    }
    
    fun addToCart() {
        val product = _uiState.value.product ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAddingToCart = true)
            
            cartRepository.addToCart(product.id, _quantity.value).collect { result ->
                result.fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            isAddingToCart = false,
                            addToCartSuccess = true
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isAddingToCart = false,
                            error = error.message ?: "Failed to add to cart"
                        )
                    }
                )
            }
        }
    }
    
    fun clearAddToCartSuccess() {
        _uiState.value = _uiState.value.copy(addToCartSuccess = false)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    private fun loadProduct() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            productRepository.getProduct(productId).collect { result ->
                result.fold(
                    onSuccess = { product ->
                        _uiState.value = _uiState.value.copy(
                            product = product,
                            isLoading = false,
                            error = null
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load product"
                        )
                    }
                )
            }
        }
    }
    
    fun retry() {
        loadProduct()
    }
}

data class ProductDetailUiState(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val isAddingToCart: Boolean = false,
    val addToCartSuccess: Boolean = false,
    val error: String? = null
) 
