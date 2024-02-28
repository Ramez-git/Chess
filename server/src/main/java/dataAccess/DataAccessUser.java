package dataAccess;

import model.AuthData;
import model.UserData;

public interface DataAccessUser {
    AuthData CreateUser(UserData user) throws DataAccessException;

    AuthData login(UserData user) throws DataAccessException;

    void deleteAll() throws DataAccessException;

}