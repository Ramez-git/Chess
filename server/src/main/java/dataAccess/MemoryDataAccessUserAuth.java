package dataAccess;

import chess.ChessGame;
import model.AuthData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryDataAccessUserAuth implements DataAccessAuth {
    final private HashMap<String, AuthData> auths = new HashMap<>();
    public AuthData createAuth(UserData user) throws DataAccessException {
        if (auths.get(user.username()) == null) {
            var authmyuser = new AuthData(UUID.randomUUID().toString(), user.username());
            auths.put(authmyuser.username(), authmyuser);
            return authmyuser;
        } else {
            throw new DataAccessException("User already logged in");
        }
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

    }

    @Override
    public void deleteAll() throws DataAccessException {

    }


    public AuthData getusr(AuthData authtok) throws DataAccessException{
        for (Map.Entry<String, AuthData> entry : auths.entrySet()) {
            String key = entry.getKey();
            AuthData value = entry.getValue();
            if(value.authToken() == authtok.authToken()){
                return value;
            }
        }
        return null;
    }
}
