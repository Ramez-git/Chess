package service;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.DataAccessgame;
import model.GameData;

import java.sql.SQLException;
import java.util.Collection;

public class GameService implements DataAccessgame {
    private final DataAccessgame data;

    public GameService(DataAccessgame data) {
        this.data = data;
    }

    public Collection<GameData> listGames() throws DataAccessException, SQLException {
        return data.listGames();
    }

    @Override
    public ChessGame getGame(Integer ID) throws DataAccessException {
        return data.getGame(ID);
    }

    @Override
    public void updateGame(Integer ID, String White, String Black) throws DataAccessException {
        data.updateGame(ID, White, Black);
    }

    public Integer createGame(String Gamename) throws DataAccessException, SQLException {
        return data.createGame(Gamename);
    }

    public void deleteAll() throws DataAccessException, SQLException {
        data.deleteAll();
    }

}
