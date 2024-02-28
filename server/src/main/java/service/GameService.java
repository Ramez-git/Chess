package service;

import chess.ChessGame;
import dataAccess.DataAccessUser;
import dataAccess.DataAccessException;
import dataAccess.DataAccessgame;
import model.AuthData;

import java.util.Collection;

public class GameService implements DataAccessgame{
    private DataAccessgame data;
    public GameService(DataAccessgame data){
        this.data =data;
    }
    public Collection<ChessGame> listGames() throws DataAccessException {
        return data.listGames();
    }

    @Override
    public void deleteSession(AuthData auth) throws DataAccessException {
    data.deleteAll();
    }

    @Override
    public ChessGame getGame(Integer ID) throws DataAccessException {
        return data.getGame(ID);
    }

    @Override
    public void updateGame(Integer ID, String White, String Black) throws DataAccessException {
        data.updateGame(ID, White, Black);
    }

    public Integer createGame(String Gamename) throws DataAccessException{
        return data.createGame(Gamename);
    }
    public void deleteAll() throws DataAccessException{
        data.deleteAll();
    }

}
