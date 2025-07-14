package com.example.myapplication.data.remote

import com.example.myapplication.data.model.Cart
import com.example.myapplication.data.model.Product
import com.example.myapplication.data.model.ShippingAddress
import com.example.myapplication.data.model.User
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

  @GET("products")
  suspend fun getProducts(@Query("category") category: String? = null): ProductsResponse

  @GET("products/{id}")
  suspend fun getProduct(@Path("id") id: String): ProductResponse

  @GET("cart")
  suspend fun getCart(): CartResponse

  @POST("cart/items")
  suspend fun addToCart(@Body request: AddToCartRequest): CartResponse

  @PUT("cart/items/{productId}")
  suspend fun updateCartItem(
    @Path("productId") productId: String,
    @Body request: UpdateCartItemRequest
  ): CartResponse

  @DELETE("cart/items/{productId}")
  suspend fun removeFromCart(@Path("productId") productId: String): CartResponse

  @POST("users/login")
  suspend fun login(@Body request: LoginRequest): LoginResponse

  @POST("users/logout")
  suspend fun logout(): LogoutResponse

  @GET("users/profile")
  suspend fun getProfile(): ProfileResponse

  @POST("orders")
  suspend fun checkout(@Body request: CheckoutRequest): CheckoutResponse
}

// Request/Response models
data class ProductsResponse(val products: List<Product>)
data class ProductResponse(val product: Product)
data class CartResponse(val cart: Cart)
data class AddToCartRequest(val productId: String, val quantity: Int)
data class UpdateCartItemRequest(val quantity: Int)
data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val user: User)
data class LogoutResponse(val message: String)
data class ProfileResponse(val user: User)
data class CheckoutRequest(
  val shippingAddress: ShippingAddress,
  val paymentMethod: String = "credit_card"
)
data class CheckoutResponse(val orderId: String, val message: String)
