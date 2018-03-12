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
    private FeedExcerpt excerpt;

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

    public class FeedExcerpt {
        private String rendered;

        public String getRendered() {
            return fromHtml(rendered).toString();
        }
    }


    public FeedExcerpt getExcerpt() {
        return excerpt;
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

}
