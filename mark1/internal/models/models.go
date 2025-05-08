package models

import (
	"time"

	"gorm.io/gorm"
)

type Category struct {
	ID          uint      `gorm:"primaryKey" json:"id"`
	Name        string    `gorm:"size:100;not null" json:"name"`
	Description string    `gorm:"type:text" json:"description"`
	CreatedAt   time.Time `json:"created_at"`
	UpdatedAt   time.Time `json:"updated_at"`
	Products    []Product `json:"products,omitempty"`
}

type Product struct {
	ID            uint      `gorm:"primaryKey" json:"id"`
	CategoryID    uint      `gorm:"not null" json:"category_id"`
	Category      Category  `gorm:"foreignKey:CategoryID" json:"category,omitempty"`
	Name          string    `gorm:"size:200;not null" json:"name"`
	Description   string    `gorm:"type:text" json:"description"`
	Price         float64   `gorm:"type:decimal(10,2);not null" json:"price"`
	StockQuantity int       `gorm:"not null" json:"stock_quantity"`
	CreatedAt     time.Time `json:"created_at"`
	UpdatedAt     time.Time `json:"updated_at"`
	OrderItems    []OrderItem `json:"order_items,omitempty"`
}

type Customer struct {
	ID        uint      `gorm:"primaryKey" json:"id"`
	FirstName string    `gorm:"size:100;not null" json:"first_name"`
	LastName  string    `gorm:"size:100;not null" json:"last_name"`
	Email     string    `gorm:"size:255;not null;unique" json:"email"`
	Phone     string    `gorm:"size:20" json:"phone"`
	Address   string    `gorm:"type:text" json:"address"`
	CreatedAt time.Time `json:"created_at"`
	UpdatedAt time.Time `json:"updated_at"`
	Orders    []Order   `json:"orders,omitempty"`
}

type Order struct {
	ID          uint        `gorm:"primaryKey" json:"id"`
	CustomerID  uint        `gorm:"not null" json:"customer_id"`
	Customer    Customer    `gorm:"foreignKey:CustomerID" json:"customer,omitempty"`
	OrderDate   time.Time   `gorm:"not null" json:"order_date"`
	TotalAmount float64     `gorm:"type:decimal(10,2);not null" json:"total_amount"`
	Status      string      `gorm:"size:50;not null" json:"status"`
	CreatedAt   time.Time   `json:"created_at"`
	UpdatedAt   time.Time   `json:"updated_at"`
	OrderItems  []OrderItem `json:"order_items,omitempty"`
}

type OrderItem struct {
	ID        uint      `gorm:"primaryKey" json:"id"`
	OrderID   uint      `gorm:"not null" json:"order_id"`
	Order     Order     `gorm:"foreignKey:OrderID" json:"order,omitempty"`
	ProductID uint      `gorm:"not null" json:"product_id"`
	Product   Product   `gorm:"foreignKey:ProductID" json:"product,omitempty"`
	Quantity  int       `gorm:"not null" json:"quantity"`
	UnitPrice float64   `gorm:"type:decimal(10,2);not null" json:"unit_price"`
	CreatedAt time.Time `json:"created_at"`
	UpdatedAt time.Time `json:"updated_at"`
} 