package com.scanly.scanlyBackend.dtos.receipts;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ReceiptResponse(
        Long orderId,
        Instant creationDate,
        List<ReceiptItemResponse> receiptItemResponseList,
        BigDecimal totalAmount,
        List<ReceiptTaxGroupResponse> receiptTaxGroupResponseList
) { }
