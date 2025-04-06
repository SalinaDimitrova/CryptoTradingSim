# 💰 Cryptocurrency Trading Platform

A web-based platform that simulates cryptocurrency trading with real-time price updates and transaction tracking. Users can buy and sell digital assets, monitor portfolio performance, and view detailed trade history. The platform integrates with Kraken’s WebSocket API for live market data and uses MariaDB for persistent data storage.

---

## 🔧 Tech Stack

### Frontend
- **React** – JavaScript library for building user interfaces  
- **Vite** – Fast and modern frontend build tool  
- **TailwindCSS** – Utility-first CSS framework for responsive, clean styling  

### Backend
- **Spring Boot** – Java framework for building robust RESTful APIs  
- **MariaDB** – Relational database for storing user and transaction data  
- **Kraken WebSocket API** – Provides real-time cryptocurrency price updates  

---

## ✨ Features

- **📈 Real-Time Price Updates**  
  Live pricing data for major cryptocurrencies like BTC and ETH via Kraken WebSocket API.

- **💱 Buy/Sell Crypto Assets**  
  Simulate the buying and selling of cryptocurrencies through a simple trading interface.

- **🧾 Transaction History**  
  View a complete log of trades, including timestamps, prices, and asset quantities.

- **📊 Profit & Loss Tracking**  
  Automatically calculate profits and losses across all transactions.

- **🔄 Account Reset Option**  
  Reset your balance and holdings at any time to start fresh.

- **💾 Persistent Storage**  
  All data is saved in a MariaDB database to ensure data integrity and persistence.

---

## 🚀 Project Setup

### 📋 Prerequisites

Ensure the following are installed on your system:

- **Java 21** or later (for the Spring Boot backend)
- **Maven** (for building and running the backend)
- **Node.js** (v22.14.0) and **npm** (for the frontend)
- **MariaDB** (configured locally or via Docker)

---

### 📦 Clone the Repository

```bash

git clone https://github.com/SalinaDimitrova/CryptoTradingSim

cd CryptoTradingSim
```
---
## 🔙 Backend Setup

1. **Navigate to the backend directory**:
```bash
 cd crypto-trading-sim
```
2. **Build the project using Maven**:
  ```bash
./mvnw clean install
  ```

3. **Run the application**:
  ```bash
./mvnw spring-boot:run
  ```
Or run the CryptoTradingSimApplication.java class from your IDE.

4. **Default configuration: The application.properties file is already set up with**:

- **MariaDB Connection**: `localhost:3306/crypto_trading_sim`
- **Username**: `root`
- **Password**: `sd045001`
- **WebSocket Endpoint**: `wss://ws.kraken.com/v2`

## 🌐 Frontend Setup

Follow these steps to run the frontend locally:

1. Navigate to the frontend directory:
  ```bash
  cd frontend
```

2. Install project dependencies:
  ```bash
  npm install
```

3. Start the development server:
```bash
  npm run dev
```

## ⚙️ System Overview

The backend provides RESTful APIs for handling cryptocurrency transactions, managing account balances, tracking holdings, and broadcasting real-time prices from the Kraken WebSocket API.

The frontend is developed using **React** and **TailwindCSS**, delivering a dynamic and responsive interface for placing trades, monitoring portfolios, and viewing live market data.
## 📡 API Endpoints

Below are the available REST API endpoints for interacting with accounts, orders, portfolios, and live prices.

---

### 🔐 Account Endpoints

#### 1. Get Account Details
- **Endpoint**: `/accounts/{accountId}`
- **Method**: `GET`
- **Description**: Retrieves account information such as balance and ID.
- **Response**:  
  ```json
  {
    "id": 1,
    "balance": 10000.0
  }
  
#### 2. Reset Account
Endpoint: `/accounts/{accountId}/reset`

Method: `POST`

Description: Resets the specified account to its default state (balance and data).

Response:
  ```json
"Account reset to default balance."
```
### 💱 Order Endpoints
#### 3. Place Order (Buy/Sell)
Endpoint: `/accounts/{accountId}/orders`

Method: `POST`

Description: Places a buy or sell order for a cryptocurrency.

Request Body:
  ```json
{
  "symbol": "BTC/USD",
  "amount": 0.5,
  "type": "BUY" // or "SELL"
}
```
Response:
  ```json
"Order placed successfully"
```
#### 4. Get All Orders
Endpoint: `/accounts/{accountId}/orders`

Method: `GET`

Description: Fetches all orders (buy and sell) for the given account.

Response:
```json
[
  {
    "symbol": "BTC/USD",
    "amount": 0.5,
    "price": 38000.0,
    "type": "BUY"
  },
  {
    "symbol": "ETH/USD",
    "amount": 1.2,
    "price": 2400.0,
    "type": "SELL"
  }
]
```
### 📊 Portfolio Endpoints
#### 5. Get All Holdings
Endpoint: `/portfolios`

Method: `GET`

Description: Returns the current holdings of all cryptocurrencies.

Response:
```json
[
  {
    "symbol": "BTC",
    "amount": 0.5
  },
  {
    "symbol": "ETH",
    "amount": 2.0
  }
]
```
#### 6. Get Holding by Symbol
Endpoint: `/portfolios/symbols/{symbol}`

Method: `GET`

Description: Fetches the amount held for a specific cryptocurrency symbol.

Response:
```json
{
  "symbol": "BTC",
  "amount": 0.5
}
```
### 💸 Price Endpoints
#### 7. Get All Live Prices (To be added via controller)
Endpoint: `/api/prices`

Method: `GET`

Description: Returns the latest prices for all tracked cryptocurrencies.

Response:
```json
{
  "BTC/USD": 38250.25,
  "ETH/USD": 2470.30
}
```

### 🔌 WebSocket (Real-Time Price Updates)
The backend streams real-time cryptocurrency prices from Kraken using STOMP over WebSocket.

#### WebSocket Info
WebSocket Endpoint: `ws://localhost:8080/ws`

Subscribe to Topic: `/topic/prices`

Send Message To: `/app/subscribe (optional, e.g., with payload "BTC/USD")`

#### 📥 Example Message from /topic/prices:
```json
[
  {
    "symbol": "BTC/USD",
    "price": 38250.25
  },
  {
    "symbol": "ETH/USD",
    "price": 2470.30
  }
]
```
>Messages are broadcast as soon as price data is received from Kraken.

## 🖼️ Screenshots & Demo

### 🧊 Initial Screen – Top 20 Cryptocurrency Prices

![Top 20 Prices](./assets/home-screen.png)
![image](https://github.com/user-attachments/assets/341adf23-4dec-4dc8-8673-bc8b7b81c06f)

---

### 💱 Buy/Sell Interface
![image](https://github.com/user-attachments/assets/80e0fb38-1331-4427-ac60-368cbd150f6f)


---

### 💰 Updated Account Balance After Transaction
![image](https://github.com/user-attachments/assets/e43cfd23-7cec-4279-8ff2-ac9237439a84)


---

### 🧾 Transaction History 
![image](https://github.com/user-attachments/assets/35d54aa4-ee79-4bf2-a089-50ca813e100d)


---

### 🎥 Demo – User Journey Through the Platform
[![Watch the Demo](([https://www.youtube.com/watch?v=VIDEO_ID](https://youtu.be/zrkFcFVKW7k))

