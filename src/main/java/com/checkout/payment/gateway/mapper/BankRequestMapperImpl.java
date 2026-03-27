package com.checkout.payment.gateway.mapper;

import org.springframework.stereotype.Component;

import com.checkout.payment.gateway.dto.request.bank.BankRequest;
import com.checkout.payment.gateway.dto.request.payment.PostPaymentRequest;
import com.checkout.payment.gateway.utils.CreateExpiryDate;

@Component
public class BankRequestMapperImpl implements BankRequestMapper {

	@Override
	public BankRequest toBankRequest(PostPaymentRequest postPaymentRequest) {
		return new BankRequest(postPaymentRequest.cardNumber(),
				CreateExpiryDate.getValidExpiryDateFromMonthAndYear(postPaymentRequest.expiryMonth(), postPaymentRequest.expiryYear()), postPaymentRequest.cvv(),
				postPaymentRequest.amount(), postPaymentRequest.currency());
	}

}
