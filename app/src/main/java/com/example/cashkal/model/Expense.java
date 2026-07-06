package com.example.cashkal.model;


/// Expense transaction saved under users/{uid}/expenses/{expenseId} in Firestore

public class Expense {

    private String id;
    private String uid;
    private double amount;
    private String description;
    private String category;
    private String expenseType;
    private long date;
    private String note;
    private long createdAt;

    public Expense() {
    }

    public Expense(String id, String uid, double amount, String description,
                   String category, String expenseType, long date,
                   String note, long createdAt) {
        this.id = id;
        this.uid = uid;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.expenseType = expenseType;
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

    public String getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(String expenseType) {
        this.expenseType = expenseType;
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