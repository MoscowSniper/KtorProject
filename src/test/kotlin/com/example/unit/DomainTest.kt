package com.example.unit

import com.example.models.OrderStatus
import com.example.models.UserRole
import kotlin.test.Test
import kotlin.test.assertEquals

class DomainTest {
    @Test
    fun `user role enum keeps admin`() {
        assertEquals("ADMIN", UserRole.ADMIN.name)
    }

    @Test
    fun `order status has canceled`() {
        assertEquals(OrderStatus.CANCELED, OrderStatus.valueOf("CANCELED"))
    }
}
