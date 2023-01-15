package com.edicom.webservice.rest.restfulws.exception;

import java.time.LocalDateTime;

//Esta es la estructura en la que queremos que se escriban nuestras excepciones
public class ErrorDetails {

    private LocalDateTime timeStamp;
    private String clientError;
    private String details;

    public ErrorDetails(LocalDateTime timeStamp, String clientError, String details) {
        this.timeStamp = timeStamp;
        this.clientError = clientError;
        this.details = details;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public String getClientError() {
        return clientError;
    }

    public String getDetails() {
        return details;
    }
}
