package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.dto.request.bank.BankRequest;
import com.checkout.payment.gateway.dto.request.payment.PostPaymentRequest;
import com.checkout.payment.gateway.dto.response.bank.BankResponse;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.PaymentNotFoundException;
import com.checkout.payment.gateway.mapper.BankRequestMapper;
import com.checkout.payment.gateway.mapper.PaymentMapper;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentGatewayServiceImpl implements PaymentGatewayService {

	private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayServiceImpl.class);

	private final PaymentsRepository paymentsRepository;
	private final BankSimulatorService bankSimulatorService;
	private final BankRequestMapper bankRequestMapper;
	private final PaymentMapper paymentMapper;

	public PaymentGatewayServiceImpl(PaymentsRepository paymentsRepository, BankSimulatorService bankSimulatorService,
			BankRequestMapper bankRequestMapper, PaymentMapper paymentMapper) {
		this.paymentsRepository = paymentsRepository;
		this.bankSimulatorService = bankSimulatorService;
		this.bankRequestMapper = bankRequestMapper;
		this.paymentMapper = paymentMapper;
	}

	@Override
	public Payment getPaymentById(UUID id) {
		LOG.debug("Requesting access to payment with ID {}", id);
		return paymentsRepository.get(id).orElseThrow(() -> new PaymentNotFoundException("Invalid ID"));
	}

	@Override
	public Payment processPayment(PostPaymentRequest paymentRequest) {
		LOG.debug("Processing payment request: {}", paymentRequest);
		
		BankRequest bankRequest = bankRequestMapper.toBankRequest(paymentRequest);
		BankResponse bankResponse = bankSimulatorService.authorizePayment(bankRequest);
		PaymentStatus paymentStatus = bankResponse.authorized() ? PaymentStatus.AUTHORIZED : PaymentStatus.DECLINED;
		Payment payment = paymentMapper.toPayment(paymentRequest, paymentStatus, UUID.randomUUID());
		paymentsRepository.add(payment);
		
		LOG.info("Processed payment request for card ending with {}: {}", payment.getCardNumberLastFour(), paymentStatus);
		return payment;
	}
}
