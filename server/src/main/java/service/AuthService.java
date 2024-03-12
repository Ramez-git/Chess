package service;

import dataAccess.DataAccessAuth;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;

import java.sql.SQLException;

public class AuthService {
    private final DataAccessAuth data;

    public AuthService(DataAccessAuth data) {
        this.data = data;
    }

    public AuthData getAuth(UserData user) throws DataAccessException, SQLException {
        return data.getAuth(user);
    }

    public AuthData getusr(AuthData authtok) throws DataAccessException, SQLException {
        return data.getusr(authtok);
    }

    public void deleteAll() throws DataAccessException, SQLException {
        data.deleteAll();
    }

    public void deleteSession(AuthData auth) throws DataAccessException, SQLException {
        data.deleteSession(auth);
    }

    public AuthData createAuth(UserData user) throws DataAccessException, SQLException {
        return data.createAuth(user);
    }

}
