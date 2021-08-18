package com.github.jm27.currency_exchange.domain;

public class Transaction {
    private int id;
    private String from;
    private String to;
    private Double amount;

    public Transaction(int id, String from, String to, Double amount) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    // NO ID Constructor.
    public Transaction(String from, String to, Double amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
