package com.checkout.payment.gateway.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Year;
import java.time.YearMonth;

import org.junit.Test;

public class CreateExpiryDateTest {

	@Test
	public void testGetValidExpiryDateFromMonthAndYear() {
		int month = 12;
		int year = Year.now().getValue() + 1; // Set to next year to ensure it's a valid future date
		String expectedExpiryDate = "12/" + year;
		
		String actualExpiryDate = CreateExpiryDate.getValidExpiryDateFromMonthAndYear(month, year);
		
		assertEquals(expectedExpiryDate, actualExpiryDate);
	}
	
	@Test
	public void testGetValidExpiryDateFromMonthAndYearWithDifferentValues() {
		int month = 11;
		int year = Year.now().getValue() + 1;
		String expectedExpiryDate = "11/" + year; 
		
		String actualExpiryDate = CreateExpiryDate.getValidExpiryDateFromMonthAndYear(month, year);
		
		assertEquals(expectedExpiryDate, actualExpiryDate);
	}
	
	@Test
	public void testGetValidExpiryDateFromMonthAndYearWithSingleDigitMonth() {
		int month = 5;
		int year = Year.now().getValue() + 1;
		String expectedExpiryDate = "05/" + year; 
		
		String actualExpiryDate = CreateExpiryDate.getValidExpiryDateFromMonthAndYear(month, year);
		
		assertEquals(expectedExpiryDate, actualExpiryDate);
	}
	
	@Test
	public void testThatGetValidExpiryDateThrowsExceptionForInvalidMonth() {
		int month = 13;
		int year = Year.now().getValue() + 1;
		
		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> CreateExpiryDate.getValidExpiryDateFromMonthAndYear(month, year));
		assertEquals("Invalid month: " + month, illegalArgumentException.getMessage());
	}
	
	@Test
	public void testThatGetValidExpiryDateThrowsExceptionForDateAfter20YearsFromToday() {
		YearMonth currentYearMonth = YearMonth.now().plusYears(21);
		int month = currentYearMonth.getMonthValue();
		int year = currentYearMonth.getYear();
		
		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> CreateExpiryDate.getValidExpiryDateFromMonthAndYear(month, year));
		assertEquals("Invalid as more than 20 years in the future: " + month + "/" + year, illegalArgumentException.getMessage());
	}
	
	@Test
	public void testThatGetValidExpiryDateFromMonthAndYearWithDateExactly20YearsFromToday() {
		YearMonth currentYearMonth = YearMonth.now().plusYears(20);
		int month = currentYearMonth.getMonthValue();
		int year = currentYearMonth.getYear();
		String expectedExpiryDate = String.format("%02d/%d", month, year);
		
		assertEquals(expectedExpiryDate, CreateExpiryDate.getValidExpiryDateFromMonthAndYear(month, year));
	}
	
	@Test
	public void testThatGetValidExpiryDateThrowsExceptionForYearAndMonthInThePast() {
		int month = 5;
		int year = Year.now().getValue() - 1;
		
		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> CreateExpiryDate.getValidExpiryDateFromMonthAndYear(month, year));
		assertEquals("Expiry date cannot be in the past: " + month + "/" + year, illegalArgumentException.getMessage());
	}
	
	@Test
	public void testThatGetValidExpiryDateThrowsExceptionForYearAndMonthInThePastWithDifferentValues() {
		int month = 5;
		int year = Year.now().getValue() - 100;
		
		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> CreateExpiryDate.getValidExpiryDateFromMonthAndYear(month, year));
		assertEquals("Expiry date cannot be in the past: " + month + "/" + year, illegalArgumentException.getMessage());
	}
	
}
