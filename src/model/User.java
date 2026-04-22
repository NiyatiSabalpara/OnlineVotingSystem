package model;

public class User {

    private String voterId;
    private String name;
    private String mobile;
    private String email;
    private String password;
    private boolean hasVoted;

    public User(String voterId, String name, String mobile, String email, String password) {
        this.voterId = voterId;
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.password = password;
        this.hasVoted = false;
    }

    public String getVoterId() {
        return voterId;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean hasVoted() {
        return hasVoted;
    }

    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }
}
