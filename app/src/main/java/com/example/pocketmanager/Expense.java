package com.example.pocketmanager;

public class Expense {

    private String title;
    private double amount;
    private String imagePath;

    public Expense(String title, double amount, String imagePath) {
        this.title = title;
        this.amount = amount;
        this.imagePath = imagePath;
    }

    public String getTitle() {
        return title;
    }

    public double getAmount() {
        return amount;
    }

    public String getImagePath() {
        return imagePath;
    }
}
