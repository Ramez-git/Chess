package dataAccess;
import model.*;

public interface DataAccessUser {
    AuthData CreateUser(UserData user)throws DataAccessException;
    AuthData login(UserData user) throws DataAccessException;
    public void deleteAll() throws DataAccessException;

}