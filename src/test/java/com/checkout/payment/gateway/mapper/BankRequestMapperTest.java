package com.checkout.payment.gateway.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Year;

import org.junit.Test;

import com.checkout.payment.gateway.dto.request.bank.BankRequest;
import com.checkout.payment.gateway.dto.request.payment.PostPaymentRequest;
import com.checkout.payment.gateway.enums.Currency;

public class BankRequestMapperTest {
	
	BankRequestMapper bankRequestMapper = new BankRequestMapperImpl();
	
	@Test
	public void testToBankRequest() {
		int year = Year.now().getValue() + 1;
		PostPaymentRequest postPaymentRequest = new PostPaymentRequest("4111111111111111", 12, year, "123", 100, Currency.USD);
		
		BankRequest bankRequest = bankRequestMapper.toBankRequest(postPaymentRequest);
		
		assertEquals("4111111111111111", bankRequest.cardNumber());
		assertEquals("12/" + year, bankRequest.expiryDate());
		assertEquals("123", bankRequest.cvv());
		assertEquals(100, bankRequest.amount());
		assertEquals(Currency.USD, bankRequest.currency());
	}
	
	@Test
	public void testToBankRequestWithDifferentValues() {
		int year = Year.now().getValue() + 2;
		PostPaymentRequest postPaymentRequest = new PostPaymentRequest("4111111111111221", 10, year, "123", 20000, Currency.EUR);
		
		BankRequest bankRequest = bankRequestMapper.toBankRequest(postPaymentRequest);
		
		assertEquals("4111111111111221", bankRequest.cardNumber());
		assertEquals("10/" + year, bankRequest.expiryDate());
		assertEquals("123", bankRequest.cvv());
		assertEquals(20000, bankRequest.amount());
		assertEquals(Currency.EUR, bankRequest.currency());
	}
	
	@Test
	public void testToBankRequestWithSingleDigitMonth() {
		int year = Year.now().getValue() + 1;
		PostPaymentRequest postPaymentRequest = new PostPaymentRequest("4111111111111111", 5, year, "123", 100, Currency.USD);
		
		BankRequest bankRequest = bankRequestMapper.toBankRequest(postPaymentRequest);
		
		assertEquals("4111111111111111", bankRequest.cardNumber());
		assertEquals("05/" + year, bankRequest.expiryDate());
		assertEquals("123", bankRequest.cvv());
		assertEquals(100, bankRequest.amount());
		assertEquals(Currency.USD, bankRequest.currency());
	}
	
	
}
