package model;

import chess.ChessGame;
import com.google.gson.Gson;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {

    public GameData makeGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    public String toString() {
        return new Gson().toJson(this);
    }

    public ChessGame getgame() {
        return game;
    }

    public String getgamename() {
        return gameName;
    }
}