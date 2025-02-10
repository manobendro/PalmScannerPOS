package com.palmscanner.pos.database.model;

import android.provider.BaseColumns;

import androidx.annotation.NonNull;

public final class User {


    private String uuid;
    private String palmTemplate;
    private String cardNumber;
    private String cardExpirationDate;
    private String cardCvv;
    private String cardHolderName;
    private String cardType;

    public User(){

    }

    public User(String uuid, String palmTemplate, String cardNumber, String cardExpirationDate, String cardCvv, String cardHolderName, String cardType) {
        this.uuid = uuid;
        this.palmTemplate = palmTemplate;
        this.cardNumber = cardNumber;
        this.cardExpirationDate = cardExpirationDate;
        this.cardCvv = cardCvv;
        this.cardHolderName = cardHolderName;
        this.cardType = cardType;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPalmTemplate() {
        return palmTemplate;
    }

    public void setPalmTemplate(String palmTemplate) {
        this.palmTemplate = palmTemplate;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardExpirationDate() {
        return cardExpirationDate;
    }

    public void setCardExpirationDate(String cardExpirationDate) {
        this.cardExpirationDate = cardExpirationDate;
    }

    public String getCardCvv() {
        return cardCvv;
    }

    public void setCardCvv(String cardCvv) {
        this.cardCvv = cardCvv;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "uuid='" + uuid + '\'' +
                ", palmTemplate='" + palmTemplate + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", cardExpirationDate='" + cardExpirationDate + '\'' +
                ", cardCvv='" + cardCvv + '\'' +
                ", cardHolderName='" + cardHolderName + '\'' +
                ", cardType='" + cardType + '\'' +
                '}';
    }

    public static class UserEntry implements BaseColumns{
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME_UUID = "uuid";
        public static final String COLUMN_NAME_PALM_TEMPLATE= "palmTemplate";
        public static final String COLUMN_NAME_CARD_NUMBER = "cardNumber";
        public static final String COLUMN_NAME_CARD_EXPIRATION_DATE = "cardExpirationDate";
        public static final String COLUMN_NAME_CARD_CVV = "cardCvv";
        public static final String COLUMN_NAME_CARD_HOLDER_NAME = "cardHolderName";
        public static final String COLUMN_NAME_CARD_TYPE = "cardType";

 }
}
