package com.checkout.payment.gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;

import java.time.Year;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.checkout.payment.gateway.dto.request.bank.BankRequest;
import com.checkout.payment.gateway.dto.request.payment.PostPaymentRequest;
import com.checkout.payment.gateway.dto.response.bank.BankResponse;
import com.checkout.payment.gateway.enums.Currency;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.mapper.BankRequestMapper;
import com.checkout.payment.gateway.mapper.PaymentMapper;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.repository.PaymentsRepository;

@ExtendWith(MockitoExtension.class)
public class PaymentGatewayServiceTest {

	@Mock
	private PaymentsRepository paymentsRepository;
	@Mock
	private BankSimulatorService bankSimulatorService;
	@Mock
	private BankRequestMapper bankRequestMapper;
	@Mock
	private PaymentMapper paymentMapper;

	@InjectMocks
	private PaymentGatewayServiceImpl paymentGatewayService;

	@Test
	public void testProcessPaymentForAuthorizedPayment() {
		int year = Year.now().getValue() + 1;
		PostPaymentRequest postPaymentRequest = new PostPaymentRequest("4111111111111111", 12, year, "123", 100, Currency.USD);
		BankRequest bankRequest = new BankRequest("4111111111111111", "12/" + year, "123", 100, Currency.USD);
		BankResponse bankResponse = new BankResponse(true, UUID.randomUUID());
		PaymentStatus paymentStatus = PaymentStatus.AUTHORIZED;
		UUID paymentId = UUID.randomUUID();
		Payment payment = new Payment(paymentId, paymentStatus, "1111", 12, year, Currency.USD, 100);

		when(bankRequestMapper.toBankRequest(postPaymentRequest)).thenReturn(bankRequest);
		when(bankSimulatorService.authorizePayment(bankRequest)).thenReturn(bankResponse);
		when(paymentMapper.toPayment(any(PostPaymentRequest.class), eq(paymentStatus), any(UUID.class))).thenReturn(payment);

		Payment result = paymentGatewayService.processPayment(postPaymentRequest);

		verify(paymentsRepository).add(any(Payment.class));
		
		assertEquals(payment, result);
	}
	
	@Test
	public void testProcessPaymentForDeclinedPayment() {
		int year = Year.now().getValue() + 1;
		PostPaymentRequest postPaymentRequest = new PostPaymentRequest("4111111111111112", 12, year, "123", 100, Currency.USD);
		BankRequest bankRequest = new BankRequest("4111111111111112", "12/" + year, "123", 100, Currency.USD);
		BankResponse bankResponse = new BankResponse(false, null);
		PaymentStatus paymentStatus = PaymentStatus.DECLINED;
		UUID paymentId = UUID.randomUUID();
		Payment payment = new Payment(paymentId, paymentStatus, "1112", 12, year, Currency.USD, 100);

		when(bankRequestMapper.toBankRequest(postPaymentRequest)).thenReturn(bankRequest);
		when(bankSimulatorService.authorizePayment(bankRequest)).thenReturn(bankResponse);
		when(paymentMapper.toPayment(any(PostPaymentRequest.class), eq(paymentStatus), any(UUID.class))).thenReturn(payment);

		Payment result = paymentGatewayService.processPayment(postPaymentRequest);

		verify(paymentsRepository).add(any(Payment.class));
		
		assertEquals(payment, result);
	}
}
