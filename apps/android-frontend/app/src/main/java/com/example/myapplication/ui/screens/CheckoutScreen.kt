package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.R
import com.example.myapplication.data.model.ShippingAddress
import com.example.myapplication.ui.viewmodel.CheckoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
  onOrderSuccess: () -> Unit,
  viewModel: CheckoutViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val shippingAddress by viewModel.shippingAddress.collectAsStateWithLifecycle()

  LaunchedEffect(uiState.orderSuccess) {
    if (uiState.orderSuccess) {
      onOrderSuccess()
      viewModel.clearOrderSuccess()
    }
  }

  LaunchedEffect(uiState.error) {
    if (uiState.error != null) {
      viewModel.clearError()
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
      .verticalScroll(rememberScrollState())
  ) {
    Text(
      text = stringResource(R.string.checkout),
      style = MaterialTheme.typography.headlineMedium,
      fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(24.dp))

    Text(
      text = stringResource(R.string.shipping_address),
      style = MaterialTheme.typography.titleLarge,
      fontWeight = FontWeight.Medium
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Shipping Address Form
    ShippingAddressForm(
      address = shippingAddress,
      onStreetChange = viewModel::updateStreet,
      onCityChange = viewModel::updateCity,
      onStateChange = viewModel::updateState,
      onCountryChange = viewModel::updateCountry,
      onZipChange = viewModel::updateZip
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Place Order Button
    Button(
      onClick = viewModel::placeOrder,
      modifier = Modifier.fillMaxWidth(),
      enabled = !uiState.isProcessing
    ) {
      if (uiState.isProcessing) {
        CircularProgressIndicator(
          modifier = Modifier.size(20.dp),
          color = MaterialTheme.colorScheme.onPrimary,
          strokeWidth = 2.dp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(stringResource(R.string.processing))
      } else {
        Text(stringResource(R.string.place_order))
      }
    }

    if (uiState.error != null) {
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = uiState.error!!,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.error
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShippingAddressForm(
  address: ShippingAddress,
  onStreetChange: (String) -> Unit,
  onCityChange: (String) -> Unit,
  onStateChange: (String) -> Unit,
  onCountryChange: (String) -> Unit,
  onZipChange: (String) -> Unit
) {
  Column(
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Street Address
    OutlinedTextField(
      value = address.street,
      onValueChange = onStreetChange,
      label = { Text(stringResource(R.string.street_address)) },
      modifier = Modifier.fillMaxWidth(),
      singleLine = true
    )

    // City and State Row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      OutlinedTextField(
        value = address.city,
        onValueChange = onCityChange,
        label = { Text(stringResource(R.string.city)) },
        modifier = Modifier.weight(1f),
        singleLine = true
      )

      OutlinedTextField(
        value = address.state,
        onValueChange = onStateChange,
        label = { Text(stringResource(R.string.state)) },
        modifier = Modifier.weight(1f),
        singleLine = true
      )
    }

    // Country and ZIP Row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      OutlinedTextField(
        value = address.country,
        onValueChange = onCountryChange,
        label = { Text(stringResource(R.string.country)) },
        modifier = Modifier.weight(1f),
        singleLine = true
      )

      OutlinedTextField(
        value = address.zip,
        onValueChange = onZipChange,
        label = { Text(stringResource(R.string.zip_code)) },
        modifier = Modifier.weight(1f),
        singleLine = true
      )
    }
  }
}
