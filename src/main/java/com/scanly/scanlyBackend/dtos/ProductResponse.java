package com.scanly.scanlyBackend.dtos;

public record ProductResponse(
        String barcode,
        String name,
        Double price
) {}