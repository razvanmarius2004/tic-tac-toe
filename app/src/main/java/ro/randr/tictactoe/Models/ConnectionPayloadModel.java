package ro.randr.tictactoe.Models;

public class ConnectionPayloadModel {
    private final String authenticationToken;
    private final String endpointId;

    public ConnectionPayloadModel(String authenticationToken, String endpointId) {
        this.authenticationToken = authenticationToken;
        this.endpointId = endpointId;
    }

    public String getAuthenticationToken() {
        return authenticationToken;
    }

    public String getEndpointId() {
        return endpointId;
    }
}
