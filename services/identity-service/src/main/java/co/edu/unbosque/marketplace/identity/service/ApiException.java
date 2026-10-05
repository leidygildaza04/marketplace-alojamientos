package co.edu.unbosque.marketplace.identity.service;

import org.springframework.http.HttpStatus;

/** Error de negocio con el estado HTTP y el código que debe ver el cliente. */
public class ApiException extends RuntimeException {

    private final transient HttpStatus status;
    private final String code;

    public ApiException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() { return status; }
    public String getCode() { return code; }
}
