package com.palmscanner.pos.viewmodel.datatype;

import androidx.annotation.NonNull;

public class PaymentItem {
    private final String orderId;
    private final int amount;

    public PaymentItem(String orderId, int amount) {
        this.orderId = orderId;
        this.amount = amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public int getAmount() {
        return amount;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getClass().getName() + "( orderId: " + this.orderId + ", amount: " + this.amount + ")";
    }

}
