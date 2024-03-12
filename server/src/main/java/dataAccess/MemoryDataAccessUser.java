package dataAccess;

import model.AuthData;
import model.UserData;
import service.AuthService;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;

public class MemoryDataAccessUser implements DataAccessUser {
    public final HashMap<String, UserData> users = new HashMap<>();
    final AuthService authService;

    // Constructor that accepts AuthService
    public MemoryDataAccessUser(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public AuthData CreateUser(UserData user) throws DataAccessException, SQLException {
        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new DataAccessException("Error: bad request");
        } else if (users.get(user.username()) != null) {
            throw new DataAccessException("Error: already taken");
        } else {
            users.put(user.username(), user);
            var auth = authService;
            var auth1 = auth.createAuth(user);
            return auth1;
        }
    }

    @Override
    public AuthData login(UserData user) throws DataAccessException, SQLException {
        if (users.get(user.username()) != null && Objects.equals(users.get(user.username()).password(), user.password())) {
            var auth = authService;
            var auth1 = auth.createAuth(user);
            return auth1;
        } else if (user.username() == null || user.password() == null) {
            throw new DataAccessException("Error: description");
        } else if (users.get(user.username()) == null || !Objects.equals(users.get(user.username()).password(), user.password())) {
            throw new DataAccessException("Error: unauthorized");
        }

        return null;
    }

    @Override
    public void deleteAll() throws DataAccessException {
        users.clear();
    }

}
