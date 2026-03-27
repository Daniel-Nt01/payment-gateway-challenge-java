package com.checkout.payment.gateway.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Year;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServiceUnavailable;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.checkout.payment.gateway.dto.request.bank.BankRequest;
import com.checkout.payment.gateway.dto.response.bank.BankResponse;
import com.checkout.payment.gateway.enums.Currency;

@SpringBootTest
@AutoConfigureMockMvc
class BankSimulatorServiceTest {

    @Autowired
    private BankSimulatorService bankSimulatorService;

    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${bank.simulator.url}")
    private String bankSimulatorUrl;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }
    
    @Test
    void whenBankSimulatorReturnsAuthorizedThenPaymentIsAuthorized() {
		String cardNumber = "4111111111111111";
		int year = Year.now().getValue() + 1;
		BankRequest bankRequest = new BankRequest(cardNumber, "12/" + year, "123", 100, Currency.USD);
		
		mockServer.expect(requestTo(bankSimulatorUrl + "/payments"))
	    .andExpect(method(HttpMethod.POST))
	    .andRespond(withSuccess(
	        "{\"authorized\": true, \"authorization_code\": \"0bb07405-6d44-4b50-a14f-7ae0beff13ad\"}", 
	        MediaType.APPLICATION_JSON));

		BankResponse bankResponse = bankSimulatorService.authorizePayment(bankRequest);

		assertTrue(bankResponse.authorized());
	}
    
    @Test
	void whenBankSimulatorReturnsDeclinedThenPaymentIsDeclined() {
		String cardNumber = "4111111111111112";
		int year = Year.now().getValue() + 1;
		BankRequest bankRequest = new BankRequest(cardNumber, "12/" + year, "123", 100, Currency.USD);
		
		mockServer.expect(requestTo(bankSimulatorUrl + "/payments"))
	    .andExpect(method(HttpMethod.POST))
	    .andRespond(withSuccess(
		        "{\"authorized\": false, \"authorization_code\": null}",
		        MediaType.APPLICATION_JSON));

		BankResponse bankResponse = bankSimulatorService.authorizePayment(bankRequest);

		assertFalse(bankResponse.authorized());
	}
    
    @Test
    void whenBankSimulatorReturnsServiceUnavailableThenPaymentIsDeclined() {
    	String cardNumber = "4111111111111110";
    	int year = Year.now().getValue() + 1;
    	BankRequest bankRequest = new BankRequest(cardNumber, "12/" + year, "123", 100, Currency.USD);
    	
    	mockServer.expect(requestTo(bankSimulatorUrl + "/payments"))
    	.andExpect(method(HttpMethod.POST))
    	.andRespond(withServiceUnavailable());
    	
    	BankResponse bankResponse = bankSimulatorService.authorizePayment(bankRequest);
    	
    	assertFalse(bankResponse.authorized());
    	assertNull(bankResponse.authorizationCode());
    }
}