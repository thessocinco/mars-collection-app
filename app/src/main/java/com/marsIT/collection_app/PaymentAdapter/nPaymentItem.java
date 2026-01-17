package com.marsIT.collection_app.PaymentAdapter;

public class nPaymentItem {
    private String paymentType;
    private String bankInitial;
    private String chkNumber;
    private String amount;

    public nPaymentItem(String paymentType, String bankInitial, String chkNumber, String amount) {
        this.paymentType = paymentType;
        this.bankInitial = bankInitial;
        this.chkNumber = chkNumber;
        this.amount = amount;
    }

    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    public String getBankInitial() { return bankInitial; }
    public void setBankInitial(String bankInitial) { this.bankInitial = bankInitial; }

    public String getChkNumber() { return chkNumber; }
    public void setChkNumber(String chkNumber) { this.chkNumber = chkNumber; }

    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
}
