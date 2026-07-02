package com.example.cashkal.model;

/** Shared user profile stored at users/{uid} in Firestore. */
public class User {
    private String uid;
    private String fullName;
    private String email;
    private double monthlyBudget;
    private double expectedMonthlyIncome;

    /** Required by Firestore toObject(). */
    public User() { }

    public User(String uid, String fullName, String email,
                double monthlyBudget, double expectedMonthlyIncome) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.monthlyBudget = monthlyBudget;
        this.expectedMonthlyIncome = expectedMonthlyIncome;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public double getMonthlyBudget() { return monthlyBudget; }
    public void setMonthlyBudget(double monthlyBudget) { this.monthlyBudget = monthlyBudget; }

    public double getExpectedMonthlyIncome() { return expectedMonthlyIncome; }
    public void setExpectedMonthlyIncome(double expectedMonthlyIncome) { this.expectedMonthlyIncome = expectedMonthlyIncome; }
}