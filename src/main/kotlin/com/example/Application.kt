package com.example

import com.example.cache.OrderCache
import com.example.config.AppConfig
import com.example.db.DatabaseFactory
import com.example.plugins.*
import com.example.queue.OrderEventPublisher
import com.example.queue.OrderEventWorker
import com.example.routes.adminRoutes
import com.example.routes.authRoutes
import com.example.routes.userRoutes
import com.example.services.AuthService
import com.example.services.OrderService
import com.example.services.ProductService
import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import io.ktor.server.routing.route
import io.ktor.server.plugins.swagger.swaggerUI

fun Application.module() {
    val config = AppConfig()
    DatabaseFactory.init(config)

    val authService = AuthService(config)
    val productService = ProductService()
    val orderService = OrderService(
        cache = OrderCache(config.redisHost, config.redisPort),
        publisher = OrderEventPublisher(config.rabbitHost, config.rabbitPort)
    )
    OrderEventWorker(config.rabbitHost, config.rabbitPort).start()

    configureSerialization()
    configureMonitoring()
    configureStatusPages()
    configureSecurity(config)

    routing {
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
        route("") {
            authRoutes(authService)
            userRoutes(productService, orderService)
            adminRoutes(productService, orderService)
        }
    }
}
