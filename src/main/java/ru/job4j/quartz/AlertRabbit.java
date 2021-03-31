package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static void main(String[] args) {
        Properties prop = new Properties();
        try (InputStream in = AlertRabbit.class.getClassLoader()
                .getResourceAsStream("rabbit.properties")) {
            prop.load(in);
            try (Connection connect = DriverManager.getConnection(
                    prop.getProperty("url"),
                    prop.getProperty("username"),
                    prop.getProperty("password"))) {
                Class.forName(prop.getProperty("driver"));
                List<Long> store = new ArrayList<>();
                Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
                scheduler.start();
                JobDataMap data = new JobDataMap();
                data.put("store", store);
                data.put("connect", connect);
                JobDetail job = newJob(Rabbit.class)
                        .usingJobData(data)
                        .build();
                SimpleScheduleBuilder times = simpleSchedule()
                        .withIntervalInSeconds(Integer.parseInt(
                                prop.getProperty("rabbit.interval")))
                        .repeatForever();
                Trigger trigger = newTrigger()
                        .startNow()
                        .withSchedule(times)
                        .build();
                scheduler.scheduleJob(job, trigger);
                Thread.sleep(10000);
                scheduler.shutdown();
                System.out.println(store);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
            List<Long> store = (List<Long>) context.getJobDetail().getJobDataMap().get("store");
            Connection connect = (Connection) context.getJobDetail().getJobDataMap().get("connect");
            store.add(System.currentTimeMillis());
            try (PreparedStatement stat = connect.prepareStatement(
                    "insert into rabbit(created_date) values(?)")) {
                Date date = new Date(store.get(store.size() - 1));
                stat.setDate(1, date);
                stat.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
}
