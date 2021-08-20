package com.github.jm27.currency_exchange.service;

import com.github.jm27.currency_exchange.domain.Transaction;
import com.github.jm27.currency_exchange.repository.TransactionRepo;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransactionService {
    private final TransactionRepo transactionRepository;

    public TransactionService(TransactionRepo transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    //
    public Flux<Transaction> getAll() {
        return transactionRepository.getAll();
    }

    //    public Mono<Transaction> get(int id) {
//        return  transactionRepository.get(id);
//    }
//
    public Transaction create(Transaction transaction) {
        return transactionRepository.create(transaction);
    }
}
