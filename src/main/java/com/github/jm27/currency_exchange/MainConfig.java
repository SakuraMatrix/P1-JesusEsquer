package com.github.jm27.currency_exchange;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspace;
import com.datastax.oss.driver.api.querybuilder.schema.CreateTable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jm27.currency_exchange.domain.Transaction;
import com.github.jm27.currency_exchange.repository.TransactionRepo;
import com.github.jm27.currency_exchange.service.TransactionService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.DisposableServer;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServer;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class MainConfig {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final CqlSession CQL_SESSION = CqlSession.builder().build();
    private static final TransactionRepo TRANSACTION_REPO = new TransactionRepo(CQL_SESSION);
    private static final TransactionService TRANSACTION_SERVICE = new TransactionService(TRANSACTION_REPO);

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
    public Transaction jsonToClassTransaction(String jsonString){
        Transaction transaction = null;
        try {
            // Convert Json to Object
            transaction = OBJECT_MAPPER.readValue(jsonString, Transaction.class);

//           System.out.println(transaction.toString());
        } catch (Exception e){
            System.err.println("Something went wrong converting json to class");
            e.printStackTrace();
        }
        return transaction;
    }

    // Init Cassandra DB session, Keyspace and table
    public void initDBKeySpaceTable(){
        CreateKeyspace exchanges = SchemaBuilder.createKeyspace("exchanges")
                .ifNotExists()
                .withSimpleStrategy(1);

        CQL_SESSION.execute(exchanges.build());

        CreateTable transactions = SchemaBuilder.createTable("exchanges", "transactions")
                .ifNotExists()
                .withPartitionKey("id", DataTypes.INT)
                .withColumn("Foo", DataTypes.TEXT)
                .withColumn("Top", DataTypes.TEXT)
                .withColumn("Amount", DataTypes.TEXT);

        CQL_SESSION.execute(transactions.build());
        System.out.println("DB init Success!");
    }


    // Reactor Client Server INIT.
    public HttpClient initializeReactorClient(){
        HttpClient client = HttpClient.create().headers(
                        h -> h.add("x-rapidapi-key", "?")
                )
                .keepAlive(true);

        client.warmup()
                .block();
        return client;
    }

    // Reactor Client Get All currencies.
    public Mono<String> clientGetCurrencies(HttpClient client){
        Mono<String> res = client.get()
                .uri("https://currency-converter5.p.rapidapi.com/currency/list")
                .responseContent()
                .aggregate()
                .asString();

        return res;
    }

    // Reactor Client Get exchange.
    public Mono<String> clientGetExchange(Map<String, String> map, HttpClient client){
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

//            res

//            TRANSACTION_SERVICE.create(tran);

        return res;
    }


    // Reactor Server.
    public DisposableServer initDisposableServer() throws URISyntaxException {
        // HTML Index file
        Path index = Paths.get(Main.class.getResource("/index.html").toURI());

        // Client
        HttpClient clientServer = initializeReactorClient();

        // Init Server
        HttpServer httpServer = HttpServer.create();

        httpServer.warmup().block();

        DisposableServer server = httpServer
                .port(8080)
                .route(routes -> // Create route. Get method, respond with string
                                routes
                                        .get("/exchanges", (request, response) ->
                                                response.send(
                                                        TRANSACTION_SERVICE.getAll()
                                                                .map(MainConfig::toByteBuff)
                                                ))
                                        .get("/currencies", (request, response) ->
                                                response.send(ByteBufFlux.fromString(clientGetCurrencies(clientServer))))
                                        .post("/echo", // Post request
                                                (request, response) ->
                                                        response.send(request
                                                                .receive()
                                                                .retain()
                                                        ))
                                        .get("/convert/to={to}/from={from}/amount={amount}", // Get request with params
                                                (request, response) ->
                                                        response.send(ByteBufFlux.fromString(clientGetExchange(request.params(), clientServer))))
                                .file("/index", index ) // Serve file on route
                )
                .bindNow();

        return server;
    }
}
