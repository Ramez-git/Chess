package model;

import com.google.gson.Gson;

public record UserData(String username, String password, String email) {


    public UserData UserData(String username, String password, String email) {
        return new UserData(username, password, email);
    }

    public String username() {
        return this.username;
    }

    public String password() {
        return this.password;
    }

    public String email() {
        return this.email;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}