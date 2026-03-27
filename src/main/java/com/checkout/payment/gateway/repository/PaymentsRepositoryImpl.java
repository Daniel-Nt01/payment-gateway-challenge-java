package com.checkout.payment.gateway.repository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.checkout.payment.gateway.model.Payment;

@Repository
public class PaymentsRepositoryImpl implements PaymentsRepository {

  private final ConcurrentHashMap<UUID, Payment> payments = new ConcurrentHashMap<>();

  @Override
  public void add(Payment payment) {
    payments.put(payment.getId(), payment);
  }

  @Override
  public Optional<Payment> get(UUID id) {
    return Optional.ofNullable(payments.get(id));
  }

}
