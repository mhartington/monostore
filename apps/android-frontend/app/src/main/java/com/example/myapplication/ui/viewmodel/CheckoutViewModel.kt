package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.ShippingAddress
import com.example.myapplication.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()
    
    private val _shippingAddress = MutableStateFlow(ShippingAddress("", "", "", "", ""))
    val shippingAddress: StateFlow<ShippingAddress> = _shippingAddress.asStateFlow()
    
    fun updateShippingAddress(address: ShippingAddress) {
        _shippingAddress.value = address
    }
    
    fun updateStreet(street: String) {
        _shippingAddress.value = _shippingAddress.value.copy(street = street)
    }
    
    fun updateCity(city: String) {
        _shippingAddress.value = _shippingAddress.value.copy(city = city)
    }
    
    fun updateState(state: String) {
        _shippingAddress.value = _shippingAddress.value.copy(state = state)
    }
    
    fun updateCountry(country: String) {
        _shippingAddress.value = _shippingAddress.value.copy(country = country)
    }
    
    fun updateZip(zip: String) {
        _shippingAddress.value = _shippingAddress.value.copy(zip = zip)
    }
    
    fun placeOrder() {
        val address = _shippingAddress.value
        
        // Validate address
        if (address.street.isBlank() || address.city.isBlank() || 
            address.state.isBlank() || address.country.isBlank() || address.zip.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Please fill in all address fields"
            )
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true, error = null)
            
            orderRepository.checkout(address).collect { result ->
                result.fold(
                    onSuccess = { orderId ->
                        _uiState.value = _uiState.value.copy(
                            isProcessing = false,
                            orderSuccess = true,
                            orderId = orderId
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isProcessing = false,
                            error = error.message ?: "Failed to place order"
                        )
                    }
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun clearOrderSuccess() {
        _uiState.value = _uiState.value.copy(orderSuccess = false, orderId = null)
    }
}

data class CheckoutUiState(
    val isProcessing: Boolean = false,
    val orderSuccess: Boolean = false,
    val orderId: String? = null,
    val error: String? = null
) 
