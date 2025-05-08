package com.csvplayground.mark2.repository

import com.csvplayground.mark2.model.Product
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Repository
class ProductRepository(private val db: DatabaseClient) {
    
    suspend fun findAllWithCategories(): Flow<Product> = flow {
        db.sql("""
            SELECT p.*, c.*
            FROM products p
            LEFT JOIN categories c ON p.category_id = c.id
        """)
            .map { row ->
                Product(
                    id = row.get("p.id") as Long,
                    categoryId = row.get("p.category_id") as Long,
                    name = row.get("p.name") as String,
                    description = row.get("p.description") as String?,
                    price = row.get("p.price") as Double,
                    stockQuantity = row.get("p.stock_quantity") as Int,
                    createdAt = row.get("p.created_at") as? java.time.LocalDateTime,
                    updatedAt = row.get("p.updated_at") as? java.time.LocalDateTime,
                    category = com.csvplayground.mark2.model.Category(
                        id = row.get("c.id") as Long,
                        name = row.get("c.name") as String,
                        description = row.get("c.description") as String?
                    )
                )
            }
            .all()
            .collect { emit(it) }
    }
} 