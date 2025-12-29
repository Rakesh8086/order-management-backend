**Initial Entity design**

**Product Service:**

**Products** - Id, name, description, category, brand, price, discount 

**Inventory** - Id, productId(FK), currStock, minStockLevel

**Order Service:**

**Orders** - Id, customerId(FK), orderDate, totalAmount, Status, deliveryWithinDays, address

**OrderItems** - Id, OrderId(FK), productId(FK), quantity, unit cost

**Notification Service:**

**Notifications** - Id, recipientEmail, orderId(FK), timestamp, Status

**Billing Service:**

**Invoices** - Id, orderId(FK), totalAmount, invoiceDate, status 

**Authentication Service:**

**Users** - Id, username, mobileNumber, email, password, roles 
