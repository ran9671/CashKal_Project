package com.example.cashkal.model;


/// Income transaction saved under users/{uid}/incomes/{incomeId} in Firestore.

public class Income {

    private String id;
    private String uid;
    private double amount;
    private String description;
    private String category;
    private long date;
    private String note;
    private long createdAt;

    public Income() {
    }

    public Income(String id, String uid, double amount, String description,
                  String category, long date, String note, long createdAt) {
        this.id = id;
        this.uid = uid;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.date = date;
        this.note = note;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}