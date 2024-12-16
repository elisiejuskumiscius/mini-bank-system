package danskebank.mini_bank_system.exception;

public class AccountException extends RuntimeException {
    public AccountException(String message, Throwable err) {
        super(message, err);
    }
}
