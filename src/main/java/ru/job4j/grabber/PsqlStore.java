package ru.job4j.grabber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.model.Post;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {
    static final Logger LOG = LoggerFactory.getLogger(PsqlStore.class.getName());
    private Connection cnn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
            cnn = DriverManager.getConnection(
                    cfg.getProperty("jdbc.url"),
                    cfg.getProperty("jdbc.username"),
                    cfg.getProperty("jdbc.password"));
        } catch (Exception e) {
            LOG.error("connection exception", e);
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement stat = cnn
                .prepareStatement("insert into post(name, text, link, created)"
                        + "values(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            stat.setString(1, post.getName());
            stat.setString(2, post.getPost());
            stat.setString(3, post.getLink());
            stat.setString(4, post.getDate());
            stat.execute();
        } catch (SQLException e) {
            LOG.error("insert exception", e);
        }
    }

    @Override
    public List<Post> getAll() {
        return select("select * from post");
    }

    @Override
    public Post findById(String id) {
        try {
        return select("select * from post p where p.id =" + id).get(0);
        } catch (IndexOutOfBoundsException e) {
            LOG.error("post with specified id not found", e);
        }
        return null;
    }

    private List<Post> select(String query) {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement stat = cnn.prepareStatement(query)) {
            try (ResultSet result = stat.executeQuery()) {
                while (result.next()) {
                    Post post = new Post();
                    post.setId(result.getInt("id"));
                    post.setName(result.getString("name"));
                    post.setPost(result.getString("text"));
                    post.setLink(result.getString("link"));
                    post.setDate(result.getString("created"));
                    posts.add(post);
                }
            }
        } catch (SQLException e) {
            LOG.error("select exception", e);
        }
        return posts;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    public static void main(String[] args) throws Exception {
        Properties prop = new Properties();
        try (InputStream in = PsqlStore.class.getClassLoader()
                .getResourceAsStream("app.properties")) {
            prop.load(in);
        }
        try (PsqlStore store = new PsqlStore(prop)) {
            SqlRuParse parser = new SqlRuParse();
            List<Post> posts = parser.list("https://www.sql.ru/forum/job-offers/1");
            for (Post post : posts) {
                store.save(post);
            }
            System.out.println(store.getAll());
            System.out.println(store.findById("262"));
        }
    }
}
