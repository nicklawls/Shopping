package com.example.nicklawler222.shopping;

public class RateReviewListItem {
    private String listItem_user;
    private String listItem_review;
    private String listItem_rating;

    public String getUserText() {
        return listItem_user;
    }

    public String getReviewText() {
        return listItem_review;
    }

    public String getRatingText() {
        return listItem_rating;
    }

    public void setUserText(String listItem_user) {
        this.listItem_user = listItem_user;
    }

    public RateReviewListItem(String listItem_user, String listItem_review, String listItem_rating){
        this.listItem_user = listItem_user;
        this.listItem_review = listItem_review;
        this.listItem_rating = listItem_rating;
    }

}
