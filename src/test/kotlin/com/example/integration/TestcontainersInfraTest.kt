package com.example.integration

import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName
import kotlin.test.assertTrue

class TestcontainersInfraTest {
    @Test
    fun `postgres and rabbitmq containers should start`() {
        PostgreSQLContainer(DockerImageName.parse("postgres:15-alpine")).use { pg ->
            RabbitMQContainer(DockerImageName.parse("rabbitmq:3.13-management")).use { rabbit ->
                pg.start()
                rabbit.start()
                assertTrue(pg.isRunning)
                assertTrue(rabbit.isRunning)
            }
        }
    }

    @Test
    fun `redis container should start`() {
        GenericContainer(DockerImageName.parse("redis:7-alpine")).withExposedPorts(6379).use { redis ->
            redis.start()
            assertTrue(redis.isRunning)
        }
    }
}
