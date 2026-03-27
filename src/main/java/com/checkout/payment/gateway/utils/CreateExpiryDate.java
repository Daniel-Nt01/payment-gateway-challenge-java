package com.checkout.payment.gateway.utils;

import java.time.YearMonth;

public class CreateExpiryDate {
	public static String getValidExpiryDateFromMonthAndYear(int month, int year) {
		if (month < 1 || month > 12) {
			throw new IllegalArgumentException("Invalid month: " + month);
		}
		
		YearMonth expiryDate = YearMonth.of(year, month);
		YearMonth currentYearMonth = YearMonth.now();
		
		if (expiryDate.isBefore(currentYearMonth)) {
			throw new IllegalArgumentException("Expiry date cannot be in the past: " + month + "/" + year);
		}
	
		if (expiryDate.isAfter(currentYearMonth.plusYears(20))) {
			throw new IllegalArgumentException("Invalid as more than 20 years in the future: " + month + "/" + year);
		}
		return String.format("%02d/%d", month, year);
	}
}
