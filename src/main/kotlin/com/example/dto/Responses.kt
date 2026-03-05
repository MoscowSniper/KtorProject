package com.example.dto

import com.example.models.OrderStatus
import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int
)

@Serializable
data class OrderItemResponse(
    val productId: Int,
    val quantity: Int,
    val unitPrice: Double
)

@Serializable
data class OrderResponse(
    val id: Int,
    val status: OrderStatus,
    val totalAmount: Double,
    val items: List<OrderItemResponse>
)

@Serializable
data class StatsResponse(
    val totalOrders: Long,
    val canceledOrders: Long,
    val revenue: Double
)

@Serializable
data class ErrorResponse(val message: String)
