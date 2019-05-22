package com.example.maratbe.whatdoieattoday.objects;

public class Order {
    private String dishName;
    private String category;
    private int day;
    private int month;

    public Order(String dishName, String category, int day) {
        this.dishName = dishName;
        this.category = category;
        this.day = day;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }
}
