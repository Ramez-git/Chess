package dataAccess;
import model.*;

public interface DataAccessUser {
    AuthData createUser(UserData user)throws DataAccessException;
    AuthData loginUser(UserData user) throws DataAccessException;

}