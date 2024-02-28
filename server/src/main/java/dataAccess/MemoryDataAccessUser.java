package dataAccess;
import model.*;
import service.AuthService;

import java.util.HashMap;
import java.util.Objects;

public class MemoryDataAccessUser implements DataAccessUser {
    final private HashMap<String, UserData> users = new HashMap<>();
    private AuthService authService;

    // Constructor that accepts AuthService
    public MemoryDataAccessUser(AuthService authService) {
        this.authService = authService;
    }
    @Override
    public AuthData createUser(UserData user) throws DataAccessException {
        if(users.get(user.username())==null){
            users.put(user.username(), user);
            var auth = authService;
            var auth1 = auth.createAuth(user);
            return auth1;
        }
        else if(users.get(user.username())!=null){
            throw new DataAccessException("Error: already taken");}
            else{
            throw new DataAccessException("Error: bad request");
        }
    }
    @Override
    public AuthData loginUser(UserData user) throws DataAccessException {
        if(users.get(user.username())!=null&& Objects.equals(users.get(user.username()).password(), user.password())){
            var auth = authService;
            var auth1 = auth.createAuth(user);
            return auth1;
        }
        else if(user.username()==null || user.password()==null){
            throw new DataAccessException("Error: description");
        }
        else if (users.get(user.username())==null){
            throw new DataAccessException("Error: unauthorized");
        }

        return null;
    }

}
