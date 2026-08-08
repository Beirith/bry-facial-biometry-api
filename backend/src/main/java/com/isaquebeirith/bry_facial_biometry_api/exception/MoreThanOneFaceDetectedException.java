package com.isaquebeirith.bry_facial_biometry_api.exception;

public class MoreThanOneFaceDetectedException extends RuntimeException {
    public MoreThanOneFaceDetectedException(String message) {
        super(message);
    }
}
