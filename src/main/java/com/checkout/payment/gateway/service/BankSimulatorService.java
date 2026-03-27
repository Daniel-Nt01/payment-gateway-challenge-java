package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.dto.request.bank.BankRequest;
import com.checkout.payment.gateway.dto.response.bank.BankResponse;

public interface BankSimulatorService {

	/**
	 * Simulates a call to the bank's API to process a payment.
	 *
	 * @param bankRequest the request containing the payment details to be authorized
	 * @return BankResponse indicating whether the payment was authorized and an authorization code if applicable
	 */
	BankResponse authorizePayment(BankRequest bankRequest);
}
