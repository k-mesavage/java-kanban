package service.exceptions;

public class StatusCodeException extends RuntimeException {

    public StatusCodeException(String message) {
        super(message);
    }
}