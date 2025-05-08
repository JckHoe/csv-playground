package csv

import (
	"archive/zip"
	"encoding/csv"
	"fmt"
	"io"
	"os"
	"path/filepath"
	"time"

	"github.com/csv-playground/mark1/internal/models"
	"gorm.io/gorm"
)

type Generator struct {
	db *gorm.DB
}

func NewGenerator(db *gorm.DB) *Generator {
	return &Generator{db: db}
}

func (g *Generator) GenerateCustomerCSV(customerID uint) (string, error) {
	var customer models.Customer
	if err := g.db.Preload("Orders.OrderItems.Product.Category").First(&customer, customerID).Error; err != nil {
		return "", err
	}

	filename := fmt.Sprintf("customer_%d_%s.csv", customerID, time.Now().Format("20060102150405"))
	file, err := os.Create(filename)
	if err != nil {
		return "", err
	}
	defer file.Close()

	writer := csv.NewWriter(file)
	defer writer.Flush()

	// Write header
	header := []string{"Customer ID", "Name", "Email", "Phone", "Address", "Order ID", "Order Date", "Total Amount", "Status", "Product Name", "Category", "Quantity", "Unit Price"}
	if err := writer.Write(header); err != nil {
		return "", err
	}

	// Write data
	for _, order := range customer.Orders {
		for _, item := range order.OrderItems {
			record := []string{
				fmt.Sprintf("%d", customer.ID),
				fmt.Sprintf("%s %s", customer.FirstName, customer.LastName),
				customer.Email,
				customer.Phone,
				customer.Address,
				fmt.Sprintf("%d", order.ID),
				order.OrderDate.Format("2006-01-02 15:04:05"),
				fmt.Sprintf("%.2f", order.TotalAmount),
				order.Status,
				item.Product.Name,
				item.Product.Category.Name,
				fmt.Sprintf("%d", item.Quantity),
				fmt.Sprintf("%.2f", item.UnitPrice),
			}
			if err := writer.Write(record); err != nil {
				return "", err
			}
		}
	}

	return filename, nil
}

func (g *Generator) GenerateProductCSV() (string, error) {
	var products []models.Product
	if err := g.db.Preload("Category").Find(&products).Error; err != nil {
		return "", err
	}

	filename := fmt.Sprintf("products_%s.csv", time.Now().Format("20060102150405"))
	file, err := os.Create(filename)
	if err != nil {
		return "", err
	}
	defer file.Close()

	writer := csv.NewWriter(file)
	defer writer.Flush()

	// Write header
	header := []string{"Product ID", "Name", "Description", "Price", "Stock Quantity", "Category ID", "Category Name", "Category Description"}
	if err := writer.Write(header); err != nil {
		return "", err
	}

	// Write data
	for _, product := range products {
		record := []string{
			fmt.Sprintf("%d", product.ID),
			product.Name,
			product.Description,
			fmt.Sprintf("%.2f", product.Price),
			fmt.Sprintf("%d", product.StockQuantity),
			fmt.Sprintf("%d", product.Category.ID),
			product.Category.Name,
			product.Category.Description,
		}
		if err := writer.Write(record); err != nil {
			return "", err
		}
	}

	return filename, nil
}

func (g *Generator) GenerateOrderCSV() (string, error) {
	var orders []models.Order
	if err := g.db.Preload("Customer").Preload("OrderItems.Product.Category").Find(&orders).Error; err != nil {
		return "", err
	}

	filename := fmt.Sprintf("orders_%s.csv", time.Now().Format("20060102150405"))
	file, err := os.Create(filename)
	if err != nil {
		return "", err
	}
	defer file.Close()

	writer := csv.NewWriter(file)
	defer writer.Flush()

	// Write header
	header := []string{"Order ID", "Customer ID", "Customer Name", "Order Date", "Total Amount", "Status", "Item ID", "Product Name", "Category", "Quantity", "Unit Price"}
	if err := writer.Write(header); err != nil {
		return "", err
	}

	// Write data
	for _, order := range orders {
		for _, item := range order.OrderItems {
			record := []string{
				fmt.Sprintf("%d", order.ID),
				fmt.Sprintf("%d", order.Customer.ID),
				fmt.Sprintf("%s %s", order.Customer.FirstName, order.Customer.LastName),
				order.OrderDate.Format("2006-01-02 15:04:05"),
				fmt.Sprintf("%.2f", order.TotalAmount),
				order.Status,
				fmt.Sprintf("%d", item.ID),
				item.Product.Name,
				item.Product.Category.Name,
				fmt.Sprintf("%d", item.Quantity),
				fmt.Sprintf("%.2f", item.UnitPrice),
			}
			if err := writer.Write(record); err != nil {
				return "", err
			}
		}
	}

	return filename, nil
}

func (g *Generator) CreateZipArchive(files []string) (string, error) {
	zipFilename := fmt.Sprintf("export_%s.zip", time.Now().Format("20060102150405"))
	zipFile, err := os.Create(zipFilename)
	if err != nil {
		return "", err
	}
	defer zipFile.Close()

	zipWriter := zip.NewWriter(zipFile)
	defer zipWriter.Close()

	for _, file := range files {
		fileToZip, err := os.Open(file)
		if err != nil {
			return "", err
		}
		defer fileToZip.Close()

		writer, err := zipWriter.Create(filepath.Base(file))
		if err != nil {
			return "", err
		}

		_, err = io.Copy(writer, fileToZip)
		if err != nil {
			return "", err
		}
	}

	return zipFilename, nil
} 