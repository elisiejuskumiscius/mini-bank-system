package danskebank.mini_bank_system.exception;

public class CustomerException extends RuntimeException {
    public CustomerException(String message, Throwable err) {
        super(message, err);
    }
}
