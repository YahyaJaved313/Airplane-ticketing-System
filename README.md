Here is a detailed `README.md` file for your **Airplane Ticketing System** project. I have structured it based on the code provided, highlighting the features, tech stack, setup instructions, and the API dependencies required for the application to function correctly.

-----

# Airplane Ticketing System (AMS) - Client Application

A comprehensive Java Swing-based desktop application for managing airline reservations. This project serves as the **frontend client** ("chicken-front"), designed to interact with a RESTful backend service to handle authentication, flight management, and ticket booking.

## 📋 Table of Contents

  - [Overview](https://www.google.com/search?q=%23overview)
  - [Features](https://www.google.com/search?q=%23-features)
      - [Admin Module](https://www.google.com/search?q=%23admin-module)
      - [Customer Module](https://www.google.com/search?q=%23customer-module)
  - [Technology Stack](https://www.google.com/search?q=%23-technology-stack)
  - [Prerequisites](https://www.google.com/search?q=%23-prerequisites)
  - [Installation & Setup](https://www.google.com/search?q=%23-installation--setup)
  - [Running the Application](https://www.google.com/search?q=%23-running-the-application)
  - [Backend API Requirements](https://www.google.com/search?q=%23-backend-api-requirements)
  - [Project Structure](https://www.google.com/search?q=%23-project-structure)

## Overview

The **AMS Client** provides a modern, responsive User Interface (UI) for an airline system. It features a dual-role architecture (Admin & Customer), allowing administrators to manage flight schedules while customers can search for flights, view status, and book tickets. The application communicates with a backend server via HTTP requests using JSON.

## 🚀 Features

### Admin Module

  * **Secure Login:** Dedicated login portal for administrators.
  * **Dashboard:** Overview of flight operations with a slide-out sidebar navigation.
  * **Flight Management:**
      * **Add Flights:** Create new flight schedules with origin, destination, and timing.
      * **Modify Flights:** Update existing flight details.
      * **Delete Flights:** Remove cancelled or outdated flights.
  * **Live Schedule:** View all active flights in a formatted table.

### Customer Module

  * **User Registration & Login:** Create account and authenticate securely.
  * **Flight Search:** Search for flights by Departure, Destination, and Date.
  * **Book Flights:**
      * Select flight and enter passenger details (Name, Gender, Passport).
      * Simulated payment gateway (Credit/Debit Card, PayPal, Cash).
      * **Digital Receipt:** Generates a bill summary upon successful booking.
  * **Flight Status:** Check the real-time status of specific flights (e.g., Confirmed).
  * **Contact Us:** Interface for user support inquiries.

## 🛠 Technology Stack

  * **Language:** Java 17
  * **GUI Framework:** Java Swing (javax.swing)
  * **Build Tool:** Maven (Wrapper included)
  * **Framework:** Spring Boot 3.4.5 (Client dependencies)
  * **HTTP Client:** Java `java.net.http.HttpClient`
  * **JSON Processing:** `org.json`
  * **Other Libraries:** Spring AI (dependencies present for future AI integration)

## 📋 Prerequisites

Before running this application, ensure you have the following installed:

1.  **Java Development Kit (JDK) 17** or higher.
2.  **Maven** (optional, as `mvnw` is provided).
3.  **Backend Service:** A running REST API server on `localhost:8080` (see [Backend API Requirements](https://www.google.com/search?q=%23-backend-api-requirements)).

## ⚙ Installation & Setup

1.  **Clone the Repository**

    ```bash
    git clone https://github.com/yourusername/airplane-ticketing-system.git
    cd airplane-ticketing-system
    ```

2.  **Configure Application**
    The application is pre-configured to connect to `http://localhost:8080/api`. If your backend runs on a different port or host, modify the `BASE_URL` in:

      * `src/main/java/com/noobcoder/chickenfront/util/HttpClientUtil.java`

3.  **Build the Project**
    Use the included Maven wrapper to install dependencies and build the application:

      * **Windows:**
        ```cmd
        mvnw.cmd clean install
        ```
      * **Linux/Mac:**
        ```bash
        ./mvnw clean install
        ```

## ▶ Running the Application

There are two ways to run the application:

### Method 1: Via Maven

```bash
./mvnw spring-boot:run
```

### Method 2: Running the JAR

After building the project, run the generated JAR file from the `target` directory:

```bash
java -jar target/chicken-front-0.0.1-SNAPSHOT.jar
```

### Method 3: Via IDE

Open the project in **IntelliJ IDEA** or **Eclipse**, navigate to `src/main/java/com/noobcoder/chickenfront/MainApp.java`, and run the `main` method.

> **Note:** The application starts at the **Login Screen**. Use the "Admin Login" or "Register" buttons to navigate.

## 🔌 Backend API Requirements

This client application requires a backend server running on `http://localhost:8080/api` with the following endpoints to function fully:

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/users/health` | Validate login credentials |
| `POST` | `/users/register` | Register a new user |
| `GET` | `/admin/flights` | Retrieve all flights (Admin view) |
| `POST` | `/admin/flights` | Add a new flight |
| `PUT` | `/admin/flights/{id}` | Update flight details |
| `DELETE` | `/admin/flights/{id}` | Delete a flight |
| `GET` | `/flights` | Retrieve all flights (Public view) |
| `GET` | `/flights/search` | Search flights (Params: origin, destination, date) |
| `POST` | `/bookings/book` | Book a ticket (Params: flightNumber, numberOfTickets) |

## 📂 Project Structure

```text
src/main/java/com/noobcoder/chickenfront
├── forms                   # UI Screens and Logic
│   ├── AdminDashboardForm.java      # Admin Management Interface
│   ├── AirlineReservationDashboard.java # Customer Home Interface
│   ├── BookFlightForm.java          # Booking & Payment UI
│   ├── FlightSearchForm.java        # Search Logic
│   ├── LoginForm.java               # Entry Point / Authentication
│   ├── RegisterForm.java            # User Registration
│   ├── CustomStyledButton.java      # Custom UI Component
│   └── ... (Other utility forms)
├── util
│   └── HttpClientUtil.java          # HTTP Request Handler (GET/POST/PUT/DELETE)
└── MainApp.java            # Application Main Entry Point
```

## 🔐 Default Credentials (For Testing)

If connecting to a compatible backend populated with seed data, the code contains fallbacks or hardcoded values for testing in specific forms:

  * **Admin Username:** `admin@example.com`
  * **Admin Password:** `admin123`

-----

*Developed by [Yahya Javed, Muhammad Faizan Zubair, Abdul Hadi]*
