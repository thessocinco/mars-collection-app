package com.marsIT.collection_app.CategoryAdapter;

public class CategoryItem {
    private String category;
    private String amount;
    private boolean selected = false; // for checkbox

    public CategoryItem(String category, String amount) {
        this.category = category;
        this.amount = amount;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }

    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
}
