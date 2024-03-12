package dataAccess;

import model.AuthData;
import model.UserData;

import java.sql.SQLException;

public interface DataAccessUser {
    AuthData CreateUser(UserData user) throws DataAccessException, SQLException;

    AuthData login(UserData user) throws DataAccessException, SQLException;

    void deleteAll() throws DataAccessException, SQLException;

}