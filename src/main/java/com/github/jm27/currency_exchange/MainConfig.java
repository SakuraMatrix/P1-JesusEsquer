package com.github.jm27.currency_exchange;

import com.datastax.oss.driver.api.core.CqlSession;
import com.github.jm27.currency_exchange.repository.TransactionRepo;
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

    // Init Cassandra DB session, Keyspace and table
    public CqlSession initDBKeySpaceTable(){
    try(CqlSession session = CqlSession.builder().build()) {
        TransactionRepo transactionRepo = new TransactionRepo(session);
        transactionRepo.InitKeySpace();
        System.out.println("DB init Success!");
    return session;
    }
    }


    // Reactor Client Server INIT.
    public HttpClient initializeReactorClient(){
        HttpClient client = HttpClient.create().headers(
                        h -> h.add("x-rapidapi-key", "d2e5268630msh2a3882309cb2cb0p1306a4jsnecab62a0cbc7")
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
