package com.checkout.payment.gateway.mapper;

import java.util.UUID;

import com.checkout.payment.gateway.dto.request.payment.PostPaymentRequest;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.Payment;

public interface PaymentMapper {
	/**
	 * Maps a PostPaymentRequest to a Payment object, incorporating the payment status and unique identifier.
	 *
	 * @param postPaymentRequest the request containing payment details
	 * @param paymentStatus the status of the payment processing
	 * @param id the unique identifier for the payment
	 * @return a Payment object populated with the provided details
	 */
	Payment toPayment(PostPaymentRequest postPaymentRequest, PaymentStatus paymentStatus, UUID id);
}
