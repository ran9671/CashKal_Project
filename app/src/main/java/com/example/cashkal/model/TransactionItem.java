package com.example.cashkal.model;

public class TransactionItem {

    public static final String TYPE_EXPENSE = "expense";
    public static final String TYPE_INCOME = "income";

    private String id;
    private String type;
    private double amount;
    private String description;
    private String category;
    private String dateText;
    private long date;
    private String note;

    public TransactionItem(String id, String type, double amount, String description,
                           String category, String dateText, long date, String note) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.dateText = dateText;
        this.date = date;
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getDateText() {
        return dateText;
    }

    public long getDate() {
        return date;
    }

    public String getNote() {
        return note;
    }

    public boolean isExpense() {
        return TYPE_EXPENSE.equals(type);
    }

    public boolean isIncome() {
        return TYPE_INCOME.equals(type);
    }
}