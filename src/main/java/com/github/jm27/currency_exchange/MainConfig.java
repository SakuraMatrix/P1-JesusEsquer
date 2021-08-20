package com.github.jm27.currency_exchange;

import ch.qos.logback.classic.Logger;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspace;
import com.datastax.oss.driver.api.querybuilder.schema.CreateTable;
import com.github.jm27.currency_exchange.service.TransactionService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.DisposableServer;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServer;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Configuration
@ComponentScan
public class MainConfig {
    private static final Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("MainConfig");

    @Autowired
    TransactionService transactionService;


    @Bean
    public CqlSession session() {
        return CqlSession.builder().build();
    }

    // Init Cassandra DB session, Keyspace and table
    @Bean
    public String initDBKeySpaceTable(CqlSession session) {
        CreateKeyspace exchanges = SchemaBuilder.createKeyspace("exchanges")
                .ifNotExists()
                .withSimpleStrategy(1);

        session.execute(exchanges.build());

        CreateTable transactions = SchemaBuilder.createTable("exchanges", "transactions")
                .ifNotExists()
                .withPartitionKey("id", DataTypes.UUID)
                .withColumn("Foo", DataTypes.TEXT)
                .withColumn("Top", DataTypes.TEXT)
                .withColumn("Amount", DataTypes.DOUBLE)
                .withColumn("Timestamp", DataTypes.TIMESTAMP);

        session.execute(transactions.build());
        System.out.println("DB init Success!");

        return "Success";
    }


    // Reactor Client Server INIT.
    @Bean
    public HttpClient initializeReactorClient() {
        log.info("Init Reactor client server");
        HttpClient client = HttpClient.create().headers(
                        h -> h.add("x-rapidapi-key", "?")
                )
                .keepAlive(true);

        client.warmup()
                .block();
        return client;
    }

    // Reactor Client Get All currencies.
    public Mono<String> clientGetCurrencies(HttpClient client) {
        Mono<String> res = client.get()
                .uri("https://currency-converter5.p.rapidapi.com/currency/list")
                .responseContent()
                .aggregate()
                .asString();

        return res;
    }

    // Reactor Client Get exchange.
    public Mono<String> clientGetExchange(Map<String, String> map, HttpClient client) {
        // Get values from map
        String from = map.get("from");
        String to = map.get("to");
        String amount = map.get("amount");

        Mono<String> res = client.get()
                .uri("https://currency-converter13.p.rapidapi.com/convert?from="
                        + from + "&to=" + to + "&amount=" + amount)
                .responseContent()
                .aggregate()
                .asString();

        res.map(Main::jsonToClassTransaction).map(transactionService::create);

        return res;
    }


    // Reactor Server.
    @Bean
    public DisposableServer initDisposableServer(HttpClient client) throws URISyntaxException {
        // HTML Index file
        Path index = Paths.get(Main.class.getResource("/index.html").toURI());

        // Init Server
        HttpServer httpServer = HttpServer.create();

        httpServer.warmup().block();

        DisposableServer server = httpServer
                .port(8080)
                .route(routes -> // Create route. Get method, respond with string
                        routes
                                .get("/exchanges", (request, response) ->
                                        response.send(
                                                transactionService.getAll()
                                                        .map(Main::toByteBuff)
                                        ))
                                .get("/currencies", (request, response) ->
                                        response.send(ByteBufFlux.fromString(clientGetCurrencies(client))))
                                .post("/echo", // Post request
                                        (request, response) ->
                                                response.send(request
                                                        .receive()
                                                        .retain()
                                                ))
                                .get("/convert/to={to}/from={from}/amount={amount}", // Get request with params
                                        (request, response) ->
                                                response.send(clientGetExchange(request.params(), client)
                                                        .map(Main::jsonToClassTransaction)
                                                        .map(transactionService::create)
                                                        .map(Main::toByteBuff)
                                                )
                                )
                                .file("/index", index) // Serve file on route
                )
                .bindNow();

        return server;
    }
}
