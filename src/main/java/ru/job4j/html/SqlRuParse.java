package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SqlRuParse {
    public static void main(String[] args) throws Exception {
        Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers").get();
        Elements pages = doc.select(".sort_options");
        int pageId = 1;
        while (pageId != 6) {
            Elements row = doc.select(".postslisttopic");
            Elements styleRow = doc.select(".altCol");
            int dateId = 1;
            for (Element td : row) {
                Element href = td.child(0);
                System.out.println(href.attr("href"));
                System.out.println(href.text());
                Element date = styleRow.get(dateId);
                System.out.println(date.text() + "\n");
                dateId += 2;
            }
            String path = pages.get(1)
                    .select("td")
                    .get(0)
                    .child(pageId)
                    .attr("href");
            doc = Jsoup.connect(path).get();
            pageId++;
        }
    }
}