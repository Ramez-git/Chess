package dataAccess;

import model.AuthData;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import service.AuthService;

import java.sql.ResultSet;
import java.sql.SQLException;

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
        }
        var myuser = getusr(user.username());
        if (myuser != null) {
            throw new DataAccessException("Error: already taken");
        } else {
            var statement = "INSERT INTO user (name,password,email) VALUES (?, ?, ?)";
            try (var conn = DatabaseManager.getConnection()) {
                try (var ps = conn.prepareStatement(statement)) {
                    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                    String hashedPassword = encoder.encode(user.password());
                    ps.setString(1, user.username());
                    ps.setString(2, hashedPassword);
                    ps.setString(3, user.email());
                    ps.executeUpdate();
                    var auth = authService;
                    var auth1 = auth.createAuth(user);
                    return auth1;
                }
            } catch (SQLException e) {
                throw new DataAccessException("unable to update database user");
            }

        }
    }

    @Override
    public AuthData login(UserData user) throws DataAccessException {
        if (user.username() == null || user.password() == null) {
            throw new DataAccessException("Error: description");
        }
        try {
            var myusr = getusr(user.username());
            if (myusr == null) {
                throw new DataAccessException("Error: unauthorized");
            }
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (encoder.matches(user.password(), myusr.password())) {
                var auth = authService;
                var auth1 = auth.createAuth(myusr);
                return auth1;
            } else {
                throw new DataAccessException("Error: unauthorized");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
            var statement = "SELECT name, password, email FROM user WHERE name=?";
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
}
