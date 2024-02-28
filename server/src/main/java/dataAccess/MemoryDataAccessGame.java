package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class MemoryDataAccessGame implements DataAccessgame {
    final private HashMap<Integer, GameData> games = new HashMap<>();
    private int id = 0;

    @Override
    public ChessGame getGame(Integer ID) throws DataAccessException {
        if (games.containsValue(ID)) {
            return games.get(ID).game();
        } else {
            throw new DataAccessException("game does not exist");
        }
    }

    public Integer createGame(String Gamename) throws DataAccessException {
        games.put(id, new GameData(id, "", "", Gamename, new ChessGame()));
        return id++;
    }

    @Override
    public void updateGame(Integer ID, String White, String Black) throws DataAccessException {
        if (games.containsKey(ID)) {
            var game = games.get(ID);
            String myWhite;
            String myBlack;
            if(Objects.equals(White, "check")){
                myWhite = game.whiteUsername();
                myBlack = Black;
            } else if (Objects.equals(Black, "check")) {
                myBlack = game.blackUsername();
                myWhite=White;
            }
            else{
                myWhite = null;
                myBlack=null;
            }
            if(myBlack==""){
                myBlack=null;
            }
            if(myWhite == ""){
                myWhite=null;
            }
            games.put(ID,new GameData(ID,myWhite,myBlack,game.getgamename(),game.getgame()));
        }
    }

    @Override
    public GameData[] listGames() throws DataAccessException {
        GameData[] data = (games.values().toArray(new GameData[0]));
        return data;
    }

    @Override
    public void deleteAll() throws DataAccessException {
        games.clear();
    }

}
