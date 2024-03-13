package dataAccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MemoryDataAccessAuth implements DataAccessAuth {
    public final HashMap<String, AuthData> auths = new HashMap<>();

    public AuthData createAuth(UserData user) throws DataAccessException {
        if(user.username()==null){
            throw new DataAccessException("Error: description");
        }
        var authmyuser = new AuthData(UUID.randomUUID().toString(), user.username());
        auths.put(authmyuser.authToken(), authmyuser);
        return authmyuser;
    }


    private void deleteAuth(AuthData authtoken) throws DataAccessException {
        if (auths.get(authtoken.authToken()) != null) {
            auths.remove(authtoken.authToken());
        } else {
            throw new DataAccessException("User not logged in thus his authtoken can't be deleted");
        }
    }


    public AuthData getAuth(UserData user) throws DataAccessException {
        for (Map.Entry<String, AuthData> entry : auths.entrySet()) {
            String authToken = entry.getKey();
            AuthData authData = entry.getValue();
            if (Objects.equals(authData.username(), user.username())) {
                return authData;
            }
        }
        throw new DataAccessException("User is not logged in getAuth");

    }

    @Override
    public void deleteSession(AuthData auth) throws DataAccessException {
        int yup = 0;
        if (auth.getauthtoken() == null) {
            throw new DataAccessException("Error: description");
        }
        if (auths.containsKey(auth.authToken())) {
            auths.remove(auth.authToken());
            yup = 1;
        }
        if (yup == 0) {
            throw new DataAccessException("Error: unauthorized");
        }
    }

    @Override
    public void deleteAll() throws DataAccessException {
        auths.clear();
    }


    public AuthData getusr(AuthData authtok) throws DataAccessException {
        if (auths.containsKey(authtok.authToken())) {
            return auths.get(authtok.authToken());
        } else {
            throw new DataAccessException("usr does not exist");
        }

    }
}
