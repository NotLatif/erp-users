# BE Routes

baseUrl = "http://localhost:8080/erp-users-1.0-SNAPSHOT/api/"

## User Routes

GET     baseUrl/users           # List all users (default 0 - 20)
GET     baseUrl/users/{id}      # Get user by ID
POST    baseUrl/users           # Create a new user
PUT     baseUrl/users/{id}      # Update user by ID
DELETE  baseUrl/users/{id}      # Delete user by ID


## inventory Routes

Warehouses have multiple stocks
Products have multiple stocks (one per warehouse)
Stocks are the quantity of a product in a warehouse
One stock refers to one product in one warehouse

### inv/    (general inventory)
GET     baseUrl/inv/             # Get stats about inventory

### inv/product 
GET     baseUrl/inv/product         # List all products (default 0 - 20 ?offset ?limit)
GET     baseUrl/inv/product/{id}    # Get product by ID
POST    baseUrl/inv/product         # Create a new product
PUT     baseUrl/inv/product/{id}    # Update product by ID
DELETE  baseUrl/inv/product/{id}    # Delete product by ID

### inv/warehouse
GET     baseUrl/inv/warehouse         # List all warehouses (default 0 - 20 ?offset ?limit)
GET     baseUrl/inv/warehouse/{id}    # Get warehouse by ID
POST    baseUrl/inv/warehouse         # Create a new warehouse
PUT     baseUrl/inv/warehouse/{id}    # Update warehouse by ID
DELETE  baseUrl/inv/warehouse/{id}    # Delete warehouse by ID  

### inv/stock
GET     baseUrl/inv/stock         # List all stocks (default 0 - 20 ?offset ?limit)
GET     baseUrl/inv/stock/{id}    # Get stock by ID
POST    baseUrl/inv/stock         # Create a new stock  
PUT     baseUrl/inv/stock/{id}    # Update stock by ID
DELETE  baseUrl/inv/stock/{id}    # Delete stock by ID