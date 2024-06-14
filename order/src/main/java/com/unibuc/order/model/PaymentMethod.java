package com.unibuc.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentMethod {
    CREDIT_CARD("CREDIT CARD"),
    DEBIT_CARD("DEBIT CARD"),
    PAYPAL("PAYPAL"),
    BANK_TRANSFER("BANK TRANSFER"),
    CASH_ON_DELIVERY("CASH ON DELIVERY");

    final private String description;
}