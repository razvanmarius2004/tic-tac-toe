package ro.randr.tictactoe.Utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.gson.Gson;

import java.nio.charset.Charset;

import ro.randr.tictactoe.Activities.MainActivity;
import ro.randr.tictactoe.Enums.ConnectionState;
import ro.randr.tictactoe.Enums.TicTac;
import ro.randr.tictactoe.Models.ConnectionPayloadModel;
import ro.randr.tictactoe.Models.DeviceModel;
import ro.randr.tictactoe.Models.MessageModel;
import ro.randr.tictactoe.Observables.ChatMessageObservable;
import ro.randr.tictactoe.Observables.ConnectionStateObservable;
import ro.randr.tictactoe.Observables.GameStateObservable;
import ro.randr.tictactoe.Observables.MainActivityStateObservable;

public class ConnectionUtils {

    private static ConnectionLifecycleCallback connectionLifecycleCallback;
    private static EndpointDiscoveryCallback endpointDiscoveryCallback;

    static class ReceiveBytesPayloadListener extends PayloadCallback {

        @Override
        public void onPayloadReceived(@NonNull String endpointId, Payload payload) {
            // This always gets the full data of the payload. Is null if it's not a BYTES payload.
            if (payload.getType() == Payload.Type.BYTES) {
                byte[] receivedBytes = payload.asBytes();
                String message = new String(receivedBytes, Charset.defaultCharset());
                Gson g = new Gson();
                MessageModel messageModel = g.fromJson(message, MessageModel.class);
                if (messageModel.ChatMessageModel != null) {
                    messageModel.ChatMessageModel.IsOwn = false;
                    ChatMessageObservable.getInstance().addChatToList(messageModel.ChatMessageModel);
                }

                GameStateObservable boardState = GameStateObservable.getInstance();
                if (messageModel.ClickMessageModel != null) {
                    boardState.setYourTurn(true);
                    if (boardState.getPlayerType() == TicTac.TIC) {
                        boardState.modifyCell(messageModel.ClickMessageModel.Y, messageModel.ClickMessageModel.X, TicTac.TAC);
                    } else {
                        boardState.modifyCell(messageModel.ClickMessageModel.Y, messageModel.ClickMessageModel.X, TicTac.TIC);
                    }

                    boardState.checkAndSetWinner();
                }

                if (messageModel.IsOpponentReady != null) {
                    boardState.setOpponentReady(true);
                }
            }
        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String endpointId, @NonNull PayloadTransferUpdate update) {
            // Bytes payloads are sent as a single chunk, so you'll receive a SUCCESS update immediately
            // after the call to onPayloadReceived().
        }
    }

    private static void InitializeEndpointDiscoveryCallback() {
        endpointDiscoveryCallback =
                new EndpointDiscoveryCallback() {
                    @Override
                    public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo info) {
                        DeviceModel device = new DeviceModel(info.getEndpointName(), endpointId, info.getServiceId());
                        MainActivityStateObservable.getInstance().addDeviceToList(device);
                    }

                    @Override
                    public void onEndpointLost(@NonNull String endpointId) {
                        MainActivityStateObservable.getInstance().removeDeviceFromList(endpointId);
                    }
                };
    }

    private static void InitializeConnectionLifecycleCallback(Context context) {
        connectionLifecycleCallback =
                new ConnectionLifecycleCallback() {
                    @Override
                    public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
                        MainActivityStateObservable.getInstance().setConnectionPayload(new ConnectionPayloadModel(connectionInfo.getAuthenticationDigits(), endpointId));
                    }

                    @Override
                    public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution result) {
                        ConnectionStateObservable connectionState = ConnectionStateObservable.getInstance();
                        switch (result.getStatus().getStatusCode()) {
                            case ConnectionsStatusCodes.STATUS_OK:
                                connectionState.setConnectionStateAndId(ConnectionState.STATUS_OK, endpointId);
                                GameStateObservable.getInstance().setConnectedEndPoint(endpointId);
                                GameStateObservable.getInstance().setOpponentReady(true);
                                break;
                            case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                                connectionState.setConnectionStateAndId(ConnectionState.STATUS_CONNECTION_REJECTED, endpointId);
                                break;
                            case ConnectionsStatusCodes.STATUS_ERROR:
                                connectionState.setConnectionStateAndId(ConnectionState.STATUS_ERROR, endpointId);
                                break;
                            default:
                                connectionState.setConnectionStateAndId(ConnectionState.STATUS_UNKNOWN, endpointId);
                        }
                    }

                    @Override
                    public void onDisconnected(@NonNull String endpointId) {
                        ConnectionStateObservable.getInstance().setConnectionStateAndId(ConnectionState.STATUS_DISCONNECTED, endpointId);
                    }
                };
    }

    public static void acceptConn(Context context, String endPointId) {
        Nearby.getConnectionsClient(context)
                .acceptConnection(endPointId, new ReceiveBytesPayloadListener());
        GameStateObservable.getInstance().setAreYouReady(true);
    }

    public static void rejectConn(Context context, String endPointId) {
        Nearby.getConnectionsClient(context).rejectConnection(endPointId);
    }

    public static void StartAdvertising(Context context) {
        if (connectionLifecycleCallback == null) {
            InitializeConnectionLifecycleCallback(context);
        }
        AdvertisingOptions advertisingOptions =
                new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build();
        Nearby.getConnectionsClient(context)
                .startAdvertising(
                        MainActivity.username, context.getPackageName(), connectionLifecycleCallback, advertisingOptions)
                .addOnSuccessListener(
                        (Void unused) -> Toast.makeText(context, "Advertising started!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(
                        (Exception e) -> Toast.makeText(context, "Advertising NOT started!", Toast.LENGTH_SHORT).show());
    }

    public static void StartDiscovery(Context context) {
        InitializeEndpointDiscoveryCallback();
        if (connectionLifecycleCallback == null) {
            InitializeConnectionLifecycleCallback(context);
        }
        DiscoveryOptions discoveryOptions =
                new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build();
        Nearby.getConnectionsClient(context)
                .startDiscovery(context.getPackageName(), endpointDiscoveryCallback, discoveryOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                        });
    }

    public static void RequestConnection(Context context, DeviceModel toDevice, String yorUsername) {
        GameStateObservable.getInstance().setPlayerType(TicTac.TIC);
        GameStateObservable.getInstance().setYourTurn(true);
        Nearby.getConnectionsClient(context)
                .requestConnection(yorUsername, toDevice.EndpointId, connectionLifecycleCallback)
                .addOnSuccessListener(
                        (Void unused) -> {
                            // We successfully requested a connection. Now both sides
                            // must accept before the connection is established.
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            // Nearby Connections failed to request the connection.
                        });
    }

    public static void SendMessage(Context context, MessageModel messageModel) {
        String sendInfo = new Gson().toJson(messageModel);
        byte[] bytes = sendInfo.getBytes(Charset.defaultCharset());
        Payload bytesPayload = Payload.fromBytes(bytes);
        Nearby.getConnectionsClient(context).sendPayload(GameStateObservable.getInstance().getConnectedEndPoint(), bytesPayload);
    }

}
