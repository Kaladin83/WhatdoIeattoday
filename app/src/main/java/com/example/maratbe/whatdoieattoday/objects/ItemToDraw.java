package com.example.maratbe.whatdoieattoday.objects;

import com.example.maratbe.whatdoieattoday.objects.Item;

public class ItemToDraw {
    private Item item;
    private int icon;

    public ItemToDraw(Item item, int icon) {
        this.item = item;
        this.icon = icon;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
