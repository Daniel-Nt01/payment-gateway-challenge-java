package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.enums.Currency;
import com.checkout.payment.gateway.enums.PaymentStatus;
import java.util.UUID;

public class Payment {
	private UUID id;
	private PaymentStatus status;
	private String cardNumberLastFour;
	private int expiryMonth;
	private int expiryYear;
	private Currency currency;
	private int amount;

	public Payment() {

	}

	public Payment(UUID id, PaymentStatus status, String cardNumberLastFour, int expiryMonth, int expiryYear,
			Currency currency, int amount) {
		super();
		this.id = id;
		this.status = status;
		this.cardNumberLastFour = cardNumberLastFour;
		this.expiryMonth = expiryMonth;
		this.expiryYear = expiryYear;
		this.currency = currency;
		this.amount = amount;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public PaymentStatus getStatus() {
		return status;
	}

	public void setStatus(PaymentStatus status) {
		this.status = status;
	}

	public String getCardNumberLastFour() {
		return cardNumberLastFour;
	}

	public void setCardNumberLastFour(String cardNumberLastFour) {
		this.cardNumberLastFour = cardNumberLastFour;
	}

	public int getExpiryMonth() {
		return expiryMonth;
	}

	public void setExpiryMonth(int expiryMonth) {
		this.expiryMonth = expiryMonth;
	}

	public int getExpiryYear() {
		return expiryYear;
	}

	public void setExpiryYear(int expiryYear) {
		this.expiryYear = expiryYear;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "Payment{" + "id='" + id + "', status='" + status + "', cardNumberLastFour='" + cardNumberLastFour
				+ "', expiryMonth=" + expiryMonth + ", expiryYear=" + expiryYear + ", currency='" + currency.getCode()
				+ "', amount=" + amount + '}';
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Payment payment = (Payment) o;

		return id.equals(payment.id);
	}
	
	@Override
	public int hashCode() {
	    return id.hashCode();
	}
}
