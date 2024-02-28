package dataAccess;
import model.*;

import java.util.HashMap;
import java.util.Objects;

public class MemoryDataAccessUserUser implements DataAccessUser {
    final private HashMap<String, UserData> users = new HashMap<>();
    @Override
    public AuthData createUser(UserData user) throws DataAccessException {

        if (users.get(user.username())==(null)) {
            var authmyuser = createAuth(user);
            users.put(user.username(), user);
            return authmyuser;
        } else if (users.get(user.username()) != null) {
            throw new DataAccessException("Error: already taken");
        } else if (user.username() == null || user.password() == null || user.email() == null) {
            throw new DataAccessException("Error: description");
        } else {
            throw new DataAccessException("Error: bad request");
        }
    }
    @Override
    public AuthData loginUser(UserData user) throws DataAccessException {
        if (users.get(user.username()) != null) {
            var authmyuser = createAuth(user);
            return authmyuser;
        } else if (!Objects.equals(users.get(user.username()).password(), user.password())) {
            throw new DataAccessException("Error: unauthorized");
        } else {
            throw new DataAccessException("Error: description");
        }
    }

}
