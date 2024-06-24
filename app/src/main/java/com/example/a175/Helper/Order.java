package com.example.a175.Helper;

import com.example.a175.Domain.ItemsDomain;

import java.util.ArrayList;

public class Order {
    private String orderId;
    private ArrayList<ItemsDomain> items;
    private double totalPrice;
    private double tax;
    private double deliveryFee;

    // Các phương thức getter và setter

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public ArrayList<ItemsDomain> getItems() {
        return items;
    }

    public void setItems(ArrayList<ItemsDomain> items) {
        this.items = items;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }
}
