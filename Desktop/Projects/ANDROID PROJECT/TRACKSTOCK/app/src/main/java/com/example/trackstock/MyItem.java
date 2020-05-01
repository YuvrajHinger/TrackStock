package com.example.trackstock;

import java.io.Serializable;

public class MyItem implements Serializable {
    int id;
    String itemName;
    int price;
    int currentStock;
    byte[] itemImage;

    public MyItem(int id, String itemName, int price, int currentStock, byte[] itemImage) {
        this.id = id;
        this.itemName = itemName;
        this.price = price;
        this.currentStock = currentStock;
        this.itemImage = itemImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public byte[] getItemImage() {
        return itemImage;
    }

    public void setItemImage(byte[] itemImage) {
        this.itemImage = itemImage;
    }
}
