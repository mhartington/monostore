package com.monostore.data.model

import com.google.gson.annotations.SerializedName

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val image: String,
    val category: String,
    val stock: Int,
    @SerializedName("created_at")
    val createdAt: String
) 
