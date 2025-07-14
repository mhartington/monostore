package com.example.myapplication.ui.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search

import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.myapplication.ui.screens.ProductList

import com.example.myapplication.R
import com.example.myapplication.ui.screens.CartScreen
import com.example.myapplication.ui.screens.CheckoutScreen
import com.example.myapplication.ui.screens.LoginScreen
import com.example.myapplication.ui.screens.ProductDetail

sealed class Screen(val route: String, val title: String) {
  object ProductList : Screen("product_list", "Products")
  object ProductDetail : Screen("product_detail/{id}", "Product Detail")
  object Cart : Screen("cart", "Cart")
  object Checkout : Screen("checkout", "Checkout")
  object Login : Screen("login", "Login")
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
  NavigationBar {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBarItem(
      icon = { Icon(Icons.Default.Home, contentDescription = null) },
      label = { Text(stringResource(R.string.nav_home)) },
      selected = currentRoute == Screen.ProductList.route,
      onClick = {
        navController.navigate(Screen.ProductList.route) {
          popUpTo(navController.graph.startDestinationId) {
            saveState = true
          }
          launchSingleTop = true
          restoreState = true
        }
      })

    NavigationBarItem(
      icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
      label = { Text(stringResource(R.string.nav_cart)) },
      selected = currentRoute == Screen.Cart.route,
      onClick = {
        navController.navigate(Screen.Cart.route) {
          popUpTo(navController.graph.startDestinationId) {
            saveState = true
          }
          launchSingleTop = true
          restoreState = true
        }
      })

    NavigationBarItem(
      icon = { Icon(Icons.Default.Person, contentDescription = null) },
      label = { Text(stringResource(R.string.nav_profile)) },
      selected = currentRoute == Screen.Login.route,
      onClick = {
        navController.navigate(Screen.Login.route) {
          popUpTo(navController.graph.startDestinationId) {
            saveState = true
          }
          launchSingleTop = true
          restoreState = true
        }
      })
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBacreen(navController: NavHostController) {
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route
  TopAppBar(title = { Text(stringResource(R.string.app_name)) }, navigationIcon = {
    if (currentRoute != Screen.ProductList.route) {
      IconButton(onClick = { navController.popBackStack() }) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
      }
    }
  })
}

@Composable
fun SettingsScreen() {
  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Text("Settings Screen")
  }
}

@Composable
fun MonoStoreApp() {
  val navController = rememberNavController()
  Scaffold(
    topBar = { TopAppBacreen(navController = navController) },
    bottomBar = { BottomNavigationBar(navController = navController) }) { padding ->
    NavHost(
      navController = navController, startDestination = Screen.ProductList.route, modifier = Modifier.padding(padding)
    ) {
      composable(Screen.ProductList.route) {
        ProductList(
          onProductClick = { productId ->
            println("Clicked on product with id: $productId")
            navController.navigate("product_detail/$productId")
          })
      }

      composable(
        route = Screen.ProductDetail.route, arguments = listOf(
        androidx.navigation.NavType.StringType.let {
          androidx.navigation.navArgument("id") { type = it }
        })) {
        ProductDetail(
          onNavigateToLogin = {
            navController.navigate(Screen.Login.route)
          })
      }


      composable(Screen.Cart.route) {
        CartScreen(onNavigateToCheckout = {
          navController.navigate(Screen.Checkout.route)
        }, onNavigateToLogin = {
          navController.navigate(Screen.Login.route)
        })
      }

      composable(Screen.Checkout.route) {
        CheckoutScreen(
          onOrderSuccess = {
            navController.navigate(Screen.ProductList.route) {
              popUpTo(Screen.ProductList.route) { inclusive = true }
            }
          })
      }
      composable(Screen.Login.route) {
        LoginScreen(
          onLoginSuccess = {
            navController.navigate(Screen.ProductList.route) {
              popUpTo(Screen.Login.route) { inclusive = true }
            }
          })
      }
    }
  }
}
