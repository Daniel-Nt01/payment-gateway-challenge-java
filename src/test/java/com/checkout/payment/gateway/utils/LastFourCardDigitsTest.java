package com.checkout.payment.gateway.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.Test;

public class LastFourCardDigitsTest {

	@Test
	public void testGetLastFourDigits() {
		String cardNumber = "1234567890123456";
		String expectedLast4Digits = "3456";

		String actualLast4Digits = LastFourCardDigits.getLastFourDigits(cardNumber);

		assertEquals(expectedLast4Digits, actualLast4Digits);
	}
	
	@Test
	public void testGetLastFourDigitsForAnotherCardNumber() {
		String cardNumber = "9876543210987654";
		String expectedLast4Digits = "7654";

		String actualLast4Digits = LastFourCardDigits.getLastFourDigits(cardNumber);

		assertEquals(expectedLast4Digits, actualLast4Digits);
	}
	
	@Test
	public void testThatLastFourDigitsThrowsExceptionWithNullCardNumber() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> LastFourCardDigits.getLastFourDigits(null));
		assertEquals("Card number must not be null", exception.getMessage());
	}
	
	@Test
	public void testThatLastFourDigitsThrowsExceptionWithCardNumberLengthUnderFourteen() {
		String cardNumber = "7654321098";
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> LastFourCardDigits.getLastFourDigits(cardNumber));
		assertEquals("Card number must be at least 14 digits long", exception.getMessage());
	}
	
	@Test
	public void testThatLastFourDigitsThrowsExceptionWithNonNumericCharactersInCardNumber() {
		String cardNumber = "1234abcd5678efgh";
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> LastFourCardDigits.getLastFourDigits(cardNumber));
		assertEquals("Card number should only be numeric characters", exception.getMessage());
	}
	
	@Test
	public void testThatLastFourDigitsHasNonNumericErrorMessageWhenNonNumericCharactersInCardNumberAndLessThanSixteen() {
		String cardNumber = "1234abcd5678e";
		try {
			LastFourCardDigits.getLastFourDigits(cardNumber);
		} catch (IllegalArgumentException e) {
			assertEquals("Card number should only be numeric characters", e.getMessage());
		}
	}
}
