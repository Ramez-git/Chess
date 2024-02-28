package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface DataAccessgame {
    ChessGame getGame(Integer ID) throws DataAccessException;

    void updateGame(Integer ID, String White, String Black) throws DataAccessException;

    Integer createGame(String Gamename) throws DataAccessException;

    GameData[] listGames() throws DataAccessException;

    void deleteAll() throws DataAccessException;
}
