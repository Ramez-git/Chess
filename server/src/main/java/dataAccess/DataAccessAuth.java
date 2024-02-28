package dataAccess;

import model.AuthData;
import model.UserData;

public interface DataAccessAuth {
    void deleteAuth(AuthData authtoken) throws DataAccessException;

    AuthData createAuth(UserData user) throws DataAccessException;

    AuthData getusr(AuthData authtok) throws DataAccessException;

    AuthData getAuth(UserData user) throws DataAccessException;

    void deleteSession(AuthData auth) throws DataAccessException;

    void deleteAll() throws DataAccessException;
}
