package com.csvplayground.mark2.repository

import com.csvplayground.mark2.model.Customer
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Repository
class CustomerRepository(private val db: DatabaseClient) {
    
    suspend fun findById(id: Long): Customer? = db.sql("""
        SELECT * FROM customers WHERE id = :id
    """)
        .bind("id", id)
        .map { row ->
            Customer(
                id = row.get("id") as Long,
                firstName = row.get("first_name") as String,
                lastName = row.get("last_name") as String,
                email = row.get("email") as String,
                phone = row.get("phone") as String?,
                address = row.get("address") as String?,
                createdAt = row.get("created_at") as? java.time.LocalDateTime,
                updatedAt = row.get("updated_at") as? java.time.LocalDateTime
            )
        }
        .one()
        .awaitSingleOrNull()

    suspend fun findOrdersByCustomerId(customerId: Long): Flow<Long> = flow {
        db.sql("""
            SELECT id FROM orders WHERE customer_id = :customerId
        """)
            .bind("customerId", customerId)
            .map { row -> row.get("id") as Long }
            .all()
            .collect { emit(it) }
    }
} 