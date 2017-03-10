package com.example;

/**
 * Created by ni on 2017-03-09.
 * com.example.instagram-exam
 */
public class Photo {
    public String username;
    public String caption;
    public String createdTime;
    public String imageUrl;
    public String profileUrl;
    public String comment1;
    public String user1;
    public String comment2;
    public String user2;
    public String id;
    public int imageHeight;
    public int likesCount;
    public int commentsCount;

    public String getRelativeTime() {
        long ct = Long.parseLong(createdTime);
        long now = System.currentTimeMillis() / 1000;
        long elapsedSeconds = now - ct;

        if (elapsedSeconds < 60) { // 분
            return String.format("%.0fs", elapsedSeconds);
        } else if (elapsedSeconds < 3600) { // 시간
            return String.format("%.0fm", Math.floor(elapsedSeconds / 60));
        } else if (elapsedSeconds < 86400) { // 하루
            return String.format("%.0fh", Math.floor(elapsedSeconds / 3600));
        } else {
            return String.format("%.0fd", Math.floor(elapsedSeconds / 86400));
        }
    }
}
