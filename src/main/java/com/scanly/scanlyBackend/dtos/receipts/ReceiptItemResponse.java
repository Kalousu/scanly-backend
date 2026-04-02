package com.scanly.scanlyBackend.dtos.receipts;

import java.math.BigDecimal;

public record ReceiptItemResponse(
        String productName,
        BigDecimal amount,
        BigDecimal unitPriceNet,
        BigDecimal unitPriceGross,
        BigDecimal taxRate,
        BigDecimal totalPriceGross,
        String taxLabel
) { }
