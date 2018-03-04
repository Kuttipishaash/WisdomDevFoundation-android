package com.wisdom;

import android.os.Build;
import android.text.Html;
import android.text.Spanned;

/**
 * Created by praji on 2/22/2018.
 */

public class FeedItem {

    private String date;
    private String link;
    private FeedTitle title;
    private FeedContent content;

    public class FeedTitle {
        private String rendered;

        public String getRendered() {

            return rendered;
        }
    }

    public class FeedContent{
        private String rendered;

        public String getRendered() {
            return fromHtml(rendered).toString();
        }
    }

    public String getDate() {
        return date;
    }

    public String getLink() {
        return link;
    }

    public FeedTitle getTitle() {
        return title;
    }

    public FeedContent getContent() {
        return content;
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }

    /*
    private String actualUrl;
    private String imageUrl;
    private String shortContent;

    public String getShortContent() {
        return shortContent;
    }

    public void setShortContent(String shortContent) {
        this.shortContent = shortContent;
    }

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
*/
}
