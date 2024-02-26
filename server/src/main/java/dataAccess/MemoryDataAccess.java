package dataAccess;
import chess.ChessGame;
import model.*;

import java.util.Collection;
import java.util.HashMap;

public class MemoryDataAccess implements DataAccess{
    final private HashMap<Integer, UserData> users = new HashMap<>();
    private int IDnum = 1;

    @Override
    public AuthData createUser(UserData user) throws DataAccessException {
        var userr = new UserData(user.Username(),user.Password(),user.email());
        var authmyuser=new AuthData(Integer.toString(IDnum),userr.Username());
        users.put(IDnum++,userr);
        return authmyuser;
    }

    @Override
    public AuthData loginUser(UserData user) throws DataAccessException {
        return user;
    }

    @Override
    public void deleteAuth(AuthData authtoken) throws DataAccessException {

    }

    @Override
    public Collection<ChessGame> listGames() throws DataAccessException {
        return null;
    }

    @Override
    public AuthData getAuth(UserData user) throws DataAccessException {
        return null;
    }

    @Override
    public void clear() throws DataAccessException {

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
        return null;
    }
}
