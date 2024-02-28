package service;

import dataAccess.DataAccessUser;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;

public class UserService {
    private DataAccessUser data;
    public UserService(DataAccessUser data){

        this.data = data;
    }
    public AuthData CreateUser(UserData user) throws DataAccessException {

        return data.createUser(user);
    }
    public AuthData login(UserData user) throws DataAccessException{
        return data.loginUser(user);
    }

}
