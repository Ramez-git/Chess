package model;
import com.google.gson.*;
public record UserData(String Username,String Password,String email) {


public UserData makeUser(String Username,String Password,String email){
    return new UserData(Username,Password,email);
}
public String toString(){
    return new Gson().toJson(this);
}
}