package com.example.queue

import com.rabbitmq.client.Channel
import com.rabbitmq.client.ConnectionFactory

class OrderEventPublisher(host: String, port: Int) {
    private val queueName = "order-events"
    private val channel: Channel

    init {
        val factory = ConnectionFactory().apply {
            this.host = host
            this.port = port
        }
        val connection = factory.newConnection()
        channel = connection.createChannel()
        channel.queueDeclare(queueName, true, false, false, null)
    }

    fun publish(message: String) {
        channel.basicPublish("", queueName, null, message.toByteArray())
    }
}
