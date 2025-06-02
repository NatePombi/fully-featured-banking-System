package model;

public enum AccountType {
    CHECKING,SAVINGS,BUSINESS;



    public static AccountType getEnum(String type){
        return switch (type.toUpperCase()){
            case "SAVINGS" ->AccountType.SAVINGS;
            case "BUSINESS" -> AccountType.BUSINESS;
            case "CHECKING" -> AccountType.CHECKING;
            case "" -> throw new IllegalArgumentException("Alert Field is empty: Account creation failed!");
            default -> throw new IllegalArgumentException("Invalid Type: Account creation failed!");
        };
    }
}
