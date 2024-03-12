package dataAccess;

import com.mysql.cj.protocol.Resultset;
import model.*;
import service.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class mySqlUser implements DataAccessUser{
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user (
              `name` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`name`),
              INDEX(name)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };
    final AuthService authService;
    public mySqlUser(AuthService authService) throws DataAccessException {
        this.authService = authService;
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database");
        }
    }

    @Override
    public AuthData CreateUser(UserData user) throws DataAccessException {
        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new DataAccessException("Error: bad request");
        }
        else{
            try{
                var myuser = getusr(user.username());
            } catch (DataAccessException e) {
                throw new RuntimeException(e);
            }
        }
         else if (users.get(user.username()) != null) {
            throw new DataAccessException("Error: already taken");
        } else {
            users.put(user.username(), user);
            var auth = authService;
            var auth1 = auth.createAuth(user);
            return auth1;
        }
    }

    @Override
    public AuthData login(UserData user) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAll() throws DataAccessException {

    }
    public UserData getusr(String username) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()){
            var statement = "SELECT username, password FROM user WHERE name=?";
            try(var ps = conn.prepareStatement(statement)){
                ps.setString(1,username);
                try(var rs = ps.executeQuery()){
                    if(rs.next()){
                        return readusr(rs);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException();
        }
        return null;
    }
    private UserData readusr(ResultSet rss) throws SQLException{
        var usrname = rss.getString("name");
        var password = rss.getString("password");
        var email = rss.getString("email");
        return new UserData(usrname,password,email);
    }
//    public Collection<String> usersinDB() throws DataAccessException {
//        var stamment = "SELECT name from user";
//        Collection<String> res= new ArrayList<>();
//        try(var connection = DatabaseManager.getConnection(); var ps = connection.prepareStatement(stamment); var result = ps.executeQuery()){
//            while(result.next()){
//                res.add(result.getString("name"));
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return res;
//    }
}
