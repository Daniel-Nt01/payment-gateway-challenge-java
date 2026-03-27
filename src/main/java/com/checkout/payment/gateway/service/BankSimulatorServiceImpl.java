package com.checkout.payment.gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.checkout.payment.gateway.dto.request.bank.BankRequest;
import com.checkout.payment.gateway.dto.response.bank.BankResponse;
import com.checkout.payment.gateway.utils.LastFourCardDigits;

@Service
public class BankSimulatorServiceImpl implements BankSimulatorService {

	private final RestTemplate restTemplate;

	private final String bankSimulatorUrl;

	private static final Logger LOG = LoggerFactory.getLogger(BankSimulatorServiceImpl.class);

	public BankSimulatorServiceImpl(RestTemplate restTemplate,
			@Value("${bank.simulator.url}") String bankSimulatorUrl) {
		this.restTemplate = restTemplate;
		this.bankSimulatorUrl = bankSimulatorUrl;
	}

	@Override
	public BankResponse authorizePayment(BankRequest bankRequest) {
		BankResponse bankResponse = null;
		String lastFourDigits = LastFourCardDigits.getLastFourDigits(bankRequest.cardNumber());
		
		try {
			bankResponse = restTemplate.postForObject(bankSimulatorUrl + "/payments", bankRequest, BankResponse.class);
			LOG.info("Bank simulator response for card ending {}: {}", lastFourDigits, bankResponse);
		} catch (HttpServerErrorException.ServiceUnavailable e) {
			LOG.error("Bank simulator service unavailable (503) for card ending {}: {}", lastFourDigits, e.getMessage());
			bankResponse = new BankResponse(false, null);
		} catch (Exception e) {		// Catch any other exceptions that may occur during the call to the bank simulator, such as network issues or unexpected errors
			LOG.error("Error calling bank simulator for card ending {}: {}", lastFourDigits, e.getMessage());
			bankResponse = new BankResponse(false, null);
		}
		
		return bankResponse;
	}

}
