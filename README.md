# LeverX-Homework-2

A multithreaded Java application that simulates an e-commerce order processing system with concurrent customers, warehouse workers, and parallel analytics.

## Task Requirements

There is a product catalog — a list of objects with price and quantity in stock. Several customers create orders at the same time using multithreading (Runnable or ExecutorService). The warehouse is a shared resource (ConcurrentHashMap<Product, Integer>). Several warehouse workers process orders taken from a BlockingQueue<Order>.

After all orders are processed, run analytics in parallel (parallelStream) to show:
- the total number of orders
- the total profit
- the top 3 best-selling products

## Architecture

### Components

**Product**: Represents items in the catalog with name, price, and quantity

**Order**: Contains order ID and map of products to quantities

**Warehouse**: Thread-safe inventory management with ConcurrentHashMap

**Customer**: Runnable that simulates customers creating orders

**Worker**: Runnable that processes orders from the queue

**ProcessingSystem**: Main coordinator class

### Threading Model

**8 Customer Threads**: Each creates 10 random orders (80 total orders)

**5 Warehouse Worker Threads**: Process orders concurrently from the blocking queue

**Parallel Analytics**: Uses parallel streams to compute metrics

## How It Works

**Initialization**: Create product catalog and warehouse with initial inventory

**Order Creation Phase**:
- 8 customer threads run concurrently
- Each creates random orders with 1-10 products
- Orders are placed in a BlockingQueue

**Processing Phase**:
- 5 warehouse workers poll orders from the queue
- Check inventory availability
- Update inventory atomically
- Add successful orders to processed list

**Analytics Phase**:
- Compute total orders processed
- Calculate total profit
- Identify top 3 best-selling products

## Thread Safety Mechanisms

**ConcurrentHashMap**: Thread-safe inventory updates without explicit locking

**BlockingQueue**: Producer-consumer pattern for order processing

**Synchronized Collections**: Protected access to processed orders list

**Synchronized Methods**: Atomic quantity updates in Product class

## Requirements

Java 8 or higher (for lambda expressions and streams)

No external dependencies required

## Compilation and Execution

From the project root directory:

```bash
  # Compile all Java files
  javac src/com/example/*.java -d out

  # Run the main class
  java -cp out com.example.ProcessingSystem
```

## Key Concepts Demonstrated

- Multithreading: ExecutorService, Runnable, thread pools
- Concurrency Utilities: BlockingQueue, ConcurrentHashMap
- Synchronization: synchronized blocks and methods
- Parallel Processing: Parallel streams for analytics
- Producer-Consumer Pattern: Order creation and processing