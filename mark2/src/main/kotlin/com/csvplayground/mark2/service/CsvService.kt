package com.csvplayground.mark2.service

import com.csvplayground.mark2.model.*
import com.csvplayground.mark2.repository.CustomerRepository
import com.csvplayground.mark2.repository.OrderRepository
import com.csvplayground.mark2.repository.ProductRepository
import kotlinx.coroutines.flow.*
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.time.format.DateTimeFormatter
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Service
class CsvService(
    private val customerRepository: CustomerRepository,
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository
) {

    suspend fun generateCustomerCsv(customerId: Long): Flow<ByteArray> = flow {
        val customer = customerRepository.findById(customerId) 
            ?: throw IllegalArgumentException("Customer not found")

        // Get all order IDs for the customer
        val orderIds = customerRepository.findOrdersByCustomerId(customerId).toList()
        
        // Get orders with customer info
        val orders = orderRepository.findByIds(orderIds).toList()
        
        // Get order items with product and category info
        val orderItems = orderRepository.findOrderItemsByOrderIds(orderIds).toList()
        
        // Create a map of order items by order ID
        val orderItemsByOrderId = orderItems.groupBy { it.orderId }
        
        // Create a map of orders by ID
        val ordersById = orders.associateBy { it.id }

        val csv = ByteArrayOutputStream().apply {
            write("Customer ID,Name,Email,Phone,Address,Order ID,Order Date,Total Amount,Status,Product Name,Category,Quantity,Unit Price\n".toByteArray())
            
            // Write data rows
            orderItems.forEach { item ->
                val order = ordersById[item.orderId]
                if (order != null) {
                    val record = listOf(
                        customer.id.toString(),
                        "${customer.firstName} ${customer.lastName}",
                        customer.email,
                        customer.phone ?: "",
                        customer.address ?: "",
                        order.id.toString(),
                        order.orderDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        order.totalAmount.toString(),
                        order.status,
                        item.product?.name ?: "",
                        item.product?.category?.name ?: "",
                        item.quantity.toString(),
                        item.unitPrice.toString()
                    ).joinToString(",")
                    write("$record\n".toByteArray())
                }
            }
        }

        emit(csv.toByteArray())
    }

    suspend fun generateProductCsv(): Flow<ByteArray> = flow {
        val products = productRepository.findAllWithCategories().toList()

        val csv = ByteArrayOutputStream().apply {
            write("Product ID,Name,Description,Price,Stock Quantity,Category ID,Category Name,Category Description\n".toByteArray())
            
            products.forEach { product ->
                val record = listOf(
                    product.id.toString(),
                    product.name,
                    product.description ?: "",
                    product.price.toString(),
                    product.stockQuantity.toString(),
                    product.category?.id.toString(),
                    product.category?.name ?: "",
                    product.category?.description ?: ""
                ).joinToString(",")
                write("$record\n".toByteArray())
            }
        }

        emit(csv.toByteArray())
    }

    suspend fun generateOrderCsv(): Flow<ByteArray> = flow {
        // Get all orders with customer info
        val orders = orderRepository.findByIds(emptyList()).toList()
        
        // Get all order items with product and category info
        val orderItems = orderRepository.findOrderItemsByOrderIds(orders.map { it.id }).toList()
        
        // Create a map of order items by order ID
        val orderItemsByOrderId = orderItems.groupBy { it.orderId }

        val csv = ByteArrayOutputStream().apply {
            write("Order ID,Customer ID,Customer Name,Order Date,Total Amount,Status,Item ID,Product Name,Category,Quantity,Unit Price\n".toByteArray())
            
            orderItems.forEach { item ->
                val order = orders.find { it.id == item.orderId }
                if (order != null) {
                    val record = listOf(
                        order.id.toString(),
                        order.customerId.toString(),
                        "${order.customer?.firstName} ${order.customer?.lastName}",
                        order.orderDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        order.totalAmount.toString(),
                        order.status,
                        item.id.toString(),
                        item.product?.name ?: "",
                        item.product?.category?.name ?: "",
                        item.quantity.toString(),
                        item.unitPrice.toString()
                    ).joinToString(",")
                    write("$record\n".toByteArray())
                }
            }
        }

        emit(csv.toByteArray())
    }

    suspend fun generateAllCsvs(customerId: Long): Flow<ByteArray> = flow {
        val zip = ByteArrayOutputStream()
        ZipOutputStream(zip).use { zipOut ->
            // Generate and add customer CSV
            generateCustomerCsv(customerId).collect { customerCsv ->
                zipOut.putNextEntry(ZipEntry("customer_${customerId}.csv"))
                zipOut.write(customerCsv)
                zipOut.closeEntry()
            }

            // Generate and add products CSV
            generateProductCsv().collect { productCsv ->
                zipOut.putNextEntry(ZipEntry("products.csv"))
                zipOut.write(productCsv)
                zipOut.closeEntry()
            }

            // Generate and add orders CSV
            generateOrderCsv().collect { orderCsv ->
                zipOut.putNextEntry(ZipEntry("orders.csv"))
                zipOut.write(orderCsv)
                zipOut.closeEntry()
            }
        }

        emit(zip.toByteArray())
    }
} 