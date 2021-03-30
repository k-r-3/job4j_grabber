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
    private List<Post> posts = new ArrayList<>();

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

    public void setId(Post post) {
        try (PreparedStatement idStat = cnn
                .prepareStatement("select p.id from post p where p.name = ?")) {
            idStat.setString(1, post.getName());
            try (ResultSet result = idStat.executeQuery()) {
                if (result.next()) {
                    post.setId(result.getInt("id"));
                    posts.add(post);
                }
            }
        } catch (SQLException e) {
            LOG.error("set id exception", e);
        }
    }

    @Override
    public List<Post> getAll() {
        return posts;
    }

    @Override
    public Post findById(String id) {
        return posts.stream()
                .filter(p -> String.valueOf(p.getId()).equals(id))
                .findFirst()
                .get();
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
            List<Post> posts = parser.fullPost("https://www.sql.ru/forum/job-offers/1");
            for (Post post : posts) {
                store.save(post);
                store.setId(post);
            }
            System.out.println(store.getAll());
            System.out.println(store.findById("2"));
        }
    }
}
