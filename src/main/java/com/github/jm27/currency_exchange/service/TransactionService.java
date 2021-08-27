package com.github.jm27.currency_exchange.service;

import com.github.jm27.currency_exchange.domain.Transaction;
import com.github.jm27.currency_exchange.repository.TransactionRepo;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class TransactionService {
    private final TransactionRepo transactionRepository;

    public TransactionService(TransactionRepo transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // Create new Transaction
    public Transaction create(Transaction transaction) {
        return transactionRepository.create(transaction);
    }

    // Get all transactions
    public Flux<Transaction> getAll() {
        return transactionRepository.getAll();
    }

    // Get single transaction
    public Mono<Transaction> get(String id) {
        return  transactionRepository.get(UUID.fromString(id));
    }

    // Delete Transaction
    public void delete(String id) {
        transactionRepository.get(UUID.fromString(id));
    }
}
