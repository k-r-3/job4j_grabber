package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;
import ru.job4j.grabber.model.Post;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class SqlRuParse implements Parse {
    static final Logger LOG = LoggerFactory.getLogger(SqlRuParse.class.getName());
    private DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("E MMM dd HH:mm:ss z y", Locale.ENGLISH);
    private SqlRuDateTimeParser parser = new SqlRuDateTimeParser();

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
                    post.setName(href.text());
                    String postLink = href.attr("href");
                    post.setLink(postLink);
                    post.setPost(detail(postLink).getPost());
                    Element date = styleRow.get(dateId);
                    post.setDate(LocalDateTime.parse(parser.parse(date.text()), formatter));
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
        } catch (IOException | ParseException e) {
            LOG.error("parse exception", e);
        }
        return posts;
    }

    @Override
    public Post detail(String link) {
        Post post = new Post();
        try {
            Document doc = Jsoup.connect(link).get();
            Elements body = doc.select(".msgBody");
            StringBuilder msg = new StringBuilder();
            StringTokenizer st = new StringTokenizer(body.get(1).text());
            int tokenCount = 0;
            while (st.hasMoreElements()) {
                String token = st.nextToken();
                if (tokenCount == 5 || token.matches(".*:")) {
                    msg.append(token).append("\n");
                    tokenCount = 0;
                } else {
                    msg.append(token).append(" ");
                    tokenCount++;
                }
            }
            post.setPost(msg.toString());
            Elements date = doc.select(".msgFooter");
            SimpleDateFormat sdf = new SimpleDateFormat("d MMM yy, HH:mm");
            post.setDate(LocalDateTime.parse(parser.parse(date.first().text()), formatter));
        } catch (IOException | ParseException e) {
            LOG.error("parse exception", e);
        }
        return post;
    }

    public static void main(String[] args) {
        System.out.println(new SqlRuParse().list("https://www.sql.ru/forum/job-offers"));
//        System.out.println(new SqlRuParse()
//                .detail("https://www.sql.ru/forum/1329807/sql-razrabotchik"));
    }
}
