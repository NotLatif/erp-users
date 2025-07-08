package com.example.erp.orders.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {
    public Long id;
    public String orderNo;
    public String customer;
    public List<OrderItemResponse> cart;
    public String status;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public static class OrderItemResponse {
        public Long id;
        public Long productId;
        public String productName;
        public Integer quantity;
    }
}