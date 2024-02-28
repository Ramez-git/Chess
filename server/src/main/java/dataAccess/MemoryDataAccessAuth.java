package dataAccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MemoryDataAccessAuth implements DataAccessAuth {
    final private HashMap<String, AuthData> auths = new HashMap<>();
    public AuthData createAuth(UserData user) throws DataAccessException {
            var authmyuser = new AuthData(UUID.randomUUID().toString(), user.username());
            auths.put(authmyuser.username(), authmyuser);
            return authmyuser;
    }


    public void deleteAuth(AuthData authtoken) throws DataAccessException {
        if(auths.get(authtoken.username())!=null){
            auths.remove(authtoken.username());
        }
        else{
            throw new DataAccessException("User not logged in thus his authtoken can't be deleted");
        }
    }


    public AuthData getAuth(UserData user) throws DataAccessException {
        if(auths.get(user.username())!=null){
            return auths.get(user.username());
        }
        else{
            throw new DataAccessException("User is not logged in getAuth");
        }
    }

    @Override
    public void deleteSession(AuthData auth) throws DataAccessException {
        int yup = 0;
        if(auth.getauthtoken()==null){
            throw new DataAccessException("Error: description");
        }
        for (Map.Entry<String, AuthData> entry : auths.entrySet()) {
            String username = entry.getKey();
            AuthData authData = entry.getValue();
            if(Objects.equals(authData.getauthtoken(), auth.getauthtoken())){
                auths.remove(authData.username());
                yup=1;
                break;
            }
    }
        if(yup==0){
            throw new DataAccessException("Error: unauthorized");
        }
    }

    @Override
    public void deleteAll() throws DataAccessException {
    auths.clear();
    }


    public AuthData getusr(AuthData authtok) throws DataAccessException{
        for (Map.Entry<String, AuthData> entry : auths.entrySet()) {
            String key = entry.getKey();
            AuthData value = entry.getValue();
            if(Objects.equals(value.authToken(), authtok.getauthtoken())){
                return value;
            }
        }
        return null;
    }
}
