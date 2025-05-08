package main

import (
	"log"

	"github.com/csv-playground/mark1/internal/config"
	"github.com/csv-playground/mark1/internal/handlers"
	"github.com/csv-playground/mark1/pkg/csv"
	"github.com/gin-gonic/gin"
)

func main() {
	// Initialize database
	db, err := config.InitDB()
	if err != nil {
		log.Fatalf("Failed to initialize database: %v", err)
	}

	// Initialize CSV generator
	generator := csv.NewGenerator(db)

	// Initialize handler
	csvHandler := handlers.NewCSVHandler(generator)

	// Initialize router
	router := gin.Default()

	// Routes
	router.GET("/api/csv/customer/:id", csvHandler.GenerateCustomerCSV)
	router.GET("/api/csv/products", csvHandler.GenerateProductCSV)
	router.GET("/api/csv/orders", csvHandler.GenerateOrderCSV)
	router.GET("/api/csv/all", csvHandler.GenerateAllCSVs)

	// Start server
	if err := router.Run(":8080"); err != nil {
		log.Fatalf("Failed to start server: %v", err)
	}
} 