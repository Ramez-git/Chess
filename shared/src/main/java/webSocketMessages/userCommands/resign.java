package webSocketMessages.userCommands;

public class resign extends UserGameCommand {
    public int gameID;
    public resign(String authToken, int gameID) {
        super(authToken);
        this.commandType = CommandType.RESIGN;
        this.gameID = gameID;
    }
}
