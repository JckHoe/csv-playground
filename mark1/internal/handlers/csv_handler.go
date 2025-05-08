package handlers

import (
	"net/http"
	"os"
	"strconv"

	"github.com/csv-playground/mark1/pkg/csv"
	"github.com/gin-gonic/gin"
)

type CSVHandler struct {
	generator *csv.Generator
}

func NewCSVHandler(generator *csv.Generator) *CSVHandler {
	return &CSVHandler{generator: generator}
}

func (h *CSVHandler) GenerateCustomerCSV(c *gin.Context) {
	customerID, err := strconv.ParseUint(c.Param("id"), 10, 32)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid customer ID"})
		return
	}

	filename, err := h.generator.GenerateCustomerCSV(uint(customerID))
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	defer os.Remove(filename)

	c.File(filename)
}

func (h *CSVHandler) GenerateProductCSV(c *gin.Context) {
	filename, err := h.generator.GenerateProductCSV()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	defer os.Remove(filename)

	c.File(filename)
}

func (h *CSVHandler) GenerateOrderCSV(c *gin.Context) {
	filename, err := h.generator.GenerateOrderCSV()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	defer os.Remove(filename)

	c.File(filename)
}

func (h *CSVHandler) GenerateAllCSVs(c *gin.Context) {
	// Generate all CSV files
	customerID, err := strconv.ParseUint(c.Query("customer_id"), 10, 32)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid customer ID"})
		return
	}

	customerFile, err := h.generator.GenerateCustomerCSV(uint(customerID))
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	defer os.Remove(customerFile)

	productFile, err := h.generator.GenerateProductCSV()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	defer os.Remove(productFile)

	orderFile, err := h.generator.GenerateOrderCSV()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	defer os.Remove(orderFile)

	// Create zip archive
	zipFile, err := h.generator.CreateZipArchive([]string{customerFile, productFile, orderFile})
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	defer os.Remove(zipFile)

	c.File(zipFile)
} 