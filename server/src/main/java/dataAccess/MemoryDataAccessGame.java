package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public class MemoryDataAccessGame implements DataAccessgame {
    public final HashMap<Integer, GameData> games = new HashMap<>();
    private int id = 1;

    @Override
    public ChessGame getGame(Integer ID) throws DataAccessException {
        if (games.containsKey(ID)) {
            return games.get(ID).game();
        } else {
            throw new DataAccessException("game does not exist");
        }
    }

    public Integer createGame(String Gamename) throws DataAccessException {
        if (Gamename == null) {
            throw new DataAccessException("name not provided");
        }
        games.put(id, new GameData(id, null, null, Gamename, new ChessGame()));
        return id++;
    }

    @Override
    public void updateGame(Integer ID, String White, String Black) throws DataAccessException {
        String myWhite;
        String myBlack;
        if (games.containsKey(ID)) {
            var game = games.get(ID);
            updateGame2(ID, White, Black);
            if (!Objects.equals(Black, "check") && game.blackUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            if (!Objects.equals(White, "check") && game.whiteUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            if (Objects.equals(White, "check")) {
                myWhite = game.whiteUsername();
                myBlack = Black;
            } else if (Objects.equals(Black, "check")) {
                myBlack = game.blackUsername();
                myWhite = White;
            } else {
                myWhite = null;
                myBlack = null;
            }
            games.put(ID, new GameData(ID, myWhite, myBlack, game.getgamename(), game.getgame()));
        } else {
            throw new DataAccessException("game does not exist");
        }

    }

    private void updateGame2(Integer ID, String White, String Black) throws DataAccessException {
        String myWhite;
        String myBlack;
        var game = games.get(ID);
        if (White == null && Black == null) {
            if (Objects.equals(game.whiteUsername(), "")) {
                myWhite = null;
            } else {
                myWhite = game.whiteUsername();
            }
            if (Objects.equals(game.blackUsername(), "")) {
                myBlack = null;
            } else {
                myBlack = game.blackUsername();
            }
            games.put(ID, new GameData(ID, myWhite, myBlack, game.getgamename(), game.getgame()));
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return games.values();
    }

    @Override
    public void deleteAll() throws DataAccessException {
        games.clear();
    }

}
