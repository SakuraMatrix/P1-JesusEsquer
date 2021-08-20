package com.github.jm27.currency_exchange.repository;

import ch.qos.logback.classic.Logger;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.github.jm27.currency_exchange.Main;
import com.github.jm27.currency_exchange.domain.Transaction;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class TransactionRepo {
    private static final Logger log = (Logger) LoggerFactory.getLogger("transactionRepo");

    private final CqlSession session;

    public TransactionRepo(CqlSession session) {
        this.session = session;
    }

    // CREATE new Transaction
    public Transaction create(Transaction transaction) {
        log.info("Inserting into keyspace new transaction");
        System.out.println(transaction.toString());
        // Statement
        PreparedStatement insertStatement = session.prepare(
                "INSERT INTO exchanges.transactions (id, Foo, Top, Amount, Timestamp) VALUES (?, ?, ?, ?, ?)"
        );

        // Statement
        BoundStatement boundStatement = insertStatement.bind(
                transaction.getId(),
                transaction.getFrom(),
                transaction.getTo(),
                transaction.getAmount(),
                transaction.getTimestamp()
        );

        Mono.from(session.executeReactive(boundStatement)).subscribe();

        return transaction;
    }

    // READ all transactions
    public Flux<Transaction> getAll() {
        log.info("Retrieving all exchanges from keyspace");
        PreparedStatement readStatement = session.prepare("SELECT * FROM exchanges.transactions");

        BoundStatement boundStatement = readStatement.bind();

        return Flux.from(session.executeReactive(boundStatement))
                .map(
                        row -> new Transaction
                                (
                                        row.getUuid("id"),
                                        row.getString("Foo"),
                                        row.getString("Top"),
                                        row.getDouble("Amount"),
                                        row.getInstant("Timestamp")
                                )
                );

    }

    // Read one Transaction
    public Mono<Transaction> get(UUID id) {
        log.info("Retrieving transaction with id: " + id);

        PreparedStatement getOneStatement = session.prepare("Select * FROM exchanges.transactions WHERE id = ?");

        BoundStatement boundStatement = getOneStatement.bind(
                id
        );

        return Mono.from(session.executeReactive(boundStatement))
                .map(
                        row -> new Transaction(
                                row.getUuid("id"),
                                row.getString("Foo"),
                                row.getString("Top"),
                                row.getDouble("Amount"),
                                row.getInstant("Timestamp")
                        )
                );
    }

    // UPDATE

    //DELETE
    public void delete(UUID id) {
        log.info("Deleting transaction with id: " + id);

        PreparedStatement deleteStatement = session.prepare("DELETE FROM exchanges.transactions WHERE id = ?");

        BoundStatement boundStatement = deleteStatement.bind(id);

       Flux.from(session.executeReactive(boundStatement)).subscribe();

    }
}
