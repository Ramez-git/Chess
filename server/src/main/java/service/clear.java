package service;
import chess.ChessGame;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.AuthData;

import java.util.Collection;
public class clear {
    private DataAccess data;
    public clear(DataAccess data){
        this.data = data;
    }
public void DELETEALL() throws DataAccessException {
    data.clear();
}
public void deleteSession(AuthData auth) throws DataAccessException {
    data.deleteAuth(auth);
}

}
