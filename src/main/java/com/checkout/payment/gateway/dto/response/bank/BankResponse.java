package com.checkout.payment.gateway.dto.response.bank;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BankResponse(boolean authorized, @JsonProperty("authorization_code") UUID authorizationCode) {
	@Override
	public String toString() {
		return "BankResponse{" + "authorized=" + authorized + '}';
	}
}
