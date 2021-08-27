package com.github.jm27.currency_exchange;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

public class MainTest extends BaseHttpTest{



    @Test
    void httpPort() {
        disposableServer = createServer(8080)
                .handle((req, resp) -> resp.sendNotFound())
                .bindNow();

        assertThat(disposableServer.port()).isEqualTo(8080);
    }


    @Test
    void startRouter() {
        disposableServer = createServer()
                .route(routes ->
                        routes.get("/exchanges",
                                (req, resp) -> resp.sendString(Mono.just("hello!"))))
                .bindNow();

        Integer code =
                createClient(disposableServer.port())
                        .get()
                        .uri("/exchanges")
                        .responseSingle((res, buf) -> Mono.just(res.status().code()))
                        .block();
        assertThat(code).isEqualTo(200);

        code = createClient(disposableServer.port())
                .get()
                .uri("/helloMan")
                .responseSingle((res, buf) -> Mono.just(res.status().code()))
                .block();
        assertThat(code).isEqualTo(404);
    }

    @Test
    public void getAllTransactions() {
    }
}
