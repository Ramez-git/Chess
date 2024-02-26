package dataAccess;
import chess.ChessGame;
import model.*;
import java.util.Collection;
public interface DataAccess {
    AuthData createUser(UserData user)throws DataAccessException;
    AuthData loginUser(UserData user) throws DataAccessException;
    void deleteAuth(AuthData authtoken) throws DataAccessException;
    Collection<ChessGame> listGames() throws DataAccessException;
    AuthData getAuth(UserData user) throws DataAccessException;
    void clear() throws DataAccessException;
    ChessGame getGame(Integer ID) throws DataAccessException;
    void updateGame(Integer ID) throws DataAccessException;;
    AuthData createAuth(UserData user) throws DataAccessException;

}