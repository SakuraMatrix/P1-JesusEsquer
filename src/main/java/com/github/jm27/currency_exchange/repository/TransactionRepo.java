package com.github.jm27.currency_exchange.repository;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspace;
import com.datastax.oss.driver.api.querybuilder.schema.CreateTable;

public class TransactionRepo {
    private final CqlSession session;

    public TransactionRepo(CqlSession session) {
        this.session = session;
    }

    // Create key space for transactions.
    public void InitKeySpace() {
        CreateKeyspace exchanges = SchemaBuilder.createKeyspace("exchanges")
                .ifNotExists()
                .withSimpleStrategy(1);

        session.execute(exchanges.build());

        CreateTable transactions = SchemaBuilder.createTable("exchanges", "transactions")
                .ifNotExists()
                .withPartitionKey("id", DataTypes.INT);
//                .withColumn("_from", DataTypes.TEXT)
//                .withColumn("_to", DataTypes.TEXT)
//                .withColumn("_amount", DataTypes.TEXT);

        session.execute(transactions.build());
    }
}
