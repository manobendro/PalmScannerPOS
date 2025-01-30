package com.palmscanner.pos.viewmodel.datatype;

public class BankCardItem {
    private String cardNo;

    public BankCardItem(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }
}
