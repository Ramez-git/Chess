package dataAccess;

import model.AuthData;
import model.UserData;
import service.AuthService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class mySqlUser implements DataAccessUser {
    final AuthService authService;
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
        } else {
            try {
                var myuser = getusr(user.username());
                if (myuser != null) {
                    throw new DataAccessException("Error: already taken");
                } else {
                    var statement = "INSERT INTO user (name,password,email) VALUES (?, ?, ?)";
                    try (var conn = DatabaseManager.getConnection()) {
                        try (var ps = conn.prepareStatement(statement)) {
                            ps.setString(1, user.username());
                            ps.setString(2, user.password());
                            ps.setString(3, user.email());
                            ps.executeUpdate();
                            var auth = authService;
                            var auth1 = auth.createAuth(user);
                            return auth1;
                        }
                    } catch (SQLException | DataAccessException e) {
                        throw new DataAccessException("unable to update database user");
                    }
                }
            } catch (DataAccessException e) {
                throw new DataAccessException("unable to read database user");
            }
        }
    }

    @Override
    public AuthData login(UserData user) throws DataAccessException {
        if (user.username() == null || user.password() == null) {
            throw new DataAccessException("Error: description");}
        else{
            try{
                var myusr = getusr(user.username());
                if(Objects.equals(myusr.password(), user.password())){
                    var auth = authService;
                    var auth1 = auth.createAuth(myusr);
                    return auth1;
                }
                else{
                    throw new DataAccessException("Error: unauthorized");
                }
            } catch (DataAccessException e) {
                throw new DataAccessException("err to fetch user from DB user");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void deleteAll() throws DataAccessException, SQLException {
        var statement = "TRUNCATE TABLE user";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        }
    }

    public UserData getusr(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password FROM user WHERE name=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readusr(rs);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("un able to read user");
        }
        return null;
    }

    private UserData readusr(ResultSet rss) throws SQLException {
        var usrname = rss.getString("name");
        var password = rss.getString("password");
        var email = rss.getString("email");
        return new UserData(usrname, password, email);
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
