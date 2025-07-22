package com.nate.exception;

public class FailedToTransferFundsException extends RuntimeException {
    public FailedToTransferFundsException() {
        super("Failed to transfer fund, Transfer did not reflect");
    }
}
