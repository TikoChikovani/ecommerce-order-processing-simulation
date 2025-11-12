package com.example.app;

import com.example.core.Customer;
import com.example.core.ReservationManager;
import com.example.core.ReservationProcessor;
import com.example.model.Order;
import com.example.model.Product;
import com.example.model.Reservation;
import com.example.model.Warehouse;
import com.example.core.Worker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ProcessingSystem {
    public static void main(String[] args) throws InterruptedException {
        List<Product> productList = Arrays.asList(
                new Product("Laptop", 939.99, 50),
                new Product("Mouse", 29.69, 100),
                new Product("Keyboard", 75.99, 80),
                new Product("Monitor", 239.59, 40),
                new Product("Headphones", 189.99, 60)
        );

        Warehouse warehouse = new Warehouse(productList);
        BlockingQueue<Order> ordersQueue = new LinkedBlockingQueue<>();
        BlockingQueue<Reservation> reservationsQueue = new LinkedBlockingQueue<>();
        List<Order> processedOrders = Collections.synchronizedList(new ArrayList<>());

        System.out.println("Creating reservations");
        ExecutorService reservationService = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 5; i++) {
            reservationService.submit(new ReservationManager(
                    "ReservationCustomer" + i,
                    warehouse,
                    productList,
                    reservationsQueue,
                    5
            ));
        }

        reservationService.shutdown();
        reservationService.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("Created reservations: " + reservationsQueue.size());

        System.out.println("Processing reservations");
        ExecutorService processService = Executors.newFixedThreadPool(3);
        List<ReservationProcessor> processors = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            ReservationProcessor processor = new ReservationProcessor(reservationsQueue, warehouse);
            processors.add(processor);
            processService.submit(processor);
        }

        while (!reservationsQueue.isEmpty()) {
            Thread.sleep(100);
        }

        processors.forEach(ReservationProcessor::stop);
        processService.shutdown();
        processService.awaitTermination(5, TimeUnit.SECONDS);

        int totalProcessed = processors.stream().mapToInt(ReservationProcessor::getProcessed).sum();
        int totalCancelled = processors.stream().mapToInt(ReservationProcessor::getCancelled).sum();

        System.out.println("\nReservations processed: " + totalProcessed);
        System.out.println("Reservations cancelled: " + totalCancelled);
        System.out.println("Active reservations: " + (totalProcessed - totalCancelled));

        System.out.println("Active reservations status");
        Map<String, Map<Product, Integer>> activeReservations = warehouse.getActiveReservations();
        System.out.println("Active reservations: " + activeReservations.size());

        System.out.println("Processing regular orders");

        ExecutorService customerService = Executors.newFixedThreadPool(8);
        for (int i = 0; i < 8; i++) {
            customerService.submit(new Customer("com.example.core.Customer" + i, ordersQueue, productList, 10));
        }

        ExecutorService workerService = Executors.newFixedThreadPool(5);
        List<Worker> workers =new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Worker worker = new Worker(ordersQueue, warehouse, processedOrders);
            workers.add(worker);
            workerService.submit(worker);
        }

        customerService.shutdown();
        customerService.awaitTermination(10, TimeUnit.SECONDS);

        while (!ordersQueue.isEmpty()) {
            Thread.sleep(100);
        }

        workers.forEach(Worker::stop);
        workerService.shutdown();
        workerService.awaitTermination(5, TimeUnit.SECONDS);

        runAnalytics(processedOrders);
    }

    private static void runAnalytics(List<Order> processedOrders) {
        long totalOrders = processedOrders.parallelStream().count();

        double totalProfit = processedOrders.parallelStream().mapToDouble(Order::getOrderPrice).sum();

        Map<String, Long> productSales = processedOrders.parallelStream()
                .flatMap(order -> order.getOrderItems().entrySet().stream())
                .collect(Collectors.groupingByConcurrent(entry -> entry.getKey().getName(),
                        Collectors.summingLong(Map.Entry::getValue)));

        List<Map.Entry<String, Long>> topThreeProducts = productSales.entrySet().parallelStream()
                .sorted(Map.Entry.<String, Long> comparingByValue().reversed())
                .limit(3).toList();

        System.out.println("Total Orders Processed: " + totalOrders);
        System.out.println("Total Profit: " + totalProfit);
        System.out.println("\nTop 3 Best-Selling Products:");
        for (int i = 0; i < topThreeProducts.size(); i++) {
            Map.Entry<String, Long> entry = topThreeProducts.get(i);
            System.out.printf("%d. %s - %d units sold\n", i + 1, entry.getKey(), entry.getValue());
        }

    }
}
