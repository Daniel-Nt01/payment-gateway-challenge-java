package com.checkout.payment.gateway.mapper;

import com.checkout.payment.gateway.dto.request.bank.BankRequest;
import com.checkout.payment.gateway.dto.request.payment.PostPaymentRequest;

public interface BankRequestMapper {
	/**
	 * Converts a PostPaymentRequest object to a BankRequest object.
	 *
	 * @param postPaymentRequest the PostPaymentRequest to be converted
	 * @return the corresponding BankRequest object
	 * @throws IllegalArgumentException if the expiry date derived from the request is invalid or in the past
	 */
	BankRequest toBankRequest(PostPaymentRequest postPaymentRequest);
}
