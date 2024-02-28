package dataAccess;

import chess.ChessGame;
import model.AuthData;

import java.util.Collection;

public interface DataAccessgame {
    ChessGame getGame(Integer ID) throws DataAccessException;
    void updateGame(Integer ID) throws DataAccessException;;
    public Integer createGame(String Gamename) throws DataAccessException;
    Collection<ChessGame> listGames() throws DataAccessException;
    public void deleteSession(AuthData auth) throws DataAccessException;
    public void deleteAll() throws DataAccessException;
}
