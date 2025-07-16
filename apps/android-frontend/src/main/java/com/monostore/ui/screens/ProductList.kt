package com.monostore.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
//import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.monostore.data.model.Product
import com.monostore.ui.viewmodel.ProductListViewModel
import com.monostore.R
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.ExperimentalMaterial3Api

@Composable
fun ProductList(
  onProductClick: (String) -> Unit,
  viewModel: ProductListViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
  Column(
    modifier = Modifier
      .fillMaxSize()
//      .padding(16.dp)
  ) {
    // Search and Filter Section
    SearchAndFilterSection(
      searchQuery = searchQuery,
      selectedCategory = selectedCategory,
      onSearchQueryChange = viewModel::setSearchQuery,
      onCategoryChange = viewModel::setCategory
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Product Grid
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
      uiState.filteredProducts.isEmpty() -> {
        EmptyState()
      }
      else -> {
        ProductGrid(
          products = uiState.filteredProducts,
          onProductClick = onProductClick
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchAndFilterSection(
    searchQuery: String,
    selectedCategory: String?,
    onSearchQueryChange: (String) -> Unit,
    onCategoryChange: (String?) -> Unit
) {
    var isSearchActive by remember { mutableStateOf(false) }

    Column {
        // Search Bar
        SearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            onSearch = { isSearchActive = false },
            active = isSearchActive,
            onActiveChange = { isSearchActive = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.search_products)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.clear_text)
                        )
                    }
                }
            }
        ) {
            // No suggestions needed; leave block empty
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Category Filter (unchanged)
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            OutlinedTextField(
                value = selectedCategory ?: stringResource(R.string.all_categories),
                onValueChange = { },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.all_categories)) },
                    onClick = {
                        onCategoryChange(null)
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.electronics)) },
                    onClick = {
                        onCategoryChange("electronics")
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.clothing)) },
                    onClick = {
                        onCategoryChange("clothing")
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.accessories)) },
                    onClick = {
                        onCategoryChange("accessories")
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ProductGrid(
  products: List<Product>,
  onProductClick: (String) -> Unit
) {
  LazyVerticalGrid(
    columns = GridCells.Adaptive(minSize = 160.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    items(products) { product ->     // Now this resolves properly.
      ProductCard(
        product = product,
        onClick = { onProductClick(product.id) }
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductCard(
  product: Product,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight()
      .clickable(onClick = onClick),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    Column {
      AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
          .data(product.image)
          .crossfade(true)
          .build(),
        contentDescription = product.name,
        modifier = Modifier
          .fillMaxWidth()
          .height(120.dp)
          .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
        contentScale = ContentScale.Crop
      )

      Column(
        modifier = Modifier.padding(12.dp)
      ) {
        Text(
          text = product.name,
          style = MaterialTheme.typography.titleMedium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis

        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "$${String.format("%.2f", product.price)}",
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = product.description.take(80) + if (product.description.length > 80) "..." else "",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )
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

@Composable
private fun EmptyState() {
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = stringResource(R.string.no_products),
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}
