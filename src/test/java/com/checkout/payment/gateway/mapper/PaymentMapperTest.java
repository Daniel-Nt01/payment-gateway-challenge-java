package com.checkout.payment.gateway.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Year;
import java.util.UUID;

import org.junit.Test;

import com.checkout.payment.gateway.dto.request.payment.PostPaymentRequest;
import com.checkout.payment.gateway.enums.Currency;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.Payment;

public class PaymentMapperTest {

	PaymentMapper paymentMapper = new PaymentMapperImpl();
	
	@Test
	public void testToPayment() {
		int year = Year.now().plusYears(1).getValue();
		PostPaymentRequest postPaymentRequest = new PostPaymentRequest("1234567890123456", 12, year, "123", 1000, Currency.USD);
		UUID id = UUID.randomUUID();
		
		Payment payment = paymentMapper.toPayment(postPaymentRequest, PaymentStatus.AUTHORIZED, id);
		
		assertEquals(id, payment.getId());
		assertEquals(PaymentStatus.AUTHORIZED, payment.getStatus());
		assertEquals("3456", payment.getCardNumberLastFour());
		assertEquals(12, payment.getExpiryMonth());
		assertEquals(year, payment.getExpiryYear());
		assertEquals(Currency.USD, payment.getCurrency());
		assertEquals(1000, payment.getAmount());
	}
	
	@Test
	public void testToPaymentWithDifferentValues() {
		int year = Year.now().plusYears(2).getValue();
		PostPaymentRequest postPaymentRequest = new PostPaymentRequest("9876543210987659", 10, year, "456", 2000, Currency.EUR);
		UUID id = UUID.randomUUID();
		
		Payment payment = paymentMapper.toPayment(postPaymentRequest, PaymentStatus.DECLINED, id);
		
		assertEquals(id, payment.getId());
		assertEquals(PaymentStatus.DECLINED, payment.getStatus());
		assertEquals("7659", payment.getCardNumberLastFour());
		assertEquals(10, payment.getExpiryMonth());
		assertEquals(year, payment.getExpiryYear());
		assertEquals(Currency.EUR, payment.getCurrency());
		assertEquals(2000, payment.getAmount());
	}
}
