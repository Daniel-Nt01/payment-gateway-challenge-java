package com.checkout.payment.gateway.exception;

public class PaymentNotFoundException extends RuntimeException{
	
  private static final long serialVersionUID = 1L;

  public PaymentNotFoundException(String message) {
    super(message);
  }
}
