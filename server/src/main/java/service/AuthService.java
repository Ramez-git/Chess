package service;

import dataAccess.*;
import model.AuthData;
import model.UserData;
import dataAccess.DataAccessAuth;

public class AuthService {
    private DataAccessAuth data;
    public AuthService(DataAccessAuth data){
        this.data = data;
    }
    AuthData getAuth(UserData user) throws DataAccessException{
        return data.getAuth(user);
    }
    public AuthData getusr(AuthData authtok) throws DataAccessException {
        return data.getusr(authtok);
    }
    public void deleteAll() throws DataAccessException {
        data.deleteAll();
    }
    public void deleteSession(AuthData auth) throws DataAccessException {
        data.deleteSession(auth);
    }
    public AuthData createAuth(UserData user) throws DataAccessException{
        return data.createAuth(user);
    }

}
