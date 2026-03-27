package com.checkout.payment.gateway.service;

import java.util.UUID;

import com.checkout.payment.gateway.dto.request.payment.PostPaymentRequest;
import com.checkout.payment.gateway.model.Payment;

public interface PaymentGatewayService {
	/**
	 * Retrieves a payment by its unique identifier.
	 *
	 * @param id the unique identifier of the payment
	 * @return the Payment object associated with the given id
	 * @throws PaymentNotFoundException if no payment exists with the given id
	 */
	Payment getPaymentById(UUID id);
	
	/**
	 * Processes a payment based on the provided payment request.
	 *
	 * @param postPaymentRequest the request containing payment details
	 * @return the Payment object representing the result of the payment processing
	 */
	Payment processPayment(PostPaymentRequest postPaymentRequest);
}
