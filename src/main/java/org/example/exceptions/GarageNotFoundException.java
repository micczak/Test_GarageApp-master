package org.example.exceptions;

public class GarageNotFoundException extends RuntimeException {
    public GarageNotFoundException(String message) {
        super(message);
    }

    public GarageNotFoundException() {
        super();
    }
}
