package com.marsIT.collection_app.CategoryAdapter;

public class nCategoryItem {
    private String category;
    private String amount;

    public nCategoryItem(String category, String amount) {
        this.category = category;
        this.amount = amount;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
}
