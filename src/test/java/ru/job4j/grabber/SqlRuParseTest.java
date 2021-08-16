package ru.job4j.grabber;

import org.junit.Test;
import ru.job4j.grabber.model.Post;

import java.util.Objects;

import static org.junit.Assert.*;

public class SqlRuParseTest {
    private String postLink = "https://www.sql.ru/forum/1329807/sql-razrabotchik";
    private String postsLink = "https://www.sql.ru/forum/job-offers";

    @Test
    public void postDescr() {
        Post post = new SqlRuParse().detail(postLink);
        assertTrue(Objects.nonNull(post.getPost()));
        assertTrue(Objects.nonNull(post.getDate()));
    }

}