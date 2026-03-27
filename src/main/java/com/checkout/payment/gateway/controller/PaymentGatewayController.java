package com.checkout.payment.gateway.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.checkout.payment.gateway.dto.request.payment.PostPaymentRequest;
import com.checkout.payment.gateway.dto.response.error.ErrorResponse;
import com.checkout.payment.gateway.dto.response.error.PaymentProcessingError;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.service.PaymentGatewayService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController()
@Tag(name = "Payment Gateway", description = "Endpoints for processing and retrieving payments")
public class PaymentGatewayController {

	private final PaymentGatewayService paymentGatewayService;

	public PaymentGatewayController(PaymentGatewayService paymentGatewayService) {
		this.paymentGatewayService = paymentGatewayService;
	}

	@Operation(summary = "Retrieve a payment by ID", description = "Returns the details of a previously processed payment")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Payment found", content = @Content(schema = @Schema(implementation = Payment.class))),
			@ApiResponse(responseCode = "404", description = "Payment not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@GetMapping("/payment/{id}")
	public ResponseEntity<Payment> getPostPaymentEventById(@PathVariable UUID id) {
		return new ResponseEntity<>(paymentGatewayService.getPaymentById(id), HttpStatus.OK);
	}

	@Operation(summary = "Process a payment", description = "Submits a payment request to the bank simulator and returns the result")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Payment processed successfully", content = @Content(schema = @Schema(implementation = Payment.class))),
			@ApiResponse(responseCode = "400", description = "Invalid request body - payment rejected", content = @Content(schema = @Schema(implementation = PaymentProcessingError.class))) })
	@PostMapping("/payment/process")
	public ResponseEntity<Payment> processPayment(@Valid @RequestBody PostPaymentRequest paymentRequest) {
		return new ResponseEntity<>(paymentGatewayService.processPayment(paymentRequest), HttpStatus.CREATED);
	}
}
