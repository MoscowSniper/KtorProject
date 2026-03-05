package com.example.db

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object Users : IntIdTable("users") {
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val role = varchar("role", 20).index()
    val createdAt = timestamp("created_at")
}

object Products : IntIdTable("products") {
    val name = varchar("name", 255).index()
    val description = text("description")
    val price = decimal("price", 10, 2)
    val stock = integer("stock")
    val updatedAt = timestamp("updated_at")
}

object Orders : IntIdTable("orders") {
    val userId = reference("user_id", Users).index()
    val status = varchar("status", 20).index()
    val totalAmount = decimal("total_amount", 10, 2)
    val createdAt = timestamp("created_at")
}

object OrderItems : IntIdTable("order_items") {
    val orderId = reference("order_id", Orders).index()
    val productId = reference("product_id", Products).index()
    val quantity = integer("quantity")
    val unitPrice = decimal("unit_price", 10, 2)
}

object AuditLogs : IntIdTable("audit_logs") {
    val userId = integer("user_id").nullable().index()
    val action = varchar("action", 100).index()
    val payload = text("payload")
    val createdAt = timestamp("created_at")
}
