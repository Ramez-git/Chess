package dataAccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;

import java.util.Collection;

public interface DataAccessgame {
    ChessGame getGame(Integer ID) throws DataAccessException;
    public void updateGame(Integer ID,String White,String Black) throws DataAccessException;
    public Integer createGame(String Gamename) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;

    public void deleteAll() throws DataAccessException;
}