# Money Management System

A comprehensive personal finance management web application built with Spring Boot, designed to help users track their income, expenses, and financial goals with an intuitive dashboard and robust security features.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Database Schema](#database-schema)
- [Security](#security)
- [Configuration](#configuration)
- [Contributing](#contributing)
- [Author](#author)

## 🎯 Overview

This Money Management System is a full-stack web application that provides users with powerful tools to manage their personal finances. The application offers features like expense tracking, income management, categorization, financial dashboards, and secure user authentication with JWT tokens.

The backend is built using Spring Boot with a RESTful API architecture, while supporting a React frontend (separate repository). The system includes email notifications, user activation, and comprehensive financial analytics.

## ✨ Features

- **User Authentication & Authorization**
  - JWT-based authentication
  - User registration with email activation
  - Password encryption with BCrypt
  - Secure session management

- **Financial Management**
  - Income tracking and categorization
  - Expense management with categories
  - Real-time financial dashboard
  - Transaction filtering and search
  - Financial analytics and reporting

- **Category Management**
  - Custom expense and income categories
  - Icon-based category visualization
  - Category-wise financial insights

- **User Profile Management**
  - Profile customization
  - Profile image upload support
  - Account activation system

- **Email Integration**
  - Account activation emails
  - Notification system
  - SMTP integration with Brevo

- **Dashboard & Analytics**
  - Real-time financial overview
  - Recent transactions display
  - Income vs Expense analysis
  - Visual financial insights

## 🛠 Technology Stack

### Backend Technologies
- **Java 21** - Core programming language
- **Spring Boot 3.5.5** - Main framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data persistence layer
- **Spring Web** - RESTful API development
- **Spring Mail** - Email functionality

### Database
- **MySQL** - Primary database
- **Hibernate** - ORM framework
- **JPA** - Data access abstraction

### Security & Authentication
- **JWT (JSON Web Tokens)** - Token-based authentication
- **BCrypt** - Password hashing
- **Spring Security** - Security framework

### Additional Libraries & Tools
- **Lombok** - Boilerplate code reduction
- **Maven** - Dependency management and build tool
- **JJWT** - JWT library for Java
- **MySQL Connector/J** - Database connectivity

### External Services
- **Brevo SMTP** - Email service provider

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/thanh1407/moneymanagement/
│   │   ├── Application.java                 # Main application class
│   │   ├── config/
│   │   │   └── SecurityConfig.java          # Security configuration
│   │   ├── controller/                      # REST API controllers
│   │   │   ├── CategoryController.java
│   │   │   ├── DashboardController.java
│   │   │   ├── ExpenseController.java
│   │   │   ├── FilterController.java
│   │   │   ├── HomeController.java
│   │   │   ├── IncomeController.java
│   │   │   └── ProfileController.java
│   │   ├── dto/                            # Data Transfer Objects
│   │   │   ├── AuthDTO.java
│   │   │   ├── CategoryDTO.java
│   │   │   ├── ExpenseDTO.java
│   │   │   ├── FilterDTO.java
│   │   │   ├── IncomeDTO.java
│   │   │   ├── ProfileDTO.java
│   │   │   └── RecentTransactionDTO.java
│   │   ├── entity/                         # JPA Entities
│   │   │   ├── CategoryEntity.java
│   │   │   ├── ExpenseEntity.java
│   │   │   ├── IncomeEntity.java
│   │   │   └── ProfileEntity.java
│   │   ├── repository/                     # Data repositories
│   │   │   ├── CategoryRepository.java
│   │   │   ├── ExpenseRepository.java
│   │   │   ├── IncomeRepository.java
│   │   │   └── ProfileRepository.java
│   │   ├── security/                       # Security components
│   │   │   └── JwtRequestFilter.java
│   │   ├── service/                        # Business logic layer
│   │   │   ├── AppUserDetailsService.java
│   │   │   ├── CategoryService.java
│   │   │   ├── DashboardService.java
│   │   │   ├── EmailService.java
│   │   │   ├── ExpenseService.java
│   │   │   ├── IncomeService.java
│   │   │   ├── NotificationService.java
│   │   │   └── ProfileService.java
│   │   └── util/                           # Utility classes
│   └── resources/
│       ├── application.properties          # Application configuration
│       ├── static/                         # Static resources
│       └── templates/                      # Email templates
└── test/                                   # Test classes
```

## 🚀 Getting Started

### Prerequisites

- **Java 21** or higher
- **Maven 3.6+**
- **MySQL 8.0+**
- **Git**

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Thanh14013/moneymanagement.git
   cd moneymanagement
   ```

2. **Set up MySQL Database**
   ```sql
   CREATE DATABASE moneymanager;
   ```

3. **Configure Environment Variables**
   Create a `.env` file or set system environment variables:
   ```properties
   BREVO_USERNAME=your_brevo_username
   BREVO_PASSWORD=your_brevo_password
   BREVO_FROM_EMAIL=your_email@domain.com
   API_SECRET_KEY=your_jwt_secret_key_min_32_characters
   ```

4. **Update Database Configuration**
   Modify `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/moneymanager
   spring.datasource.username=your_mysql_username
   spring.datasource.password=your_mysql_password
   ```

5. **Build and Run the Application**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

6. **Access the Application**
   - Backend API: `http://localhost:8080/api/v1.0`
   - Health Check: `http://localhost:8080/api/v1.0/health`

## 📚 API Endpoints

### Authentication
- `POST /api/v1.0/register` - User registration
- `POST /api/v1.0/login` - User login
- `GET /api/v1.0/activate` - Account activation

### Dashboard
- `GET /api/v1.0/dashboard` - Get dashboard data

### Profile Management
- `GET /api/v1.0/profile` - Get user profile
- `PUT /api/v1.0/profile` - Update user profile

### Expense Management
- `GET /api/v1.0/expenses` - Get all expenses
- `POST /api/v1.0/expenses` - Create new expense
- `PUT /api/v1.0/expenses/{id}` - Update expense
- `DELETE /api/v1.0/expenses/{id}` - Delete expense

### Income Management
- `GET /api/v1.0/income` - Get all income
- `POST /api/v1.0/income` - Create new income
- `PUT /api/v1.0/income/{id}` - Update income
- `DELETE /api/v1.0/income/{id}` - Delete income

### Category Management
- `GET /api/v1.0/categories` - Get all categories
- `POST /api/v1.0/categories` - Create new category
- `PUT /api/v1.0/categories/{id}` - Update category
- `DELETE /api/v1.0/categories/{id}` - Delete category

### Health Check
- `GET /api/v1.0/health` - Application health status

## 🗄 Database Schema

### Core Entities

- **tbl_profiles** - User account information
- **tbl_categories** - Expense and income categories
- **tbl_expenses** - Expense transactions
- **tbl_incomes** - Income transactions

### Key Relationships

- Users can have multiple expenses and incomes
- Each expense/income belongs to a category
- Categories can be shared across users
- All entities have audit fields (created_at, updated_at)

## 🔐 Security

- **JWT Authentication** - Stateless authentication using JSON Web Tokens
- **Password Encryption** - BCrypt hashing for secure password storage
- **CORS Configuration** - Cross-origin resource sharing for frontend integration
- **Email Activation** - Account verification via email
- **Session Management** - Stateless session handling
- **API Secret Key** - Configurable JWT signing key

## ⚙️ Configuration

The application uses `application.properties` for configuration:

- **Database Configuration** - MySQL connection settings
- **Email Configuration** - SMTP settings for Brevo
- **Security Configuration** - JWT secret key and authentication settings
- **Logging Configuration** - Debug logging for security components
- **CORS Configuration** - Frontend URL allowlisting

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 👨‍💻 Author

**Nguyễn Vũ Thành**
- Email: thanh14704@gmail.com
- GitHub: [@Thanh14013](https://github.com/Thanh14013)

---

⭐ If you found this project helpful, please give it a star!

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
