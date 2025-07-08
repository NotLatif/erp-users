package com.example.erp.orders.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class OrderRequest {

    @NotBlank
    @Size(max = 30)
    public String orderNo;

    @NotBlank
    @Size(max = 100)
    public String customer;

    @NotNull
    @Size(min = 1)
    @Valid
    public List<CartItem> cart;

    @NotBlank
    @Pattern(regexp = "PROCESSING|SHIPPING|DELIVERED|CANCELED")
    public String status;

    public static class CartItem {
        @NotNull
        public Long productId;

        @NotNull
        @Min(1)
        public Integer quantity;
    }
}