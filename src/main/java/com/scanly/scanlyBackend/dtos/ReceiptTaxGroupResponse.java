package com.scanly.scanlyBackend.dtos;

import java.math.BigDecimal;

public record ReceiptTaxGroupResponse(
        String label,
        BigDecimal rate,
        BigDecimal net,
        BigDecimal tax,
        BigDecimal gross
) {
}
