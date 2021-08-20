package com.github.jm27.currency_exchange.repository;

import ch.qos.logback.classic.Logger;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.github.jm27.currency_exchange.domain.Transaction;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class TransactionRepo {
    private static final Logger log = (Logger) LoggerFactory.getLogger("transactionRepo");

    private final CqlSession session;

    public TransactionRepo(CqlSession session) {
        this.session = session;
    }

    // CREATE
    public Transaction create(Transaction transaction){
        log.info("Inserting into keyspace new transaction");
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

//        Mono.from(session.executeReactive(boundStatement)).subscribe();

        session.execute(boundStatement);
        return transaction;
    }

    public Flux<Transaction> getAll(){
        log.info("Retrieving all exchanges from keyspace");
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
