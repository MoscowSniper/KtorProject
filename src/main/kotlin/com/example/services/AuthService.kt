package com.example.services

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.config.AppConfig
import com.example.db.Users
import com.example.dto.LoginRequest
import com.example.dto.RegisterRequest
import com.example.models.UserRole
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.Date

class AuthService(private val config: AppConfig) {
    private val algorithm = Algorithm.HMAC256(config.jwtSecret)

    fun register(req: RegisterRequest): String = transaction {
        val exists = Users.select { Users.email eq req.email }.any()
        if (exists) throw BusinessException("User already exists")
        val hash = BCrypt.withDefaults().hashToString(12, req.password.toCharArray())
        val userId = Users.insert {
            it[email] = req.email
            it[passwordHash] = hash
            it[role] = UserRole.USER.name
            it[createdAt] = Instant.now()
        } get Users.id
        generateToken(userId.value, UserRole.USER)
    }

    fun login(req: LoginRequest): String = transaction {
        val user = Users.select { Users.email eq req.email }.singleOrNull()
            ?: throw AccessDeniedException("Invalid credentials")
        val verified = BCrypt.verifyer().verify(req.password.toCharArray(), user[Users.passwordHash]).verified
        if (!verified) throw AccessDeniedException("Invalid credentials")
        generateToken(user[Users.id].value, UserRole.valueOf(user[Users.role]))
    }

    private fun generateToken(userId: Int, role: UserRole): String = JWT.create()
        .withAudience(config.jwtAudience)
        .withIssuer(config.jwtIssuer)
        .withClaim("userId", userId)
        .withClaim("role", role.name)
        .withExpiresAt(Date.from(Instant.now().plusSeconds(60 * 60 * 24)))
        .sign(algorithm)
}
