package com.example.footballlive.data;

import androidx.annotation.Keep;

@Keep
public class Notice {
    String title;
    String content;
    String creationDate;

    public Notice() {
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {return content;}

    public void setContent(String content) {
        this.content = content;
    }
}
