package model;

import com.google.gson.annotations.SerializedName;

public class wrapper {
    @SerializedName("playerColor")
    private String playerColor;
    @SerializedName("gameID")
    private String gameID;

    public wrapper(String playerColor, String gameID) {
        this.playerColor = playerColor;
        this.gameID = gameID;
    }
    public String getGameID() {
        return gameID;
    }
}
