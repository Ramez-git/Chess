package dataAccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class MemoryDataAccessGame implements DataAccessgame{
    final private HashMap<Integer, GameData> games = new HashMap<>();
    private int id = 0;
    @Override
    public ChessGame getGame(Integer ID) throws DataAccessException {
        return null;
    }
    public Integer createGame(String Gamename) throws DataAccessException{
        games.put(id,new GameData(id,"","",Gamename,new ChessGame()));
        return id++;
    }
    @Override
    public void updateGame(Integer ID) throws DataAccessException {

    }

    @Override
    public Collection<ChessGame> listGames() throws DataAccessException {
        Collection<ChessGame> g = new HashSet<>();
        for(var i : games.values()){
            g.add(i.game());
        }
        return g;
    }

    @Override
    public void deleteSession(AuthData auth) throws DataAccessException {

    }

}
