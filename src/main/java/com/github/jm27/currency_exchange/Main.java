package com.github.jm27.currency_exchange;

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