package com.csvplayground.mark2.model

import java.time.LocalDateTime

data class Category(
    val id: Long? = null,
    val name: String,
    val description: String?,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)

data class Product(
    val id: Long? = null,
    val categoryId: Long,
    val name: String,
    val description: String?,
    val price: Double,
    val stockQuantity: Int,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val category: Category? = null
)

data class Customer(
    val id: Long? = null,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String?,
    val address: String?,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)

data class Order(
    val id: Long? = null,
    val customerId: Long,
    val orderDate: LocalDateTime,
    val totalAmount: Double,
    val status: String,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val customer: Customer? = null,
    val orderItems: List<OrderItem>? = null
)

data class OrderItem(
    val id: Long? = null,
    val orderId: Long,
    val productId: Long,
    val quantity: Int,
    val unitPrice: Double,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val product: Product? = null
) 