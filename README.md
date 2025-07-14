# 🛒 Supermarket Billing System

This is a GUI-based desktop application developed in **Java** using **Swing** and **MySQL**, created as part of my **Summer Training at Lovely Professional University**. The project implements a role-based billing system where **Admins** can manage products and view logs, and **Cashiers** can process customer billing in real time.

---

## 📌 Features

- 🔐 **Login System**  
  Secure authentication for Admin and Cashier roles with MySQL validation.

- 🛒 **Billing Interface**  
  Add products to cart using Product ID, calculate totals, and finalize sales.

- 📦 **Product Management**  
  Admins can add, update, and delete product details and manage inventory.

- 📊 **Sales History and Logs**  
  View past sales and user login/billing activity via logging system.

- 💻 **Graphical Interface**  
  Built using Java Swing for a smooth user experience.

- 🔌 **MySQL Integration via JDBC**  
  All data stored securely in a relational database.

---

## 🧰 Technologies Used

| Tool / Technology | Description |
|-------------------|-------------|
| **Java (JDK 8+)** | Core programming language |
| **Java Swing**    | GUI development |
| **MySQL**         | Relational database |
| **JDBC**          | Java Database Connectivity |
| **Java Logging API** | Used to log activities like login and sales |

---

## 💼 Modules Overview

### 🔐 Login Module
- Authenticates users based on credentials stored in MySQL.
- Supports `admin` and `cashier` roles.

### 🧑‍💼 Admin Dashboard
- Add / Update / Delete products
- View logs of user activity and sales

### 👨‍💼 Cashier Dashboard
- Add items to cart using Product ID
- View cart and calculate total
- Finalize sale and auto-update stock

### 🧾 Logging System
- Logs every login and sale transaction
- Stores log data in a dedicated MySQL table

---

## 📸 Screenshots

> _Insert screenshots of:_
- Login screen
- Admin Dashboard
- Product Management
- Billing Cart and Sale Finalization

---

## 🛠️ System Requirements

- Java Development Kit (JDK 8+)
- MySQL Server
- MySQL JDBC Driver
- IDE like IntelliJ / Eclipse / NetBeans

---

## 🗂️ Database Tables

Some required tables include:
- `users`
- `products`
- `sales`
- `sale_items`
- `logs`

Refer to the `schema.sql` file in the repository for full table creation queries.

---

## 🚀 Getting Started

1. Clone the repository  
   ```bash
   git clone https://github.com/your-username/supermarket-billing-system.git
   cd supermarket-billing-system
