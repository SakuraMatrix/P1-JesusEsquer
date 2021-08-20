package com.github.jm27.currency_exchange;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.jm27.currency_exchange.domain.Transaction;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import reactor.netty.DisposableServer;

import java.net.URISyntaxException;
import java.time.Instant;
import java.util.UUID;


public class Main {
    // Logback logger
    private static final Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("main");

    public static void main(String... args) throws URISyntaxException {

        log.info("Init Application context");
        AnnotationConfigApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(MainConfig.class);

        // Init Cassandra DB
        applicationContext.getBean("initDBKeySpaceTable");

        log.info("Init DB");
        // Init Server
        applicationContext.getBean(DisposableServer.class)
                .onDispose()
                .block();
        log.info("Init Server");

        applicationContext.close();
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    public static ByteBuf toByteBuff(Object o) {

        try {
            return Unpooled.buffer()
                    .writeBytes(OBJECT_MAPPER.writerFor(Transaction.class).writeValueAsBytes(o));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    // JSON to Transaction class.
    public static Transaction jsonToClassTransaction(String jsonString) {
        Transaction transaction = null;
        try {
            // Convert Json to Object
            transaction = OBJECT_MAPPER.readValue(jsonString, Transaction.class);

            UUID id = UUID.randomUUID();
            Instant instant = Instant.now();

            transaction.setId(id);
            transaction.setTimestamp(instant);

        } catch (Exception e) {
            System.err.println("Something went wrong converting json to class");
            e.printStackTrace();
        }

        return transaction;
    }

}