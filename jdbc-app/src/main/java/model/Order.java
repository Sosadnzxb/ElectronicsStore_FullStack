package com.electronics.store.model;

import java.util.Date;

public class Order {
    private int id;
    private int customerId;
    private Date orderDate;
    private String status;

    public Order() {}

    public Order(int customerId, Date orderDate, String status) {
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}