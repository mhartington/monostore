package com.monostore.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monostore.ui.viewmodel.ProductDetailViewModel

import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.monostore.data.model.Product
import com.monostore.R

@Composable
fun ProductDetail(
  onNavigateToLogin: () -> Unit,
  viewModel: ProductDetailViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val quantity by viewModel.quantity.collectAsStateWithLifecycle()

  LaunchedEffect(uiState.addToCartSuccess) {
    if (uiState.addToCartSuccess) {
      viewModel.clearAddToCartSuccess()
    }
  }

  LaunchedEffect(uiState.error) {
    if (uiState.error != null) {
      viewModel.clearError()
    }
  }

  when {
    uiState.isLoading -> {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator()
      }
    }
    uiState.error != null -> {
      ErrorSection(
        error = uiState.error!!,
        onRetry = viewModel::retry
      )
    }
    uiState.product != null -> {
      ProductDetailContent(
        product = uiState.product!!,
        quantity = quantity,
        isAddingToCart = uiState.isAddingToCart,
        onQuantityChange = viewModel::setQuantity,
        onIncrementQuantity = viewModel::incrementQuantity,
        onDecrementQuantity = viewModel::decrementQuantity,
        onAddToCart = {
          // In a real app, you'd check authentication here
          viewModel.addToCart()
        }
      )
    }
  }
}

@Composable
private fun ProductDetailContent(
  product: Product,
  quantity: Int,
  isAddingToCart: Boolean,
  onQuantityChange: (Int) -> Unit,
  onIncrementQuantity: () -> Unit,
  onDecrementQuantity: () -> Unit,
  onAddToCart: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(16.dp)
  ) {
    // Product Image
    AsyncImage(
      model = ImageRequest.Builder(LocalContext.current)
        .data(product.image)
        .crossfade(true)
        .build(),
      contentDescription = product.name,
      modifier = Modifier
        .fillMaxWidth()
        .height(300.dp)
        .clip(RoundedCornerShape(12.dp)),
      contentScale = ContentScale.Crop
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Product Info
    Text(
      text = product.name,
      style = MaterialTheme.typography.headlineMedium
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = "$${String.format("%.2f", product.price)}",
      style = MaterialTheme.typography.headlineSmall,
      color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = product.description,
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Quantity Selector
    QuantitySelector(
      quantity = quantity,
      maxQuantity = product.stock,
      onQuantityChange = onQuantityChange,
      onIncrement = onIncrementQuantity,
      onDecrement = onDecrementQuantity
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Add to Cart Button
    Button(
      onClick = onAddToCart,
      modifier = Modifier.fillMaxWidth(),
      enabled = !isAddingToCart && product.stock > 0
    ) {
      if (isAddingToCart) {
        CircularProgressIndicator(
          modifier = Modifier.size(20.dp),
          color = MaterialTheme.colorScheme.onPrimary,
          strokeWidth = 2.dp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(stringResource(R.string.adding_to_cart))
      } else {
        Text(stringResource(R.string.add_to_cart))
      }
    }

    if (product.stock <= 0) {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = stringResource(R.string.out_of_stock),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.error,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}

@Composable
private fun QuantitySelector(
  quantity: Int,
  maxQuantity: Int,
  onQuantityChange: (Int) -> Unit,
  onIncrement: () -> Unit,
  onDecrement: () -> Unit
) {
  Column {
    Text(
      text = stringResource(R.string.quantity),
      style = MaterialTheme.typography.titleMedium
    )

    Spacer(modifier = Modifier.height(8.dp))

    Row(
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onDecrement,
        enabled = quantity > 1
      ) {
        Icon(Icons.Default.Close, contentDescription = null)

      }

      OutlinedTextField(
        value = quantity.toString(),
        onValueChange = { value ->
          value.toIntOrNull()?.let { onQuantityChange(it) }
        },
        modifier = Modifier.width(80.dp),
        textStyle = MaterialTheme.typography.bodyLarge,
        singleLine = true
      )

      IconButton(
        onClick = onIncrement,
        enabled = quantity < maxQuantity
      ) {
        Icon(Icons.Default.Add, contentDescription = null)
      }
    }
  }
}

@Composable
private fun ErrorSection(
  error: String,
  onRetry: () -> Unit
) {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      text = error,
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.error
    )

    Spacer(modifier = Modifier.height(16.dp))

    Button(onClick = onRetry) {
      Text(stringResource(R.string.retry))
    }
  }
}
