package com.marsIT.collection_app.CustomerStatus;

public class CustStatus {

    private String customerID;
    private String customerName;
    private boolean collected;

    public CustStatus(String customerID, String customerName, boolean collected) {
        this.customerID = customerID;
        this.customerName = customerName;
        this.collected = collected;
    }

    // Getters
    public String getCustomerID() { return customerID; }
    public String getCustomerName() { return customerName; }
    public boolean isCollected() { return collected; }
}
