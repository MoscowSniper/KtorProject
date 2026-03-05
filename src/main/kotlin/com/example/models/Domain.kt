package com.example.models

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole { USER, ADMIN }

@Serializable
enum class OrderStatus { CREATED, CANCELED }

@Serializable
data class JwtPrincipalData(val userId: Int, val role: UserRole)
