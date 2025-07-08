package com.example.erp.orders.dto;

import java.util.stream.Collectors;

import com.example.erp.orders.Order;
import com.example.erp.orders.OrderItem;

public class OrderMapper {
    public static OrderResponse toDto(Order order) {
        if (order == null) return null;
        OrderResponse dto = new OrderResponse();
        dto.id = order.getId();
        dto.orderNo = order.getOrderNo();
        dto.customer = order.getCustomer();
        dto.status = order.getStatus().name();
        dto.createdAt = order.getCreatedAt();
        dto.updatedAt = order.getUpdatedAt();
        dto.cart = order.getCart() == null ? null : order.getCart().stream().map(OrderMapper::toDto).collect(Collectors.toList());
        return dto;
    }

    public static OrderResponse.OrderItemResponse toDto(OrderItem item) {
        if (item == null) return null;
        OrderResponse.OrderItemResponse dto = new OrderResponse.OrderItemResponse();
        dto.id = item.getId();
        dto.productId = item.getProduct() != null ? item.getProduct().getId() : null;
        dto.productName = item.getProduct() != null ? item.getProduct().getName() : null;
        dto.quantity = item.getQuantity();
        return dto;
    }
}