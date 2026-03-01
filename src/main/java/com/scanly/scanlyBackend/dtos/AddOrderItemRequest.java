package com.scanly.scanlyBackend.dtos;

public record AddOrderItemRequest (
        String code,
        int amount
){}
