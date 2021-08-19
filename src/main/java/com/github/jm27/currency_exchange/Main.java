package com.github.jm27.currency_exchange;

import com.datastax.oss.driver.api.core.CqlSession;
import com.github.jm27.currency_exchange.domain.Transaction;
import com.github.jm27.currency_exchange.repository.TransactionRepo;
import com.github.jm27.currency_exchange.service.TransactionService;

import java.net.URISyntaxException;


public class Main {
    public static void main(String ...args) throws URISyntaxException {

        MainConfig mainConfig = new MainConfig();
        // Init Cassandra DB
        mainConfig.initDBKeySpaceTable();

        // Init Server
        mainConfig.initDisposableServer().onDispose().block();



    }
}