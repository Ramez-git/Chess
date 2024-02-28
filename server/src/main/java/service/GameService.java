package service;

import chess.ChessGame;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;

import java.util.Collection;

public class GameService {
    private DataAccess data;
    public GameService(DataAccess data){
        this.data =data;
    }
    public Collection<ChessGame> listGames() throws DataAccessException {
        return data.listGames();
    }
}
