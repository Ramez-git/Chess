package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.sql.SQLException;

public interface DataAccessgame {
    ChessGame getGame(Integer ID) throws DataAccessException;

    void updateGame(Integer ID, String White, String Black) throws DataAccessException;

    Integer createGame(String Gamename) throws DataAccessException, SQLException;

    GameData[] listGames() throws DataAccessException, SQLException;

    void deleteAll() throws DataAccessException, SQLException;
}
