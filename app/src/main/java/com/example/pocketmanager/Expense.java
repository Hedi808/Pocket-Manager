package com.example.pocketmanager;

public class Expense {

    private String title;
    private double amount;
    private String imageBase64;

    public Expense(String title, double amount, String imageBase64) {
        this.title = title;
        this.amount = amount;
        this.imageBase64 = imageBase64;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}
