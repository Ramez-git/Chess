package dataAccess;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;

import java.sql.SQLException;
import java.util.UUID;

public class mySqlAuth implements DataAccessAuth{
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auths (
              `authtoken` varchar(256) NOT NULL,
              `authsdata` TEXT DEFAULT NULL,
              PRIMARY KEY (`name`),
              INDEX(name)
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
    public void deleteAuth(AuthData authtoken) throws DataAccessException {

    }

    @Override
    public AuthData createAuth(UserData user) throws DataAccessException {
        var authsdata1 = new AuthData(UUID.randomUUID().toString(), user.username());
        var authsdata = new Gson().toJson(authsdata1);
        var authToken = authsdata1.authToken();
        var statement ="INSERT INTO auths (authToken, authsdata) VALUES (?, ?)";
        return authsdata1;
    }

    @Override
    public AuthData getusr(AuthData authtok) throws DataAccessException {
        return null;
    }

    @Override
    public AuthData getAuth(UserData user) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteSession(AuthData auth) throws DataAccessException {

    }

    @Override
    public void deleteAll() throws DataAccessException {

    }
}
