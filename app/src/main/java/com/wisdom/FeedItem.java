package com.wisdom;

/**
 * Created by praji on 2/22/2018.
 */

public class FeedItem {
    private String articleHeading;
    private String actualUrl;
    private String imageUrl;

    public String getActualUrl() {
        return actualUrl;
    }

    public void setActualUrl(String actualUrl) {
        this.actualUrl = actualUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getArticleHeading() {

        return articleHeading;
    }

    public void setArticleHeading(String articleHeading) {
        this.articleHeading = articleHeading;
    }
}
