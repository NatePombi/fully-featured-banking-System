package com.nate.exception;

public class TransferToSameAccountException extends RuntimeException {
    public TransferToSameAccountException() {
        super("Failed: You're Transferring to the same account");
    }
}
