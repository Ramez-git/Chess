package service;

import chess.ChessGame;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;

import java.util.Collection;

public class UserService {
    private DataAccess data;
    public UserService(DataAccess data){

        this.data = data;
    }
    public AuthData CreateUser(UserData user) throws DataAccessException {

        return data.createUser(user);
    }
    public AuthData login(UserData user) throws DataAccessException{
        return data.loginUser(user);
    }

}
