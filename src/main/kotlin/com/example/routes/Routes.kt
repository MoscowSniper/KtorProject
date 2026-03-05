package com.example.routes

import com.example.dto.AuthResponse
import com.example.dto.CreateOrderRequest
import com.example.dto.ProductRequest
import com.example.dto.StatsResponse
import com.example.models.UserRole
import com.example.services.AccessDeniedException
import com.example.services.AuthService
import com.example.services.OrderService
import com.example.services.ProductService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.authRoutes(authService: AuthService) {
    route("/auth") {
        post("/register") {
            call.respond(AuthResponse(authService.register(call.receive())))
        }
        post("/login") {
            call.respond(AuthResponse(authService.login(call.receive())))
        }
    }
}

fun Route.userRoutes(productService: ProductService, orderService: OrderService) {
    get("/products") { call.respond(productService.list()) }
    get("/products/{id}") { call.respond(productService.getById(call.parameters["id"]!!.toInt())) }

    authenticate("auth-jwt") {
        post("/orders") {
            call.respond(HttpStatusCode.Created, orderService.createOrder(call.userId(), call.receive<CreateOrderRequest>()))
        }
        get("/orders") { call.respond(orderService.listOrders(call.userId())) }
        delete("/orders/{id}") {
            orderService.cancelOrder(call.userId(), call.parameters["id"]!!.toInt())
            call.respond(HttpStatusCode.NoContent)
        }
    }
}

fun Route.adminRoutes(productService: ProductService, orderService: OrderService) {
    authenticate("auth-jwt") {
        post("/products") {
            call.requireAdmin()
            call.respond(HttpStatusCode.Created, productService.create(call.receive<ProductRequest>()))
        }
        put("/products/{id}") {
            call.requireAdmin()
            call.respond(productService.update(call.parameters["id"]!!.toInt(), call.receive<ProductRequest>()))
        }
        delete("/products/{id}") {
            call.requireAdmin()
            productService.delete(call.parameters["id"]!!.toInt())
            call.respond(HttpStatusCode.NoContent)
        }
        get("/stats/orders") {
            call.requireAdmin()
            val (total, canceled, revenue) = orderService.stats()
            call.respond(StatsResponse(total, canceled, revenue))
        }
    }
}

private fun ApplicationCall.userId(): Int =
    principal<JWTPrincipal>()!!.payload.getClaim("userId").asInt()

private fun ApplicationCall.requireAdmin() {
    val role = principal<JWTPrincipal>()!!.payload.getClaim("role").asString()
    if (role != UserRole.ADMIN.name) throw AccessDeniedException("Admin required")
}
