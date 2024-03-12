package dataAccess;

import model.AuthData;
import model.UserData;

import java.sql.SQLException;

public interface DataAccessAuth {
    void deleteAuth(AuthData authtoken) throws DataAccessException;

    AuthData createAuth(UserData user) throws DataAccessException, SQLException;

    AuthData getusr(AuthData authtok) throws DataAccessException, SQLException;

    AuthData getAuth(UserData user) throws DataAccessException, SQLException;

    void deleteSession(AuthData auth) throws DataAccessException, SQLException;

    void deleteAll() throws DataAccessException, SQLException;
}
