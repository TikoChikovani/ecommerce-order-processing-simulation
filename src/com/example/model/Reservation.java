package com.example.model;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Reservation {
    public enum ReservationStatus {
        ACTIVE,
        CANCELLED,
        CONFIRMED
    }
   private final String reservationId;
   private final String customerId;
   private final Map<Product, Integer> reservedItems;
   private final LocalDateTime createdAt;
   private ReservationStatus status;

   public Reservation(String customerId) {
       this.reservationId = UUID.randomUUID().toString();
       this.customerId = customerId;
       this.reservedItems = new ConcurrentHashMap<>();
       this.createdAt = LocalDateTime.now();
       this.status = ReservationStatus.ACTIVE;
   }

   public void addProduct(Product product, int quantity) {
       reservedItems.merge(product, quantity, Integer::sum);
   }

   public String getReservationId() {
       return reservationId;
   }

   public String getCustomerId() {
       return customerId;
   }

   public Map<Product, Integer> getReservedItems() {
       return new ConcurrentHashMap<>(reservedItems);
   }

   public LocalDateTime getCreatedAt() {
       return createdAt;
   }

   public ReservationStatus getStatus() {
       return status;
   }

   public void setStatus(ReservationStatus status) {
       this.status = status;
   }

    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId='" + reservationId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", reservedItems=" + reservedItems +
                ", createdAt=" + createdAt +
                ", status=" + status +
                '}';
    }
}
