package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.model.Post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse implements Parse {
    static final Logger LOG = LoggerFactory.getLogger(SqlRuParse.class.getName());

    @Override
    public List<Post> list(String link) {
        List<Post> posts = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(link).get();
            Elements pages = doc.select(".sort_options");
            int pageId = 1;
            while (pageId != 6) {
                Elements row = doc.select(".postslisttopic");
                Elements styleRow = doc.select(".altCol");
                int dateId = 1;
                for (Element td : row) {
                    Post post = new Post();
                    Element href = td.child(0);
                    post.setPost(String.format("%s\r\n%s",
                            href.attr("href"), href.text()));
                    Element date = styleRow.get(dateId);
                    post.setDate(date.text());
                    dateId += 2;
                    posts.add(post);
                }
                String path = pages.get(1)
                        .select("td")
                        .get(0)
                        .child(pageId)
                        .attr("href");
                doc = Jsoup.connect(path).get();
                pageId++;
            }
        }catch(IOException e) {
            LOG.error("parse exception", e);
        }
        return posts;
    }

    @Override
    public Post detail(String link) {
        Post post = new Post();
        return post.parseDetails(link);
    }

    public static void main(String[] args) {
        System.out.println(new SqlRuParse().list("https://www.sql.ru/forum/job-offers"));
        System.out.println(new SqlRuParse().detail("https://www.sql.ru/forum/1329807/sql-razrabotchik"));
    }
}
