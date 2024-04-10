package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import service.AuthService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class mysqlGame implements DataAccessgame {
    final AuthService authService;
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS games (
              `id` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256) DEFAULT NULL,
              `blackUsername` varchar(256) DEFAULT NULL,
              `gameName` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };
    int ID = 0;

    public mysqlGame(AuthService authService) throws DataAccessException {
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
    public ChessGame getGame(Integer ID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password FROM games WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, String.valueOf(ID));
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readgame(rs).game();
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("unable to read game");
        }
        return null;
    }

    public GameData getGamefull(Integer ID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, whiteUsername,blackUsername,gameName,json FROM games WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, ID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readgame(rs);
                    } else {
                        throw new DataAccessException("game does not exist");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("unable to read game");
        }

    }

    @Override
    public void updateGame(Integer ID, String White, String Black) throws DataAccessException {
        try {
            var mygame = getGamefull(ID);
        } catch (DataAccessException e) {
            throw new DataAccessException("game does not exist");
        }
        var mygame = getGamefull(ID);
        String myWhite;
        String myBlack;
        if (White == null && Black == null) {
            if (Objects.equals(mygame.whiteUsername(), "")) {
                myWhite = null;
            } else {
                myWhite = mygame.whiteUsername();
            }
            if (Objects.equals(mygame.blackUsername(), "")) {
                myBlack = null;
            } else {
                myBlack = mygame.blackUsername();
            }
        }
        if (!Objects.equals(Black, "check") && mygame.blackUsername() != null) {
            throw new DataAccessException("Error: already taken");
        }
        if (!Objects.equals(White, "check") && mygame.whiteUsername() != null) {
            throw new DataAccessException("Error: already taken");
        }
        if (Objects.equals(White, "check")) {
            myWhite = mygame.whiteUsername();
            myBlack = Black;
        } else if (Objects.equals(Black, "check")) {
            myBlack = mygame.blackUsername();
            myWhite = White;
        } else {
            myWhite = null;
            myBlack = null;
        }
        var statement = "UPDATE games SET whiteUsername=?, blackUsername=? WHERE id =?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, myWhite);
                ps.setString(2, myBlack);
                ps.setInt(3, mygame.gameID());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Integer createGame(String Gamename) throws DataAccessException, SQLException {
        if (Gamename == null) {
            throw new DataAccessException("name not provided");
        }
        var statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, json) VALUES (?, ?, ?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                ps.setString(1, null);
                ps.setString(2, null);
                ps.setString(3, Gamename);
                ps.setString(4, new Gson().toJson(new ChessGame()));
                ps.executeUpdate();
                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("autoincrement err");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: bad request");
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException, SQLException {
        var result = new ArrayList<GameData>();
        var statement = "SELECT id,whiteUsername,blackUsername,gameName,json FROM games";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readgame(rs));
                    }
                }
            }
        }
//        GameData[] resultt = result.toArray(new GameData[0]);
        return result;
    }

    @Override
    public void deleteAll() throws DataAccessException, SQLException {
        var statement = "TRUNCATE TABLE games";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        }
    }

    private GameData readgame(ResultSet res) throws SQLException {
        var ID = res.getInt("id");
        var white = res.getString("whiteUsername");
        var black = res.getString("blackUsername");
        var gameName = res.getString("gameName");
        var game = new Gson().fromJson(res.getString("json"), ChessGame.class);
        return new GameData(ID, white, black, gameName, game);
    }
}