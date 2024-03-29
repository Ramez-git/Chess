package server;

import com.google.gson.Gson;
import com.google.protobuf.StringValue;
import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:"+ String.valueOf(port) +"/";
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");//auth here
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    public AuthData register(UserData user) throws ResponseException {
        return this.makeRequestwithbody("POST", "/user", user, AuthData.class);
    }

    public AuthData login(UserData user) throws ResponseException {
        return this.makeRequestwithbody("POST", "/session", user, AuthData.class);
    }

    public String creategame(Object gamename, String auth) throws ResponseException {
        this.makeRequestwithauthandbody("POST", "/game", gamename, Object.class, auth);
        return "success";
    }

    public Object listgames(String auth) throws ResponseException {
        return this.makeRequestwithoutbody("GET", "/game", Object.class, auth);
    }

    public Object joingame(String auth, Object color) throws ResponseException {
        return this.makeRequestwithauthandbody("PUT", "/game", color, Object.class, auth);
    }

    public Object observer(String auth, Object ID) throws ResponseException {
        this.makeRequestwithauthandbody("PUT", "/game", ID, Object.class, auth);
        return "success";
    }

    public void logout(String auth) throws ResponseException {
        this.makeRequestwithoutbody("DELETE", "/session", Object.class, auth);
    }

    private <T> T makeRequestwithbody(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            writeBody(request, http);

            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private <T> T makeRequestwithoutbody(String method, String path, Class<T> responseClass, String auth) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", auth);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private <T> T makeRequestwithauthandbody(String method, String path, Object request, Class<T> responseClass, String auth) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            http.addRequestProperty("Authorization", auth);
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
            http.connect();

            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
