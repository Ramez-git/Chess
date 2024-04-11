package ui;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import dataAccess.*;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import model.wrapper;
import server.Server;
import server.ServerFacade;

import java.util.*;

import static ui.EscapeSequences.*;
import static ui.states.LOGEDOUT;

public class UIclient {
    boolean quit = false;
    public states clientstate = LOGEDOUT;
    public AuthData auth;
    public String game;
    public Server server;
    public ChessGame mygame;
    public final reply reply;
    public UIclient(String URL, ui.reply reply) throws DataAccessException {
        this.reply = reply;
        this.server = new Server();

    }

}
