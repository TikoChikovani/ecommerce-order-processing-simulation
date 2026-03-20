# LeverX-Homework-2

A **multithreaded Java application** that simulates an **e-commerce warehouse system** with concurrent customers, product reservations, warehouse workers, and parallel analytics.

---

## Task Requirements

- There is a **product catalog** — a list of objects with price and quantity in stock.
- Several **customers** create **orders** and **reservations** simultaneously using multithreading (`Runnable` or `ExecutorService`).
- The **warehouse** is a shared resource (`ConcurrentHashMap<Product, Integer>`).
- Several **warehouse workers** process orders taken from a `BlockingQueue<Order>`.
- Several **reservation managers** and **reservation processors** handle product reservations concurrently.
- After all orders are processed, **analytics** is run in parallel (`parallelStream`) to show:
    - total number of processed orders
    - total profit
    - top 3 best-selling products

---

## Reservation Functionality

The warehouse supports **product reservations** before purchase:

- Customers can **reserve** products without creating an order.
- Reserved products are **locked** (not available for others to buy).
- Reservations can be **cancelled**, releasing products back to inventory.
- Reservations can also be **converted into confirmed orders**.

---

## Architecture

### Components

| Component                | Description                                                      |
|--------------------------|------------------------------------------------------------------|
| **Product**              | Represents items in the catalog with name, price, and quantity   |
| **Order**                | Represents confirmed customer purchases                          |
| **Reservation**          | Holds temporary product reservations for a specific customer     |
| **Warehouse**            | Central, thread-safe storage and reservation management system   |
| **Customer**             | Runnable that simulates customers creating orders                |
| **Worker**               | Runnable that processes orders from the queue                    |
| **ReservationManager**   | Runnable that simulates customers creating reservations          |
| **ReservationProcessor** | Runnable that randomly cancels or confirms reservations          |
| **ProcessingSystem**     | Main coordinator that initializes all threads and runs analytics |

---

## Threading Model

| Role                       | Thread Count   | Function                           |
|----------------------------|----------------|------------------------------------|
| **Reservation Managers**   | 5              | Create random product reservations |
| **Reservation Processors** | 3              | Process or cancel reservations     |
| **Customers**              | 8              | Generate random product orders     |
| **Warehouse Workers**      | 5              | Process orders concurrently        |

### Key Thread-Safety Mechanisms

- `ConcurrentHashMap` for inventory and reservation storage
- `BlockingQueue` for producer–consumer coordination
- `synchronized` methods for atomic quantity updates

---

## How It Works

### 1. Initialization
- Create product catalog and initialize warehouse inventory.
- Start blocking queues for orders and reservations.

### 2. Reservation Phase
- `ReservationManager` threads create random reservations.
- Each reservation is verified for available stock.
- If successful, reserved items are deducted from inventory and tracked by `Warehouse`.

### 3. Reservation Processing Phase
- `ReservationProcessor` threads randomly **cancel** or **keep** reservations.
- Cancelled reservations return products to inventory.
- Active reservations remain locked for later order conversion.

### 4. Order Creation Phase
- `Customer` threads generate random orders (independent of reservations).
- Orders are added to a shared `BlockingQueue`.

### 5. Order Processing Phase
- `Worker` threads consume and process orders concurrently.
- Inventory is updated atomically and successful orders are recorded.

### 6. Analytics Phase
- After all processing, a parallel stream computes:
    - **Total orders processed**
    - **Total profit**
    - **Top 3 best-selling products**

---

## Requirements

- **Java 8 or higher** (for lambdas and parallel streams)
- **No external dependencies**

---

## Compilation & Execution

From the project root directory:

```bash
# Compile all Java source files
  javac src/com/example/**/*.java -d out

# Run the main class
  java -cp out com.example.app.ProcessingSystem
```

## Key Concepts Demonstrated

- **Multithreading** with `ExecutorService` and `Runnable`
- **Concurrent data structures** using `ConcurrentHashMap` and `BlockingQueue`
- **Synchronized access** for thread-safe updates to shared resources
- **Producer–Consumer design pattern** for coordinating threads
- **Parallel data analytics** implemented with `parallelStream`
