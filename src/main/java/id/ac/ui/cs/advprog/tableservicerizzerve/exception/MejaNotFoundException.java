package id.ac.ui.cs.advprog.tableservicerizzerve.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MejaNotFoundException extends RuntimeException {
    public MejaNotFoundException() {
        super("Meja does not exist");
    }
}