package com.checkout.payment.gateway.dto.response.error;

import com.checkout.payment.gateway.enums.PaymentStatus;

public record PaymentProcessingError(PaymentStatus status, String message) {

}
