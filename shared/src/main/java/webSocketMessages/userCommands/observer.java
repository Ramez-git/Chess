package webSocketMessages.userCommands;

public class observer extends UserGameCommand {
    public int gameID;
    public observer(String authToken, int gameID) {
        super(authToken);
        this.commandType = CommandType.JOIN_OBSERVER;
        this.gameID = gameID;
    }
}
