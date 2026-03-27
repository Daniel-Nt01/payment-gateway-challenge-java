package com.checkout.payment.gateway.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Currency {
	GBP("GBP"),
	USD("USD"),
	EUR("EUR");
	
	private final String code;
	
	Currency(String code) {
		this.code = code;
	}
	
	@JsonValue
	public String getCode() {
		return code;
	}
}
