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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.monostore.ui.cart.CartViewModel

import com.monostore.R
import com.monostore.data.model.Cart
import com.monostore.data.model.CartItem
import com.monostore.ui.viewmodel.AuthViewModel

@Composable
fun CartScreen(
  onNavigateToCheckout: () -> Unit,
  onNavigateToLogin: () -> Unit,
  viewModel: CartViewModel = hiltViewModel(),
  authViewModel: AuthViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(uiState.error) {
    if (uiState.error != null) {
      viewModel.clearError()
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
  ) {
    Text(
      text = stringResource(R.string.shopping_cart),
      style = MaterialTheme.typography.headlineMedium,
      fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Check authentication
    when {
      authUiState.isLoading -> {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          CircularProgressIndicator()
        }
      }
      !authUiState.isAuthenticated -> {
        NotLoggedInSection(onNavigateToLogin)
      }
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
      uiState.cart == null || uiState.cart!!.items.isEmpty() -> {
        EmptyCartSection()
      }
      else -> {
        CartContent(
          cart = uiState.cart!!,
          onUpdateQuantity = viewModel::updateItemQuantity,
          onRemoveItem = viewModel::removeItem,
          onNavigateToCheckout = onNavigateToCheckout
        )
      }
    }
  }
}

@Composable
private fun NotLoggedInSection(onNavigateToLogin: () -> Unit) {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      text = "Please log in to view your cart.",
      style = MaterialTheme.typography.headlineSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(16.dp))
    Button(onClick = onNavigateToLogin) {
      Text("Login")
    }
  }
}

@Composable
private fun CartContent(
  cart: Cart,
  onUpdateQuantity: (String, Int) -> Unit,
  onRemoveItem: (String) -> Unit,
  onNavigateToCheckout: () -> Unit
) {
  Column(
    modifier = Modifier.fillMaxSize()
  ) {
    // Cart Items
    LazyColumn(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(cart.items) { item ->
        CartItemCard(
          item = item,
          onUpdateQuantity = onUpdateQuantity,
          onRemoveItem = onRemoveItem
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Total and Checkout
    Card(
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
      Column(
        modifier = Modifier.padding(16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = stringResource(R.string.total),
            style = MaterialTheme.typography.titleLarge
          )
          Text(
            text = "$${String.format("%.2f", cart.total)}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = onNavigateToCheckout,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(stringResource(R.string.proceed_to_checkout))
        }
      }
    }
  }
}

@Composable
private fun CartItemCard(
  item: CartItem,
  onUpdateQuantity: (String, Int) -> Unit,
  onRemoveItem: (String) -> Unit
) {
  Card(
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Product Image
      AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
          .data(item.product.image)
          .crossfade(true)
          .build(),
        contentDescription = item.product.name,
        modifier = Modifier
          .size(80.dp)
          .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
      )

      Spacer(modifier = Modifier.width(12.dp))

      // Product Info
      Column(
        modifier = Modifier.weight(1f)
      ) {
        Text(
          text = item.product.name,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "$${String.format("%.2f", item.product.price)}",
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "Subtotal: $${String.format("%.2f", item.subtotal)}",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      // Quantity Controls
      Column(
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = { onUpdateQuantity(item.product.id, item.quantity - 1) },
            enabled = item.quantity > 1
          ) {
            Icon(Icons.Default.Close, contentDescription = null)
          }

          Text(
            text = item.quantity.toString(),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 8.dp)
          )

          IconButton(
            onClick = { onUpdateQuantity(item.product.id, item.quantity + 1) }
          ) {
            Icon(Icons.Default.Add, contentDescription = null)
          }
        }

        IconButton(
          onClick = { onRemoveItem(item.product.id) }
        ) {
          Icon(
            Icons.Default.Delete,
            contentDescription = stringResource(R.string.remove_item),
            tint = MaterialTheme.colorScheme.error
          )
        }
      }
    }
  }
}

@Composable
private fun EmptyCartSection() {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      text = stringResource(R.string.cart_empty),
      style = MaterialTheme.typography.headlineSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(16.dp))

    Button(
      onClick = { /* This would navigate to product list in a real app */ }
    ) {
      Text(stringResource(R.string.continue_shopping))
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
