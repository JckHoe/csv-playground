USE csv_playground;

-- Insert Categories
INSERT INTO categories (name, description) VALUES
('Electronics', 'Electronic devices and accessories'),
('Clothing', 'Apparel and fashion items'),
('Books', 'Books and publications'),
('Home & Garden', 'Home decor and garden supplies'),
('Sports', 'Sports equipment and accessories');

-- Insert Products
INSERT INTO products (category_id, name, description, price, stock_quantity) VALUES
(1, 'Smartphone X', 'Latest smartphone model', 999.99, 50),
(1, 'Laptop Pro', 'High-performance laptop', 1499.99, 30),
(2, 'T-Shirt', 'Cotton t-shirt', 29.99, 100),
(2, 'Jeans', 'Denim jeans', 79.99, 75),
(3, 'Programming Book', 'Learn programming', 49.99, 25),
(4, 'Garden Tools Set', 'Complete garden tools', 89.99, 40),
(5, 'Yoga Mat', 'Premium yoga mat', 39.99, 60);

-- Insert Customers
INSERT INTO customers (first_name, last_name, email, phone, address) VALUES
('John', 'Doe', 'john.doe@email.com', '123-456-7890', '123 Main St'),
('Jane', 'Smith', 'jane.smith@email.com', '234-567-8901', '456 Oak Ave'),
('Bob', 'Johnson', 'bob.johnson@email.com', '345-678-9012', '789 Pine Rd'),
('Alice', 'Brown', 'alice.brown@email.com', '456-789-0123', '321 Elm St'),
('Charlie', 'Wilson', 'charlie.wilson@email.com', '567-890-1234', '654 Maple Dr');

-- Insert Orders
INSERT INTO orders (customer_id, order_date, total_amount, status) VALUES
(1, NOW(), 1029.98, 'COMPLETED'),
(2, NOW(), 1499.99, 'PROCESSING'),
(3, NOW(), 159.98, 'COMPLETED'),
(4, NOW(), 89.99, 'SHIPPED'),
(5, NOW(), 39.99, 'COMPLETED');

-- Insert Order Items
INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES
(1, 1, 1, 999.99),
(1, 3, 1, 29.99),
(2, 2, 1, 1499.99),
(3, 3, 2, 29.99),
(3, 4, 1, 79.99),
(4, 6, 1, 89.99),
(5, 7, 1, 39.99); 