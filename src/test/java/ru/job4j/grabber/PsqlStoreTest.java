package ru.job4j.grabber;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.grabber.model.Post;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Properties;

import static org.junit.Assert.*;

public class PsqlStoreTest {
    static final Logger LOG = LoggerFactory.getLogger(PsqlStoreTest.class.getName());
    private Properties prop = new Properties();

    @Before
    public void init() {
        try (InputStream in = PsqlStore.class.getClassLoader()
                .getResourceAsStream("app.properties")) {
            prop.load(in);
        } catch (IOException e) {
            LOG.error("init exception", e);
        }
    }

    @Test
    public void insert() {
        Post post = new Post();
        post.setName("name");
        post.setPost("post");
        post.setLink("http://link.ru");
        post.setDate(LocalDateTime.now());
        PsqlStore store = new PsqlStore(prop);
        store.sqlRollback();
        store.save(post);
        assertTrue(store.getAll().size() == 1);
    }

    @Test
    public void selectAll() {
        PsqlStore store = new PsqlStore(prop);
        store.sqlRollback();
        Post first = new Post();
        Post second = new Post();
        first.setName("name1");
        second.setName("name2");
        first.setPost("post1");
        second.setPost("post2");
        first.setId(1);
        second.setId(2);
        first.setLink("http://link1.ru");
        second.setLink("http://link2.ru");
        first.setDate(LocalDateTime.now());
        second.setDate(LocalDateTime.now());
        store.save(first);
        store.save(second);
        assertTrue(store.getAll().size() == 2);
    }

}