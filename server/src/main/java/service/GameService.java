package service;

import chess.ChessGame;
import dataAccess.DataAccessUser;
import dataAccess.DataAccessException;
import dataAccess.DataAccessgame;

import java.util.Collection;

public class GameService {
    private DataAccessgame data;
    public GameService(DataAccessgame data){
        this.data =data;
    }
    public Collection<ChessGame> listGames() throws DataAccessException {
        return data.listGames();
    }
    public Integer createGame(String Gamename) throws DataAccessException{
        return data.createGame(Gamename);
    }
    public void deleteAll() throws DataAccessException{
        data.deleteAll();
    }
}
