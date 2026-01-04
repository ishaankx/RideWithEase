# 🚖 RideWithEase

> A full-stack ride-booking platform connecting passengers with drivers in real-time. Built with Java Servlets, Hibernate, and OpenStreetMap.

![License](https://img.shields.io/badge/License-MIT-blue.svg)
![Java](https://img.shields.io/badge/Java-17%2B-orange)
![Hibernate](https://img.shields.io/badge/Hibernate-ORM-brightgreen)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5-purple)
![Build](https://img.shields.io/badge/Build-Maven-red)

## Overview

**RideWithEase** is a web-based transportation network application that mimics core functionalities of services like Uber or Ola. It features two distinct portals—one for **Riders** to book trips and one for **Drivers** to manage ride requests.

The application utilizes **OpenStreetMap (Leaflet)** for mapping and routing, eliminating the need for paid map API keys during development. It implements a secure authentication system using **JWT (JSON Web Tokens)** and manages data persistence via **Hibernate/JPA**.

## Key Features

### Customer (Rider) Portal
- **Smart Booking:** Interactive map selection for pickup and drop-off locations using Nominatim Search API.
- **Fare Estimation:** Real-time calculation of distance and fare based on vehicle type (Auto, Mini, Prime).
- **Live Status:** Real-time polling to track driver assignment, arrival, and trip progress.
- **Loyalty Program:** Earn points per ride and redeem coupons (e.g., `NEWRIDER`, `10RIDES`).
- **History & Profile:** View past rides, payment breakdowns, and manage profile details.

### Driver Portal
- **Dashboard:** View high-level stats and toggle availability.
- **Ride Radar:** Real-time feed of nearby ride requests with earnings previews.
- **Trip Management:** Interactive map routing, OTP verification for trip start, and trip completion.
- **Earnings:** Instant fare calculation upon trip completion.

### Technical Highlights
- **Security:** Stateless authentication using JWT.
- **Database:** MySQL with Hibernate ORM for object-relational mapping.
- **Routing:** Leaflet Routing Machine for turn-by-turn visual routes.
- **Architecture:** MVC (Model-View-Controller) pattern using Java Servlets.

---

## Tech Stack

| Component | Technology |
|-----------|------------|
| **Backend** | Java (JDK 17/21), Jakarta EE Servlets |
| **Database** | MySQL, Hibernate 6.2 (JPA) |
| **Build Tool** | Apache Maven |
| **Frontend** | JSP, Bootstrap 5, Vanilla JS (ES6) |
| **Maps/Geo** | Leaflet.js, OpenStreetMap, Nominatim API |
| **Auth** | JJWT (Java JSON Web Token) |
| **Server** | Jetty (via Maven Plugin) / Tomcat |

---

## Getting Started

Follow these instructions to set up the project locally.

### Prerequisites
- Java Development Kit (JDK) 17 or higher
- Apache Maven
- MySQL Server

### Installation

1. **Clone the repository**
   ```bash
   git clone [https://github.com/ishaankx/ridewithease.git](https://github.com/ishaankx/ridewithease.git)
   cd ridewithease
    ```
2. **Database Configuration**
   ```bash
      Open src/main/resources/META-INF/persistence.xml and update your MySQL credentials:
   ```
   ```bash
    XML
      <property name="jakarta.persistence.jdbc.user" value="YOUR_USERNAME"/>
      <property name="jakarta.persistence.jdbc.password" value="YOUR_PASSWORD"/>
   ```
   Note: The application is configured to automatically create the database ridewithease_db if it does not exist.


4. **Build the Project**
   ```bash
      mvn clean install
   ```

4. **Run the Application This project uses the Jetty Maven plugin for easy deployment.**
   ```bash
      mvn jetty:run
   ```


5. **Access the App Open your browser and navigate to:**
   ```bash
      http://localhost:8080/index.jsp
   ```




