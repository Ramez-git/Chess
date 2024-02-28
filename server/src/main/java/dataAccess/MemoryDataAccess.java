package dataAccess;

import chess.ChessGame;
import model.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class MemoryDataAccess implements DataAccess {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemoryDataAccess that = (MemoryDataAccess) o;
        return Objects.equals(users, that.users) && Objects.equals(games, that.games) && Objects.equals(auths, that.auths);
    }

    @Override
    public int hashCode() {
        return Objects.hash(users, games, auths);
    }

    final private HashMap<String, UserData> users = new HashMap<>();
    final private HashMap<Integer, GameData> games = new HashMap<>();
    final private HashMap<String, AuthData> auths = new HashMap<>();


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

    @Override
    public void deleteAuth(AuthData authtoken) throws DataAccessException {
        if(auths.get(authtoken.username())!=null){
            auths.remove(authtoken.username());
        }
        else{
            throw new DataAccessException("User not logged in thus his authtoken can't be deleted");
        }
    }

    @Override
    public Collection<ChessGame> listGames() throws DataAccessException {
        return null;
    }

    @Override
    public AuthData getAuth(UserData user) throws DataAccessException {
        if(auths.get(user.username())!=null){
            return auths.get(user.username());
        }
        else{
            throw new DataAccessException("User is not logged in getAuth");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        users.clear();
        games.clear();
        auths.clear();
    }

    @Override
    public ChessGame getGame(Integer ID) throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(Integer ID) throws DataAccessException {

    }

    @Override
    public AuthData createAuth(UserData user) throws DataAccessException {
        if (auths.get(user.username()) == null) {
            var authmyuser = new AuthData(UUID.randomUUID().toString(), user.username());
            auths.put(authmyuser.username(), authmyuser);
            return authmyuser;
        } else {
            throw new DataAccessException("User already logged in");
        }
    }
}
