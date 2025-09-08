package com.kunfeng2002.be002.exception;

public class InvalidWalletAddressException extends RuntimeException {
    public InvalidWalletAddressException(String message) {
        super(message);
    }

    public InvalidWalletAddressException(String message, Throwable cause) {
        super(message, cause);
    }
}
