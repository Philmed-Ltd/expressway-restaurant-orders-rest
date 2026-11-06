package io.mpesapayment.exception;

public class PaymentException extends Exception{
    public PaymentException(Throwable cause) {
        super(cause);
    }
    public PaymentException(String message) {
        super(message);
    }
}
