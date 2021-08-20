package com.github.jm27.currency_exchange;

import ch.qos.logback.classic.Logger;
import com.datastax.oss.driver.api.core.CqlSession;
import com.github.jm27.currency_exchange.domain.Transaction;
import com.github.jm27.currency_exchange.repository.TransactionRepo;
import com.github.jm27.currency_exchange.service.TransactionService;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import reactor.netty.DisposableServer;

import java.net.URISyntaxException;


public class Main {
    // Logback logger
    private static final Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("main");

    public static void main(String ...args) throws URISyntaxException {

        log.info("Init Application context");
        AnnotationConfigApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(MainConfig.class);

//        MainConfig mainConfig = new MainConfig();
        // Init Cassandra DB
        applicationContext.getBean("initDBKeySpaceTable");
//        mainConfig.initDBKeySpaceTable();
        log.info("Init DB");
        // Init Server
        applicationContext.getBean(DisposableServer.class)
                .onDispose()
                .block();
        log.info("Init Server");

        applicationContext.close();
    }
}