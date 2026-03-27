package com.checkout.payment.gateway.utils;

public class LastFourCardDigits {

	public static String getLastFourDigits(String cardNumber) {
		if (cardNumber == null) {
			throw new IllegalArgumentException("Card number must not be null");
		}
		
		if (!cardNumber.matches("\\d+")) {
			throw new IllegalArgumentException("Card number should only be numeric characters");
		}
		
		if (cardNumber.length() < 14) {
			throw new IllegalArgumentException("Card number must be at least 14 digits long");
		}
		
		return cardNumber.substring(cardNumber.length() - 4);
	}
}
