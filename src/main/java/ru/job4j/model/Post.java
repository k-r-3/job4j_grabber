package ru.job4j.model;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

public class Post {
    private int id;
    private String name;
    private String link;
    private String post;
    private LocalDateTime date;

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public String getPost() {
        return post;
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return id + "\n"
                + name + "\n"
                + link + "\n"
                + post + "\n"
                + date + "\n";
    }
}
