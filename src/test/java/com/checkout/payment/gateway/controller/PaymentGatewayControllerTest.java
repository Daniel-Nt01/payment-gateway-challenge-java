package com.checkout.payment.gateway.controller;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServiceUnavailable;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Year;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;

import com.checkout.payment.gateway.enums.Currency;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.repository.PaymentsRepository;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentGatewayControllerTest {

	@Autowired
	private MockMvc mvc;
	@Autowired
	PaymentsRepository paymentsRepository;
	
	@Autowired
	private RestTemplate restTemplate;

	@Value("${bank.simulator.url}")
	private String bankSimulatorUrl;

	@Test
	void whenPaymentWithIdExistThenCorrectPaymentIsReturned() throws Exception {
		int year = Year.now().getValue() + 1;
		Payment payment = new Payment();
		payment.setId(UUID.randomUUID());
		payment.setAmount(10);
		payment.setCurrency(Currency.USD);
		payment.setStatus(PaymentStatus.AUTHORIZED);
		payment.setExpiryMonth(12);
		payment.setExpiryYear(year);
		payment.setCardNumberLastFour("4321");

		paymentsRepository.add(payment);

		mvc.perform(MockMvcRequestBuilders.get("/payment/" + payment.getId())).andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value(payment.getStatus().getName()))
				.andExpect(jsonPath("$.cardNumberLastFour").value(payment.getCardNumberLastFour()))
				.andExpect(jsonPath("$.expiryMonth").value(payment.getExpiryMonth()))
				.andExpect(jsonPath("$.expiryYear").value(payment.getExpiryYear()))
				.andExpect(jsonPath("$.currency").value(payment.getCurrency().getCode()))
				.andExpect(jsonPath("$.amount").value(payment.getAmount()));
	}

	@Test
	void whenPaymentWithIdDoesNotExistThen404IsReturned() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/payment/" + UUID.randomUUID())).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Payment not found"));
	}
	
	@Test
	void testProcessPaymentWithInvalidCardNumber() throws Exception {
		int year = Year.now().getValue() + 1;
		String requestBody = String.format(
				"{\"card_number\": \"1234567890123456A\", \"expiry_month\": 12, \"expiry_year\": %d, \"cvv\": \"123\", \"amount\": 100, \"currency\": \"USD\"}",
				year);
		PaymentStatus expectedStatus = PaymentStatus.REJECTED;

		mvc.perform(MockMvcRequestBuilders.post("/payment/process").contentType("application/json").content(requestBody))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Invalid request body"))
				.andExpect(jsonPath("$.status").value(expectedStatus.getName()));
	}
	
	@Test
	void testProcessPaymentWithShortCardNumber() throws Exception {
		int year = Year.now().getValue() + 1;
		String requestBody = String.format(
				"{\"card_number\": \"411111111111\", \"expiry_month\": 12, \"expiry_year\": %d, \"cvv\": \"123\", \"amount\": 100, \"currency\": \"USD\"}",
				year);
		PaymentStatus expectedStatus = PaymentStatus.REJECTED;

		mvc.perform(MockMvcRequestBuilders.post("/payment/process").contentType("application/json").content(requestBody))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Invalid request body"))
				.andExpect(jsonPath("$.status").value(expectedStatus.getName()));
	}
	
	@Test
	void testProcessPaymentWithInvalidMonth() throws Exception {
		int year = Year.now().getValue() + 1;
		String requestBody = String.format(
				"{\"card_number\": \"4111111111111111\", \"expiry_month\": 13, \"expiry_year\": %d, \"cvv\": \"123\", \"amount\": 100, \"currency\": \"USD\"}",
				year);
		PaymentStatus expectedStatus = PaymentStatus.REJECTED;

		mvc.perform(MockMvcRequestBuilders.post("/payment/process").contentType("application/json").content(requestBody))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Invalid request body"))
				.andExpect(jsonPath("$.status").value(expectedStatus.getName()));
	}
	
	@Test
	void testProcessPaymentWithInvalidYear() throws Exception {
		int year = -8;
		String requestBody = String.format(
				"{\"card_number\": \"4111111111111111\", \"expiry_month\": 12, \"expiry_year\": %d, \"cvv\": \"123\", \"amount\": 100, \"currency\": \"USD\"}",
				year);
		PaymentStatus expectedStatus = PaymentStatus.REJECTED;
		
		mvc.perform(MockMvcRequestBuilders.post("/payment/process").contentType("application/json").content(requestBody))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value("Invalid request body"))
		.andExpect(jsonPath("$.status").value(expectedStatus.getName()));
	}
	
	@Test
	void testProcessPaymentWithExpiredCard() throws Exception {
		int year = Year.now().getValue() - 1;
		String requestBody = String.format(
				"{\"card_number\": \"4111111111111111\", \"expiry_month\": 12, \"expiry_year\": %d, \"cvv\": \"123\", \"amount\": 100, \"currency\": \"USD\"}",
				year);
		PaymentStatus expectedStatus = PaymentStatus.REJECTED;
		
		mvc.perform(MockMvcRequestBuilders.post("/payment/process").contentType("application/json").content(requestBody))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value("Invalid request body"))
		.andExpect(jsonPath("$.status").value(expectedStatus.getName()));
	}
	
	@Test
	void testProcessPaymentWithInvalidCVV() throws Exception {
		int year = Year.now().getValue() + 1;
		String requestBody = String.format(
				"{\"card_number\": \"4111111111111111\", \"expiry_month\": 12, \"expiry_year\": %d, \"cvv\": \"12A\", \"amount\": 100, \"currency\": \"USD\"}",
				year);
		PaymentStatus expectedStatus = PaymentStatus.REJECTED;

		mvc.perform(MockMvcRequestBuilders.post("/payment/process").contentType("application/json").content(requestBody))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Invalid request body"))
				.andExpect(jsonPath("$.status").value(expectedStatus.getName()));
	}
	
	@Test
	void testProcessPaymentWithInvalidCVVLength() throws Exception {
		int year = Year.now().getValue() + 1;
		String requestBody = String.format(
				"{\"card_number\": \"4111111111111111\", \"expiry_month\": 12, \"expiry_year\": %d, \"cvv\": \"12345\", \"amount\": 100, \"currency\": \"USD\"}",
				year);
		PaymentStatus expectedStatus = PaymentStatus.REJECTED;

		mvc.perform(MockMvcRequestBuilders.post("/payment/process").contentType("application/json").content(requestBody))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Invalid request body"))
				.andExpect(jsonPath("$.status").value(expectedStatus.getName()));
	}
	
	@Test
	void testProcessPaymentWithNegativeAmount() throws Exception {
		int year = Year.now().getValue() + 1;
		String requestBody = String.format(
				"{\"card_number\": \"4111111111111111\", \"expiry_month\": 12, \"expiry_year\": %d, \"cvv\": \"123\", \"amount\": -100, \"currency\": \"USD\"}",
				year);
		PaymentStatus expectedStatus = PaymentStatus.REJECTED;

		mvc.perform(MockMvcRequestBuilders.post("/payment/process").contentType("application/json").content(requestBody))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Invalid request body"))
				.andExpect(jsonPath("$.status").value(expectedStatus.getName()));
	}
	
	@Test
	void testProcessPaymentWithUnsupportedCurrency() throws Exception {
		int year = Year.now().getValue() + 1;
		String requestBody = String.format(
				"{\"card_number\": \"4111111111111111\", \"expiry_month\": 12, \"expiry_year\": %d, \"cvv\": \"123\", \"amount\": 100, \"currency\": \"ABC\"}",
				year);
		PaymentStatus expectedStatus = PaymentStatus.REJECTED;

		mvc.perform(MockMvcRequestBuilders.post("/payment/process").contentType("application/json").content(requestBody))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Invalid request body"))
				.andExpect(jsonPath("$.status").value(expectedStatus.getName()));
	}
	
	@Test
	void testProcessPaymentWithMissingFields() throws Exception {
		String requestBody = "{}";
		PaymentStatus expectedStatus = PaymentStatus.REJECTED;

		mvc.perform(MockMvcRequestBuilders.post("/payment/process").contentType("application/json").content(requestBody))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Invalid request body"))
				.andExpect(jsonPath("$.status").value(expectedStatus.getName()));
	}
	
	@Test
	void testProcessPaymentWithCardNumberEndingWith1IsAuthorized() throws Exception {
		int year = Year.now().getValue() + 1;
		String requestBody = String.format(
				"{\"card_number\": \"4111111111111111\", \"expiry_month\": 12, \"expiry_year\": %d, \"cvv\": \"123\", \"amount\": 100, \"currency\": \"USD\"}",
				year);
				
		MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
		
		mockServer.expect(requestTo(bankSimulatorUrl + "/payments"))
	    .andExpect(method(HttpMethod.POST))
	    .andRespond(withSuccess(
	        "{\"authorized\": true, \"authorization_code\": \"0bb07405-6d44-4b50-a14f-7ae0beff13ad\"}", 
	        MediaType.APPLICATION_JSON));


		mvc.perform(MockMvcRequestBuilders.post("/payment/process").contentType("application/json").content(requestBody))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.status").value(PaymentStatus.AUTHORIZED.getName()))
				.andExpect(jsonPath("$.cardNumberLastFour").value("1111"))
				.andExpect(jsonPath("$.expiryMonth").value(12))
				.andExpect(jsonPath("$.expiryYear").value(year))
				.andExpect(jsonPath("$.currency").value("USD"))
				.andExpect(jsonPath("$.amount").value(100));
	}
	
	@Test
	void testProcessPaymentWithCardNumberEndingWith2IsDeclined() throws Exception {
		int year = Year.now().getValue() + 1;
		String requestBody = String.format(
				"{\"card_number\": \"4111111111111112\", \"expiry_month\": 12, \"expiry_year\": %d, \"cvv\": \"123\", \"amount\": 100, \"currency\": \"USD\"}",
				year);
				
		MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
		
		mockServer.expect(requestTo(bankSimulatorUrl + "/payments"))
	    .andExpect(method(HttpMethod.POST))
	    .andRespond(withSuccess(
	        "{\"authorized\": false, \"authorization_code\": null}", 
	        MediaType.APPLICATION_JSON));
		
		mvc.perform(MockMvcRequestBuilders.post("/payment/process").contentType("application/json").content(requestBody))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.status").value(PaymentStatus.DECLINED.getName()))
				.andExpect(jsonPath("$.cardNumberLastFour").value("1112"))
				.andExpect(jsonPath("$.expiryMonth").value(12))
				.andExpect(jsonPath("$.expiryYear").value(year))
				.andExpect(jsonPath("$.currency").value("USD"))
				.andExpect(jsonPath("$.amount").value(100));
	}
	
	@Test
	void testProcessPaymentWithCardNumberEndingWith0IsDeclinedDueToBankSimulatorError() throws Exception {
		int year = Year.now().getValue() + 1;
		String requestBody = String.format(
				"{\"card_number\": \"4111111111111110\", \"expiry_month\": 12, \"expiry_year\": %d, \"cvv\": \"123\", \"amount\": 100, \"currency\": \"USD\"}",
				year);
				
		MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
		
		mockServer.expect(requestTo(bankSimulatorUrl + "/payments"))
	    .andExpect(method(HttpMethod.POST))
	    .andRespond(withServiceUnavailable());
		
		mvc.perform(MockMvcRequestBuilders.post("/payment/process").contentType("application/json").content(requestBody))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.status").value(PaymentStatus.DECLINED.getName()))
				.andExpect(jsonPath("$.cardNumberLastFour").value("1110"))
				.andExpect(jsonPath("$.expiryMonth").value(12))
				.andExpect(jsonPath("$.expiryYear").value(year))
				.andExpect(jsonPath("$.currency").value("USD"))
				.andExpect(jsonPath("$.amount").value(100));
	}
}
