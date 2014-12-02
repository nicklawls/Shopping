package com.example.nicklawler222.shopping;
public class CartListItem {
    private String itemTitle;
    private String itemPrice;
    private String itemNumber;
    private String totalItemPrice;

    public String getItemTitle() {
        return itemTitle;
    }
    public String getItemPrice() {
        return itemPrice;
    }
    public String getTotalItemPrice() {
        return totalItemPrice;
    }
    public String getItemNumber() {
        return itemNumber;
    }
    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }
    public CartListItem(String title, String number, String price, String totalPrice){
        this.itemTitle = title;
        this.itemNumber = number;
        this.itemPrice = price;
        this.totalItemPrice = totalPrice;
    }
}
