package com.example.queue

import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import org.slf4j.LoggerFactory

class OrderEventWorker(host: String, port: Int) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun start() {
        val queueName = "order-events"
        val factory = ConnectionFactory().apply {
            this.host = host
            this.port = port
        }
        val connection = factory.newConnection()
        val channel = connection.createChannel()
        channel.queueDeclare(queueName, true, false, false, null)

        val deliverCallback = DeliverCallback { _, delivery ->
            val event = String(delivery.body)
            logger.info("Consumed order event: {}", event)
            logger.info("Fake email sent for event: {}", event)
        }
        val cancelCallback = CancelCallback { }

        channel.basicConsume(queueName, true, deliverCallback, cancelCallback)
    }
}
