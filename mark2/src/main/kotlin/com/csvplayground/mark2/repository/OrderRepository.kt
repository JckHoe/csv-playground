package com.csvplayground.mark2.repository

import com.csvplayground.mark2.model.Order
import com.csvplayground.mark2.model.OrderItem
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Repository
class OrderRepository(private val db: DatabaseClient) {
    
    suspend fun findByIds(orderIds: List<Long>): Flow<Order> = flow {
        if (orderIds.isEmpty()) return@flow
        
        db.sql("""
            SELECT o.*, c.*
            FROM orders o
            LEFT JOIN customers c ON o.customer_id = c.id
            WHERE o.id IN (:orderIds)
        """)
            .bind("orderIds", orderIds)
            .map { row ->
                Order(
                    id = row.get("o.id") as Long,
                    customerId = row.get("o.customer_id") as Long,
                    orderDate = row.get("o.order_date") as java.time.LocalDateTime,
                    totalAmount = row.get("o.total_amount") as Double,
                    status = row.get("o.status") as String,
                    createdAt = row.get("o.created_at") as? java.time.LocalDateTime,
                    updatedAt = row.get("o.updated_at") as? java.time.LocalDateTime,
                    customer = com.csvplayground.mark2.model.Customer(
                        id = row.get("c.id") as Long,
                        firstName = row.get("c.first_name") as String,
                        lastName = row.get("c.last_name") as String,
                        email = row.get("c.email") as String,
                        phone = row.get("c.phone") as String?,
                        address = row.get("c.address") as String?
                    )
                )
            }
            .all()
            .collect { emit(it) }
    }

    suspend fun findOrderItemsByOrderIds(orderIds: List<Long>): Flow<OrderItem> = flow {
        if (orderIds.isEmpty()) return@flow
        
        db.sql("""
            SELECT oi.*, p.*, c.*
            FROM order_items oi
            LEFT JOIN products p ON oi.product_id = p.id
            LEFT JOIN categories c ON p.category_id = c.id
            WHERE oi.order_id IN (:orderIds)
        """)
            .bind("orderIds", orderIds)
            .map { row ->
                OrderItem(
                    id = row.get("oi.id") as Long,
                    orderId = row.get("oi.order_id") as Long,
                    productId = row.get("oi.product_id") as Long,
                    quantity = row.get("oi.quantity") as Int,
                    unitPrice = row.get("oi.unit_price") as Double,
                    createdAt = row.get("oi.created_at") as? java.time.LocalDateTime,
                    updatedAt = row.get("oi.updated_at") as? java.time.LocalDateTime,
                    product = com.csvplayground.mark2.model.Product(
                        id = row.get("p.id") as Long,
                        categoryId = row.get("p.category_id") as Long,
                        name = row.get("p.name") as String,
                        description = row.get("p.description") as String?,
                        price = row.get("p.price") as Double,
                        stockQuantity = row.get("p.stock_quantity") as Int,
                        category = com.csvplayground.mark2.model.Category(
                            id = row.get("c.id") as Long,
                            name = row.get("c.name") as String,
                            description = row.get("c.description") as String?
                        )
                    )
                )
            }
            .all()
            .collect { emit(it) }
    }
} 