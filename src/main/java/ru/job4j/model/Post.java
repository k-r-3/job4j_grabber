package ru.job4j.model;

public class Post {
    private User user;
    private String heading;
    private String responsibilities;
    private String requirements;
    private String workingConditions;

    public void setUser(User user) {
        this.user = user;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public void setResponsibilities(String responsibilities) {
        this.responsibilities = responsibilities;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public void setWorkingConditions(String workingConditions) {
        this.workingConditions = workingConditions;
    }

    public User getUser() {
        return user;
    }

    public String getHeading() {
        return heading;
    }

    public String getResponsibilities() {
        return responsibilities;
    }

    public String getRequirements() {
        return requirements;
    }

    public String getWorkingConditions() {
        return workingConditions;
    }

    public User getAutor() {
        return user;
    }

    public String getPost() {
        return String.format("%s\r\n%s\r\n%s\r\n mail for communication : %s"
                + "\r\n%s", getHeading(), getRequirements(), getResponsibilities(),
                getUser().getEmail());
    }
}
