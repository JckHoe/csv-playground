package com.csvplayground.mark2.controller

import com.csvplayground.mark2.service.CsvService
import kotlinx.coroutines.flow.first
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.server.ServerResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/api/csv")
class CsvController(private val csvService: CsvService) {

    @GetMapping("/customer/{id}")
    suspend fun generateCustomerCsv(@PathVariable id: Long) = ServerResponse.ok()
        .contentType(MediaType.parseMediaType("text/csv"))
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=customer_${id}_${getTimestamp()}.csv")
        .bodyValue(csvService.generateCustomerCsv(id).first())

    @GetMapping("/products")
    suspend fun generateProductCsv() = ServerResponse.ok()
        .contentType(MediaType.parseMediaType("text/csv"))
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products_${getTimestamp()}.csv")
        .bodyValue(csvService.generateProductCsv().first())

    @GetMapping("/orders")
    suspend fun generateOrderCsv() = ServerResponse.ok()
        .contentType(MediaType.parseMediaType("text/csv"))
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orders_${getTimestamp()}.csv")
        .bodyValue(csvService.generateOrderCsv().first())

    @GetMapping("/all")
    suspend fun generateAllCsvs(@RequestParam customerId: Long) = ServerResponse.ok()
        .contentType(MediaType.parseMediaType("application/zip"))
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=export_${getTimestamp()}.zip")
        .bodyValue(csvService.generateAllCsvs(customerId).first())

    private fun getTimestamp() = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
} 