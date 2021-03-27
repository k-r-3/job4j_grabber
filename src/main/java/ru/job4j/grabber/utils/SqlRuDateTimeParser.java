package ru.job4j.grabber.utils;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;;
import java.util.Locale;
import java.util.regex.Pattern;

public class SqlRuDateTimeParser implements DateTimeParser {
    static final Locale locale = new Locale("ru");
    static final SimpleDateFormat sdf,
            today,
            dateFormat;

    static {
        sdf = new SimpleDateFormat("d MMM yy, HH:mm", locale);
        today = new SimpleDateFormat("y-MM-dd, HH:mm", locale);
        dateFormat = new SimpleDateFormat("dd MMM yyyy");
    }
        @Override
    public String parse(String parse) throws ParseException {
            Pattern pToday = Pattern.compile("сегодня.*");
            Pattern yesterday = Pattern.compile("вчера.*");
            String[] shortMonths = {
                    "янв", "фев", "мар", "апр", "май", "июн",
                    "июл", "авг", "сен", "окт", "ноя", "дек"};
            DateFormatSymbols dfs = DateFormatSymbols.getInstance(locale);
            dfs.setShortMonths(shortMonths);
            sdf.setDateFormatSymbols(dfs);
            if (pToday.matcher(parse).find()) {
                parse = parse.replace("сегодня", LocalDate.now().toString());
                return today.parse(parse).toString();
            } else if (yesterday.matcher(parse).find()) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, -1);
                String replacement = dateFormat.format(calendar.getTime());
                parse = parse.replace("вчера", replacement);
                parse = parse.replace(".", "");
            }
            return sdf.parse(parse).toString();
    }
}
