package com.checkout.payment.gateway.repository;

import java.util.Optional;
import java.util.UUID;

import com.checkout.payment.gateway.model.Payment;

public interface PaymentsRepository {

	/**
	 * Adds a new payment to the repository.
	 *
	 * @param payment the Payment object to be added
	 */
	void add(Payment payment);
	
	/**
	 * Retrieves a payment by its unique identifier.
	 *
	 * @param id the unique identifier of the payment
	 * @return an Optional containing the Payment object if found, or empty if not found
	 */
	Optional<Payment> get(UUID id);
}
