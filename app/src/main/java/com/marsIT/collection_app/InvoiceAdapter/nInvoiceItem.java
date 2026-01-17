package com.marsIT.collection_app.InvoiceAdapter;

public class nInvoiceItem {
    private String invoiceNo;
    private String amount;
    private String department;

    public nInvoiceItem(String invoiceNo, String amount, String department) {
        this.invoiceNo = invoiceNo;
        this.amount = amount;
        this.department = department;
    }

    public String getInvoiceNo() { return invoiceNo; }
    public String getAmount() { return amount; }
    public String getDepartment() { return department; }

    public void setInvoiceNo(String invoiceNo) { this.invoiceNo = invoiceNo; }
    public void setAmount(String amount) { this.amount = amount; }
    public void setDepartment(String department) { this.department = department; }
}
