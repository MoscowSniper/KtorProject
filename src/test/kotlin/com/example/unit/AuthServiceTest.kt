package com.example.unit

import at.favre.lib.crypto.bcrypt.BCrypt
import kotlin.test.Test
import kotlin.test.assertTrue

class AuthServiceTest {
    @Test
    fun `bcrypt hash should verify`() {
        val password = "secret"
        val hash = BCrypt.withDefaults().hashToString(4, password.toCharArray())
        val verified = BCrypt.verifyer().verify(password.toCharArray(), hash).verified
        assertTrue(verified)
    }
}
