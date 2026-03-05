package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.config.AppConfig
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt

fun Application.configureSecurity(config: AppConfig) {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = config.jwtRealm
            verifier(
                JWT.require(Algorithm.HMAC256(config.jwtSecret))
                    .withAudience(config.jwtAudience)
                    .withIssuer(config.jwtIssuer)
                    .build()
            )
            validate { credential ->
                credential.payload.getClaim("userId").asInt()?.let { JWTPrincipal(credential.payload) }
            }
        }
    }
}
