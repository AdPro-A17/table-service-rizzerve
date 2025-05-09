package id.ac.ui.cs.advprog.tableservicerizzerve.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidNomorMejaException extends RuntimeException {
    public InvalidNomorMejaException() {
        super("nomorMeja must be >= 1");
    }
}