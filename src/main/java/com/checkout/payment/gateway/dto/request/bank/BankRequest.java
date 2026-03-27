package com.checkout.payment.gateway.dto.request.bank;

import com.checkout.payment.gateway.enums.Currency;
import com.fasterxml.jackson.annotation.JsonProperty;

public record BankRequest(@JsonProperty("card_number") String cardNumber,
		@JsonProperty("expiry_date") String expiryDate, String cvv, int amount, Currency currency) {
	
	@Override
	public String toString() {
		return "BankRequest{" + "cardNumber='" + cardNumber.substring(cardNumber.length() - 4) + "', expiryDate='" + expiryDate
				+ "', currency='" + currency.getCode() + "', amount=" + amount + '}';
	}
}
