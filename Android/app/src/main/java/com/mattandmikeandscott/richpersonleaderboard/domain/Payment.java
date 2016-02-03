package com.mattandmikeandscott.richpersonleaderboard.domain;

import java.util.Date;

public class Payment {
    private int id;
    private double amount;
    private Date date;

    public Payment(int id, double amount, Date date) {
        this.id = id;
        this.amount = amount;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}