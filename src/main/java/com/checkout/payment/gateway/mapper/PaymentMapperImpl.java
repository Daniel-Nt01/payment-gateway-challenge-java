package com.checkout.payment.gateway.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.checkout.payment.gateway.dto.request.payment.PostPaymentRequest;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.utils.LastFourCardDigits;

@Component
public class PaymentMapperImpl implements PaymentMapper {

	@Override
	public Payment toPayment(PostPaymentRequest postPaymentRequest, PaymentStatus paymentStatus, UUID id) {
		return new Payment(id,
				paymentStatus,
				LastFourCardDigits.getLastFourDigits(postPaymentRequest.cardNumber()),
				postPaymentRequest.expiryMonth(), postPaymentRequest.expiryYear(), postPaymentRequest.currency(),
				postPaymentRequest.amount());
	}

}
