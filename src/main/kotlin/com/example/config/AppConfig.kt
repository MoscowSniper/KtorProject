package com.example.config

private fun env(name: String, default: String): String =
    System.getenv(name) ?: System.getProperty(name, default)

data class AppConfig(
    val dbUrl: String = env("DB_URL", "jdbc:postgresql://localhost:5432/shop"),
    val dbUser: String = env("DB_USER", "shop"),
    val dbPassword: String = env("DB_PASSWORD", "shop"),
    val jwtSecret: String = env("JWT_SECRET", "dev-secret"),
    val jwtIssuer: String = env("JWT_ISSUER", "ktor-shop"),
    val jwtAudience: String = env("JWT_AUDIENCE", "ktor-shop-users"),
    val jwtRealm: String = "shop",
    val redisHost: String = env("REDIS_HOST", "localhost"),
    val redisPort: Int = env("REDIS_PORT", "6379").toInt(),
    val rabbitHost: String = env("RABBIT_HOST", "localhost"),
    val rabbitPort: Int = env("RABBIT_PORT", "5672").toInt()
)
