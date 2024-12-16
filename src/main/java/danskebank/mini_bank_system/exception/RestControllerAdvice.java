package danskebank.mini_bank_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(annotations = RestController.class)
public class RestControllerAdvice {

    @ExceptionHandler(CustomerException.class)
    public ResponseEntity<ErrorResponse> customerException(CustomerException customerException) {
        var response = new ErrorResponse("Customer exception", customerException.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AddressException.class)
    public ResponseEntity<ErrorResponse> addressException(AddressException addressException) {
        var response = new ErrorResponse("Address exception", addressException.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountException.class)
    public ResponseEntity<ErrorResponse> accountException(AccountException accountException) {
        var response = new ErrorResponse("Account exception", accountException.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}
