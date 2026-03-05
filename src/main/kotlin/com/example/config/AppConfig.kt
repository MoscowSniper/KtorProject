package com.example.config

private fun envOrNull(name: String): String? =
    System.getenv(name) ?: System.getProperty(name)

private fun firstNonBlank(vararg values: String?): String? =
    values.firstOrNull { !it.isNullOrBlank() }

private fun normalizeDbUrl(raw: String): String =
    if (raw.startsWith("jdbc:")) raw else "jdbc:${raw}"

data class AppConfig(
    val dbUrl: String = normalizeDbUrl(
        firstNonBlank(
            envOrNull("DB_URL"),
            envOrNull("JDBC_DATABASE_URL"),
            envOrNull("DATABASE_URL")
        ) ?: "jdbc:postgresql://localhost:5432/postgres"
    ),
    val dbUser: String = firstNonBlank(
        envOrNull("DB_USER"),
        envOrNull("JDBC_DATABASE_USERNAME"),
        envOrNull("POSTGRES_USER"),
        envOrNull("PGUSER")
    ) ?: "postgres",
    val dbPassword: String = firstNonBlank(
        envOrNull("DB_PASSWORD"),
        envOrNull("JDBC_DATABASE_PASSWORD"),
        envOrNull("POSTGRES_PASSWORD"),
        envOrNull("PGPASSWORD")
    ) ?: "postgres",
    val jwtSecret: String = firstNonBlank(envOrNull("JWT_SECRET"), "dev-secret")!!,
    val jwtIssuer: String = firstNonBlank(envOrNull("JWT_ISSUER"), "ktor-shop")!!,
    val jwtAudience: String = firstNonBlank(envOrNull("JWT_AUDIENCE"), "ktor-shop-users")!!,
    val jwtRealm: String = "shop",
    val redisHost: String = firstNonBlank(envOrNull("REDIS_HOST"), "localhost")!!,
    val redisPort: Int = firstNonBlank(envOrNull("REDIS_PORT"), "6379")!!.toInt(),
    val rabbitHost: String = firstNonBlank(envOrNull("RABBIT_HOST"), "localhost")!!,
    val rabbitPort: Int = firstNonBlank(envOrNull("RABBIT_PORT"), "5672")!!.toInt()
)
