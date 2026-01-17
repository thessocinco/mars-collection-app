package com.marsIT.collection_app.InvoiceAdapter;

public class InvoiceItem {
    private String invoiceNo;
    private String amount;
    private String department;
    private boolean isSelected; // For delete checkbox

    public InvoiceItem(String invoiceNo, String amount, String department) {
        this.invoiceNo = invoiceNo;
        this.amount = amount;
        this.department = department;
        this.isSelected = false;
    }

    public String getInvoiceNo() { return invoiceNo; }
    public String getAmount() { return amount; }
    public String getDepartment() { return department; }
    public boolean isSelected() { return isSelected; }

    public void setInvoiceNo(String invoiceNo) { this.invoiceNo = invoiceNo; }
    public void setAmount(String amount) { this.amount = amount; }
    public void setDepartment(String department) { this.department = department; }
    public void setSelected(boolean selected) { isSelected = selected; }
}
