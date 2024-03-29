package dataAccess;

import model.AuthData;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class mySqlAuth implements DataAccessAuth {
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auths (
              `username` varchar(256) NOT NULL,
              `authToken` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`),
              INDEX(authToken),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    public mySqlAuth() throws DataAccessException {
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
    public AuthData createAuth(UserData user) throws DataAccessException, SQLException {
        if (user.username() == null) {
            throw new DataAccessException("Error: description");
        }
        var authsdata1 = new AuthData(UUID.randomUUID().toString(), user.username());
        var authToken = authsdata1.authToken();
        var statement = "INSERT INTO auths (username, authToken) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, user.username());
                ps.setString(2, authToken);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException("unable to update database auth");
            }

        }
        return authsdata1;
    }

    @Override
    public AuthData getusr(AuthData authtok) throws DataAccessException, SQLException {
        if (authtok == null) {
            throw new DataAccessException("Error: description");
        }
        var statement = "SELECT username, authToken FROM auths WHERE authToken=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authtok.authToken());
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readauth(rs);
                    } else {
                        throw new SQLException("usr does not exist");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("usr does not exist");
        }

    }

    @Override
    public AuthData getAuth(UserData user) throws DataAccessException, SQLException {
        var statement = "SELECT username, authToken FROM auths WHERE username=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, user.username());
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readauth(rs);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("unable to update database auth");
        }
        return null;
    }

    @Override
    public void deleteSession(AuthData auth) throws DataAccessException, SQLException {
        var statement = "SELECT username, authToken FROM auths WHERE authToken=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, auth.authToken());
                ps.executeQuery();
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var myusr = readauth(rs);
                        if (myusr == null) {
                            throw new DataAccessException("Error: unauthorized");
                        }
                    } else {
                        throw new DataAccessException("Error: unauthorized");
                    }
                }
            }
        }
        statement = "DELETE FROM auths WHERE authToken=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, auth.authToken());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: unauthorized");
        }
    }

    @Override
    public void deleteAll() throws DataAccessException, SQLException {
        var statement = "TRUNCATE TABLE auths";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        }
    }

    private AuthData readauth(ResultSet rss) throws SQLException {
        var usrname = rss.getString("username");
        var authtok = rss.getString("authToken");
        return new AuthData(authtok, usrname);
    }
}
