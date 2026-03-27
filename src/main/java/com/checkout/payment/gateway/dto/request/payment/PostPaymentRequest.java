package com.checkout.payment.gateway.dto.request.payment;

import com.checkout.payment.gateway.enums.Currency;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record PostPaymentRequest(
		@NotBlank(message = "Card number must not be null") @Pattern(regexp = "\\d{14,19}", message = "Card number must be between 14 and 19 numeric digits") @JsonProperty("card_number") String cardNumber,
		@Min(value = 1, message = "Expiry month must be at least 1") @Max(value = 12 , message = "Expiry month at most can be 12") @JsonProperty("expiry_month") int expiryMonth, 
		@Positive(message = "Year must be a positive value") @JsonProperty("expiry_year") int expiryYear, //Is fully validated in CreateExpiryDate
		@NotBlank(message = "CVV must not be null")  @Pattern(regexp = "\\d{3,4}", message = "CVV must be between 3 and 4 numeric digits") String cvv,
		@Positive(message = "Amount must be a positive value") int amount, 
		@NotNull(message = "Currency must not be null") Currency currency) {

	@Override
	public String toString() {
		return "PostPaymentRequest{" + "cardNumber='" + cardNumber.substring(cardNumber.length() - 4) + "', expiryMonth=" + expiryMonth + ", expiryYear="
				+ expiryYear + ", currency='" + currency.getCode()  + "', amount=" + amount + '}';
	}
}
