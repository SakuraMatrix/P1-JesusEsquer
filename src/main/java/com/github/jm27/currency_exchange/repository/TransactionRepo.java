package com.github.jm27.currency_exchange.repository;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.github.jm27.currency_exchange.domain.Transaction;
import reactor.core.publisher.Flux;

public class TransactionRepo {
    private final CqlSession session;

    public TransactionRepo(CqlSession session) {
        this.session = session;
    }

    // CREATE
    public Transaction create(Transaction transaction){
        // Statement
        PreparedStatement insertStatement = session.prepare(
                "INSERT INTO exchanges.transactions (id, Foo, Top, Amount) VALUES (?, ?, ?, ?)"
        );

        // Statement
        BoundStatement boundStatement = insertStatement.bind(
                transaction.getId(),
                transaction.getFrom(),
                transaction.getTo(),
                transaction.getAmount()
        );

        session.execute(boundStatement);
        return transaction;
    }

    public Flux<Transaction> getAll(){

        PreparedStatement readStatement = session.prepare("SELECT * FROM exchanges.transactions");

        BoundStatement boundStatement = readStatement.bind();

        return Flux.from(session.executeReactive(boundStatement))
                .map(
                row -> new Transaction
                        (
                        row.getInt("id"),
                        row.getString("Foo"),
                        row.getString("Top"),
                        row.getString("Amount")
                )
        );

    }
}
