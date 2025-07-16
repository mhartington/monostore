package com.monostore.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monostore.data.model.Product
import com.monostore.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()
    
    init {
        loadProducts()
    }
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        filterProducts()
    }
    
    fun setCategory(category: String?) {
        _selectedCategory.value = category
        loadProducts()
    }
    
    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            productRepository.getProducts(_selectedCategory.value).collect { result ->
                result.fold(
                    onSuccess = { products ->
                        _uiState.value = _uiState.value.copy(
                            products = products,
                            isLoading = false,
                            error = null
                        )
                        filterProducts()
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load products"
                        )
                    }
                )
            }
        }
    }
    
    private fun filterProducts() {
        val query = _searchQuery.value.lowercase()
        val filteredProducts = if (query.isEmpty()) {
            _uiState.value.products
        } else {
            _uiState.value.products.filter { product ->
                product.name.lowercase().contains(query)
            }
        }
        
        _uiState.value = _uiState.value.copy(filteredProducts = filteredProducts)
    }
    
    fun retry() {
        loadProducts()
    }
}

data class ProductListUiState(
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) 
