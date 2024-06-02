package com.lacus.utils.jackson;

public class JacksonException extends RuntimeException {

    public JacksonException() {
        super();
    }

    public JacksonException(String message) {
        super(message);
    }

    public JacksonException(String message, Exception e) {
        super(message, e);
    }

    public JacksonException(Throwable cause) {
        super(cause);
    }

}
