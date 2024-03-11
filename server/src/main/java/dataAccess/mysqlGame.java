package dataAccess;

import chess.ChessGame;
import model.GameData;

public class mysqlGame implements DataAccessgame{
    @Override
    public ChessGame getGame(Integer ID) throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(Integer ID, String White, String Black) throws DataAccessException {

    }

    @Override
    public Integer createGame(String Gamename) throws DataAccessException {
        return null;
    }

    @Override
    public GameData[] listGames() throws DataAccessException {
        return new GameData[0];
    }

    @Override
    public void deleteAll() throws DataAccessException {

    }
}
