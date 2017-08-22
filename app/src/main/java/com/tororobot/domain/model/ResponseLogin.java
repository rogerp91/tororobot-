package com.tororobot.domain.model;

/**
 * Created by Roger Patiño on 23/09/2016.
 */

public class ResponseLogin {

//    @SerializedName("token")
    private String token;

//    @SerializedName("user")
    private User user;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}