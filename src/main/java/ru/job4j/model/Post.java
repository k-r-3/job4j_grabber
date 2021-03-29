package ru.job4j.model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.text.ParseException;

public class Post {
    static final Logger LOG = LoggerFactory.getLogger(Post.class.getName());
    private String post;
    private String date;

    public void setPost(String post) {
        this.post = post;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPost() {
        return post;
    }

    public String getDate() {
        return date;
    }

    public void parseDetails(String url) {
        SqlRuDateTimeParser parser = new SqlRuDateTimeParser();
        try {
            Document doc = Jsoup.connect(url).get();
            Elements body = doc.select(".msgBody");
            setPost(body.get(1).text());
            Elements date = doc.select(".msgFooter");
            setDate(parser.parse(date.first().text()));
        } catch (IOException | ParseException e) {
            LOG.error("parse exception", e);
        }

    }

    public static void main(String[] args) {
        Post post = new Post();
        post.parseDetails("https://www.sql.ru/forum/1325330"
                + "/lidy-be-fe-senior-cistemnye-analitiki-qa-i-devops-moskva-do-200t");
        System.out.println(post.getPost());
        System.out.println(post.getDate());
    }
}
