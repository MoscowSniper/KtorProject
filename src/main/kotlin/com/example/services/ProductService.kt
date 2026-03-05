package com.example.services

import com.example.db.Products
import com.example.dto.ProductRequest
import com.example.dto.ProductResponse
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.Instant

class ProductService {
    fun list(): List<ProductResponse> = transaction {
        Products.selectAll().map(::toResponse)
    }

    fun getById(id: Int): ProductResponse = transaction {
        Products.selectAll().where { Products.id eq id }.map(::toResponse).singleOrNull()
            ?: throw NotFoundException("Product $id not found")
    }

    fun create(req: ProductRequest): ProductResponse = transaction {
        val id = Products.insert {
            it[name] = req.name
            it[description] = req.description
            it[price] = req.price.toBigDecimal()
            it[stock] = req.stock
            it[updatedAt] = Instant.now()
        } get Products.id
        getById(id.value)
    }

    fun update(id: Int, req: ProductRequest): ProductResponse = transaction {
        val updated = Products.update({ Products.id eq id }) {
            it[name] = req.name
            it[description] = req.description
            it[price] = req.price.toBigDecimal()
            it[stock] = req.stock
            it[updatedAt] = Instant.now()
        }
        if (updated == 0) throw NotFoundException("Product $id not found")
        getById(id)
    }

    fun delete(id: Int) = transaction {
        if (Products.deleteWhere { Products.id eq id } == 0) {
            throw NotFoundException("Product $id not found")
        }
    }

    private fun toResponse(row: ResultRow) = ProductResponse(
        id = row[Products.id].value,
        name = row[Products.name],
        description = row[Products.description],
        price = row[Products.price].toDouble(),
        stock = row[Products.stock]
    )
}
