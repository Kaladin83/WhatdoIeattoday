package com.example.maratbe.whatdoieattoday.objects;

import java.util.ArrayList;

public class DayOrder {
    private int day;
    private int month;
    private int monthColor;
    private ArrayList<Item> items;

    public DayOrder(int day, int month, ArrayList<Item> items, int color) {
        this.day = day;
        this.month = month;
        this.items = items;
        this.monthColor = color;
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

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public int getMonthColor() {
        return monthColor;
    }

    public void setMonthColor(int monthColor) {
        this.monthColor = monthColor;
    }
}
