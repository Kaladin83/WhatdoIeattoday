package com.example.maratbe.whatdoieattoday.objects;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

public class Item {
    private String value;
    private String category;
    private int level;

    public Item (){};
    public Item(String value, String category, int level) {
        this.value = value;
        this.category = category;
        this.level = level;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
