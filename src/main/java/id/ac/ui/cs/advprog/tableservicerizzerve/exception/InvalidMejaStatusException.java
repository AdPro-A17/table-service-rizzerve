package id.ac.ui.cs.advprog.tableservicerizzerve.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidMejaStatusException extends RuntimeException {
    public InvalidMejaStatusException(String status) {
        super("Invalid Meja status: " + status);
    }
}