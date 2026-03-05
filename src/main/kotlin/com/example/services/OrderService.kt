package com.example.services

import com.example.cache.OrderCache
import com.example.db.AuditLogs
import com.example.db.OrderItems
import com.example.db.Orders
import com.example.db.Products
import com.example.dto.CreateOrderRequest
import com.example.dto.OrderItemResponse
import com.example.dto.OrderResponse
import com.example.models.OrderStatus
import com.example.queue.OrderEventPublisher
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.time.Instant

class OrderService(
    private val cache: OrderCache,
    private val publisher: OrderEventPublisher
) {
    fun createOrder(userId: Int, req: CreateOrderRequest): OrderResponse = transaction {
        if (req.items.isEmpty()) throw BusinessException("Order items cannot be empty")
        var total = BigDecimal.ZERO
        val orderItems = req.items.map { item ->
            val product = Products.select { Products.id eq item.productId }.singleOrNull()
                ?: throw NotFoundException("Product ${item.productId} not found")
            if (product[Products.stock] < item.quantity) {
                throw BusinessException("Not enough stock for product ${item.productId}")
            }
            Products.update({ Products.id eq item.productId }) {
                it[stock] = product[Products.stock] - item.quantity
            }
            val unitPrice = product[Products.price]
            total += unitPrice * item.quantity.toBigDecimal()
            Triple(item.productId, item.quantity, unitPrice)
        }

        val orderId = Orders.insert {
            it[Orders.userId] = userId
            it[status] = OrderStatus.CREATED.name
            it[totalAmount] = total
            it[createdAt] = Instant.now()
        } get Orders.id

        orderItems.forEach { (productId, quantity, unitPrice) ->
            OrderItems.insert {
                it[orderId] = orderId
                it[OrderItems.productId] = productId
                it[OrderItems.quantity] = quantity
                it[OrderItems.unitPrice] = unitPrice
            }
        }

        AuditLogs.insert {
            it[AuditLogs.userId] = userId
            it[action] = "ORDER_CREATED"
            it[payload] = "orderId=${orderId.value}"
            it[createdAt] = Instant.now()
        }

        val response = getOrderInternal(orderId.value, userId)
        cache.put(orderId.value, Json.encodeToString(response))
        publisher.publish("ORDER_CREATED:${orderId.value}:$userId")
        response
    }

    fun listOrders(userId: Int): List<OrderResponse> = transaction {
        Orders.select { Orders.userId eq userId }.map { getOrderInternal(it[Orders.id].value, userId) }
    }

    fun cancelOrder(userId: Int, orderId: Int) = transaction {
        val order = Orders.select { Orders.id eq orderId }.singleOrNull() ?: throw NotFoundException("Order not found")
        if (order[Orders.userId].value != userId) throw AccessDeniedException("No access to this order")
        if (order[Orders.status] == OrderStatus.CANCELED.name) return@transaction

        Orders.update({ Orders.id eq orderId }) { it[status] = OrderStatus.CANCELED.name }
        AuditLogs.insert {
            it[AuditLogs.userId] = userId
            it[action] = "ORDER_CANCELED"
            it[payload] = "orderId=$orderId"
            it[createdAt] = Instant.now()
        }
        publisher.publish("ORDER_CANCELED:$orderId:$userId")
    }

    fun stats(): Triple<Long, Long, Double> = transaction {
        val total = Orders.selectAll().count()
        val canceled = Orders.select { Orders.status eq OrderStatus.CANCELED.name }.count()
        val revenue = Orders.slice(Orders.totalAmount).selectAll().sumOf { it[Orders.totalAmount].toDouble() }
        Triple(total, canceled, revenue)
    }

    private fun getOrderInternal(orderId: Int, userId: Int): OrderResponse {
        val order = Orders.select { (Orders.id eq orderId) and (Orders.userId eq userId) }.singleOrNull()
            ?: throw NotFoundException("Order not found")
        val items = OrderItems.select { OrderItems.orderId eq orderId }.map {
            OrderItemResponse(it[OrderItems.productId].value, it[OrderItems.quantity], it[OrderItems.unitPrice].toDouble())
        }
        return OrderResponse(
            id = orderId,
            status = OrderStatus.valueOf(order[Orders.status]),
            totalAmount = order[Orders.totalAmount].toDouble(),
            items = items
        )
    }
}
