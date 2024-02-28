package service;

import dataAccess.DataAccessException;
import dataAccess.DataAccessUser;
import model.AuthData;
import model.UserData;

public class UserService implements DataAccessUser {
    private final DataAccessUser data;

    public UserService(DataAccessUser data) {

        this.data = data;
    }

    public AuthData CreateUser(UserData user) throws DataAccessException {

        return data.CreateUser(user);
    }

    public AuthData login(UserData user) throws DataAccessException {
        return data.login(user);
    }

    @Override
    public void deleteAll() throws DataAccessException {
        data.deleteAll();
    }

}
