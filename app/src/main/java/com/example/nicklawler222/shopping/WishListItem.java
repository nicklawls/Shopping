package com.example.nicklawler222.shopping;
public class WishListItem {
    private String itemTitle;
    public String getItemTitle() {
        return itemTitle;
    }
    private String itemNumber;
    public String getItemNumber() {
        return itemNumber;
    }
    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }
    public WishListItem(String title, String number){
        this.itemTitle = title;
        this.itemNumber = number;
    }
}
