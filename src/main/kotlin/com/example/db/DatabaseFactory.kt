package com.example.db

import com.example.config.AppConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init(config: AppConfig) {
        val hikari = HikariConfig().apply {
            jdbcUrl = config.dbUrl
            username = config.dbUser
            password = config.dbPassword
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 10
        }
        Database.connect(HikariDataSource(hikari))
        transaction {
            SchemaUtils.create(Users, Products, Orders, OrderItems, AuditLogs)
        }
    }
}
