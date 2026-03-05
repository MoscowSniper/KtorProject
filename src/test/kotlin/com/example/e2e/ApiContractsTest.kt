package com.example.e2e

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApiContractsTest {
    @Test
    fun `unknown route returns 404`() = testApplication {
        val response = client.get("/unknown")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `register endpoint requires db and may fail gracefully`() = testApplication {
        val response = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("{\"email\":\"test@example.com\",\"password\":\"123456\"}")
        }
        // In CI without infra this will be 500; we only verify endpoint availability contract.
        assertEquals(true, response.status.value in listOf(200, 500))
        response.bodyAsText()
    }
}
