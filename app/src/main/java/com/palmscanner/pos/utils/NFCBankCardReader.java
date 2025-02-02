package com.palmscanner.pos.utils;

import android.text.TextUtils;
import android.util.Log;

import com.jiebao.nfc.uartnfc.CardReaderDevice;
import com.jiebao.util.CardReaderUtils;

public class NFCBankCardReader {

    private static final String TAG = "NFCBankCardReader";

    // EMV Payment System Environment Selection
    private static final String SELECT_PSE = "00A404000E325041592E5359532E444446303100"; // 2PAY.SYS.DDF01

    // List of known AIDs for different card networks
    private static final String[] EMV_AIDS = {"A0000000031010", // Visa
            "A0000000041010", // Mastercard
            "A00000002501",   // American Express
            "A0000001523010", // Discover
            "A0000000651010", // JCB
            "A000000333010101" // China UnionPay
    };

    private static final CardReaderDevice cardReader = CardReaderDevice.getInstance();

    public static void initCardReader() {
        cardReader.initCardReader();
    }
    public static CardReaderDevice getCardReader(){
        return cardReader;
    }

    public static String getCardType(){
        String cardType = cardReader.getCardType();
        if(TextUtils.isEmpty(cardType)){
            return "Unknown";
        }else {
            return cardType;
        }
    }
    public static Result readBankCard() {
        // Step 1: Reset the CPU card (check if an NFC card is present)
        if (cardReader.resetCpuCard() == null) {
            return new Result(false, "Card Reset Failed", null);
        }

        // Step 2: Select Payment System Environment (PSE)
        String[] response = cardReader.CardCpuSendCosCmd(CardReaderUtils.hexToByteArr(SELECT_PSE));
        if (response == null || response.length != 2 || !response[0].equals("9000")) {
            return new Result(false, "PSE Not Found", null);
        }

        // Step 3: Find and Select the Correct AID (Visa, Mastercard, etc.)
        String aid = findCardAID(response[1]);
        if (TextUtils.isEmpty(aid)) {
            return new Result(false, "No Recognized EMV Application Found", null);
        }
        Log.d(TAG, "Selected AID: " + aid);

        // Step 4: Select the EMV Application
        String[] aidResponse = cardReader.CardCpuSendCosCmd(CardReaderUtils.hexToByteArr("00A4040007" + aid));
        if (aidResponse == null || aidResponse.length != 2 || !"9000".equals(aidResponse[0])) {
            return new Result(false, "Failed to Select EMV Application", null);
        }

        // Step 5: Get Processing Options (GPO)
        String[] gpoResponse = cardReader.CardCpuSendCosCmd(CardReaderUtils.hexToByteArr("80A80000028300"));
        if (gpoResponse == null || gpoResponse.length != 2 || !"9000".equals(gpoResponse[0])) {
            return new Result(false, "Failed to Get Processing Options", null);
        }

        // Step 6: Read Card Number (PAN) and Expiry Date
        String[] panResponse = cardReader.CardCpuSendCosCmd(CardReaderUtils.hexToByteArr("00B2010C00")); // Read Record command
        if (panResponse == null || panResponse.length != 2 || !"9000".equals(panResponse[0])) {
            return new Result(false, "Failed to Read Card Data", null);
        }

        String cardNumber = extractPAN(panResponse[1]);
        String expiryDate = extractExpiryDate(panResponse[1]);
        String cardData = "Card Number: " + cardNumber + "\nExpiry: " + expiryDate;

        return new Result(true, "Card Read Successfully", cardData);
    }

    public static void deInitCardReader() {
        cardReader.deInitCardReader();
    }

    /**
     * Extracts the AID from the EMV response.
     */
    private static String findCardAID(String emvData) {
        for (String aid : EMV_AIDS) {
            if (emvData.contains(aid)) {
                return aid;
            }
        }
        return null;
    }

    /**
     * Extracts the PAN (Card Number) from the EMV response.
     */
    private static String extractPAN(String emvData) {
        int index = emvData.indexOf("5A"); // 5A = Tag for PAN in EMV data
        if (index != -1 && emvData.length() > index + 16) {
            return emvData.substring(index + 4, index + 20); // Extract PAN
        }
        return "Unknown PAN";
    }

    /**
     * Extracts the Expiry Date (YYMM) from the EMV response.
     */
    private static String extractExpiryDate(String emvData) {
        int index = emvData.indexOf("5F24"); // 5F24 = Tag for Expiry Date
        if (index != -1 && emvData.length() > index + 8) {
            return emvData.substring(index + 4, index + 8); // Extract Expiry Date
        }
        return "Unknown Expiry";
    }

    /**
     * Result Class to Wrap NFC Card Data
     */
    public static class Result {
        private final boolean status;
        private final String message;
        private final String cardData;

        public Result(boolean status, String message, String cardData) {
            this.status = status;
            this.message = message;
            this.cardData = cardData;
        }

        public boolean isStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public String getCardData() {
            return cardData;
        }

        @Override
        public String toString() {
            return "Status: " + (status ? "Success" : "Failed") + "\nMessage: " + message + "\nCard Data: " + (cardData != null ? cardData : "N/A");
        }
    }
}
