package com.github.jm27.currency_exchange.domain;


public class Transaction {
    private int id;
    private String from;
    private String to;
    private String amount;

    public Transaction(){}

    public Transaction(int id, String from, String to, String amount) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    // NO ID Constructor.
    public Transaction(String from, String to, Double amount) {
        this.id = 1;
        this.from = from;
        this.to = to;
        this.amount = String.valueOf(amount);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
