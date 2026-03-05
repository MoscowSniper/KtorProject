package com.example.cache

import redis.clients.jedis.JedisPooled

class OrderCache(host: String, port: Int) {
    private val jedis = JedisPooled(host, port)

    fun put(orderId: Int, payload: String) {
        jedis.setex("order:$orderId", 3600, payload)
    }
}
