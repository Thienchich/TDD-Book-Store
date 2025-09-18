package com.example.springtestdemo.models;

public enum PaymentStatus {
    PENDING(1),       // chờ thanh toán
    COMPLETED(2),     // Thanh toán thành công
    FAILED(3),        // Thanh toán thất bại
    REFUNDED(4);      // Đã hoàn tiền

    private final int value;

    PaymentStatus(int value) {
        this.value = value;
    }
}