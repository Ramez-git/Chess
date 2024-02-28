package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class MemoryDataAccessGame implements DataAccessgame{
    final private HashMap<Integer, GameData> games = new HashMap<>();
    private int id = 0;
    @Override
    public ChessGame getGame(Integer ID) throws DataAccessException {
        if(games.containsValue(ID)){
        return games.get(ID).game();
        }
        else{
            throw new DataAccessException("game does not exist");
        }
    }
    public Integer createGame(String Gamename) throws DataAccessException{
        games.put(id,new GameData(id,"","",Gamename,new ChessGame()));
        return id++;
    }
    @Override
    public void updateGame(Integer ID,String White,String Black) throws DataAccessException {
        if(games.containsKey(ID)){
        var game = games.get(ID);
            if (Black == "check") {

        games.put(ID,new GameData(ID,White,game.blackUsername(),game.getgamename(),game.getgame()));}
            else{
                games.put(ID,new GameData(ID, game.whiteUsername(), Black,game.getgamename(),game.getgame()));
            }
        }
        else{
            throw new DataAccessException("err on updategame");
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> g = new HashSet<>();
        for(var i : games.values()){
            g.add(i);
        }
        return g;
    }

    @Override
    public void deleteAll() throws DataAccessException {

    }

}
