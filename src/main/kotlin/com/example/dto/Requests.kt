package com.example.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(val email: String, val password: String)

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class AuthResponse(val token: String)

@Serializable
data class ProductRequest(
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int
)

@Serializable
data class OrderItemRequest(val productId: Int, val quantity: Int)

@Serializable
data class CreateOrderRequest(val items: List<OrderItemRequest>)
