package com.example.pocketmanager;

import android.graphics.Bitmap;

public class Expense {

    private String title;
    private String amount;
    private Bitmap photo;

    public Expense(String title, String amount, Bitmap photo) {
        this.title = title;
        this.amount = amount;
        this.photo = photo;
    }

    // GETTERS
    public String getTitle() {
        return title;
    }

    public String getAmount() {
        return amount;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    // SETTERS (pour UPDATE)
    public void setTitle(String title) {
        this.title = title;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
