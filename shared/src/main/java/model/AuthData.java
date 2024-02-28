package model;

import com.google.gson.Gson;

public record AuthData(String authToken, String username){
    public AuthData makeAuth(String authToken, String username){
        return new AuthData(authToken,username);
    }
    public String toString() {
        return new Gson().toJson(this);
    }
    public String username(){
        return this.username;
    }
    public String authtoken(){
        return this.authToken;
    }
}