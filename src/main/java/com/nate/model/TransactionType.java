package com.nate.model;

public enum TransactionType {
    DEPOSIT,WITHDRAW,TRANSFER;

    public static TransactionType getEnum(String type){
        return switch (type.toLowerCase()){
            case "deposit"-> TransactionType.DEPOSIT;
            case "withdraw"-> TransactionType.WITHDRAW;
            case "transfer" -> TransactionType.TRANSFER;
            default -> throw new IllegalArgumentException("Invalid Transaction Type");
        };
    }
}
