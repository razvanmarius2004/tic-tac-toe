package ro.randr.tictactoe.Observables;

import java.util.Observable;

import ro.randr.tictactoe.Enums.ConnectionState;

public class ConnectionStateObservable extends Observable {
    private ConnectionState connectionState;
    private String endPointId;
    private static ConnectionStateObservable instance;

    private ConnectionStateObservable() {
    }

    public static ConnectionStateObservable getInstance() {
        if (instance == null) {
            instance = new ConnectionStateObservable();
        }
        return instance;
    }

    public ConnectionState getConnectionState() {
        return connectionState;
    }

    public String getEndPointId() {
        return endPointId;
    }

    public void setConnectionStateAndId(ConnectionState connectionState, String endPointId) {
        this.connectionState = connectionState;
        this.endPointId = endPointId;
        setChanged();
        notifyObservers();
    }
}
