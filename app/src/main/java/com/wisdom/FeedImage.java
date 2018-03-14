package com.wisdom;

/**
 * Created by praji on 14-Mar-18.
 */

public class FeedImage {
    public RenderedImage guid;

    public class RenderedImage {
        private String rendered;

        public String getRendered() {
            return rendered;
        }
    }

    public RenderedImage getGuid() {
        return guid;
    }
}
