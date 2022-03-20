package ro.randr.tictactoe.Utils;

import android.content.Context;
import android.content.Intent;
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

import java.lang.ref.WeakReference;
import java.nio.charset.Charset;

import ro.randr.tictactoe.Activities.GameAndChatActivity;
import ro.randr.tictactoe.Activities.MainActivity;
import ro.randr.tictactoe.Interfaces.TwoOptionsDialog;
import ro.randr.tictactoe.Models.ChatMessageModel;
import ro.randr.tictactoe.Models.DeviceModel;
import ro.randr.tictactoe.Models.MessageModel;

public class ConnectionUtils {
    private static String connectedEndPoint;
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
                    GameAndChatActivity.mAdapter.addToDataSet(messageModel.ChatMessageModel);
                }
                if (messageModel.ClickMessageModel != null) {
                    GameAndChatActivity.ManageClickReceived(messageModel.ClickMessageModel.X, messageModel.ClickMessageModel.Y);
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
                        MainActivity.mAdapter.addToDataSet(device);
                    }

                    @Override
                    public void onEndpointLost(@NonNull String endpointId) {
                        MainActivity.mAdapter.removeFromDataSet(endpointId);
                    }
                };
    }

    private static void InitializeConnectionLifecycleCallback(Context context) {
        connectionLifecycleCallback =
                new ConnectionLifecycleCallback() {
                    @Override
                    public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
                        Dialog dialog = new Dialog(context, "accept_connection", "token: " + connectionInfo.getAuthenticationDigits(), new TwoOptionsDialog() {
                            @Override
                            public void onPositive() {
                                Nearby.getConnectionsClient(context)
                                        .acceptConnection(endpointId,  new ReceiveBytesPayloadListener());
                                Intent intent = new Intent(context, GameAndChatActivity.class);
                                context.startActivity(intent);
                            }

                            @Override
                            public void onNegative() {
                                Nearby.getConnectionsClient(context).rejectConnection(endpointId);
                            }
                        });
                        dialog.show();
                    }

                    @Override
                    public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution result) {
                        switch (result.getStatus().getStatusCode()) {
                            case ConnectionsStatusCodes.STATUS_OK:
                                Toast.makeText(context, "Connected to : " + endpointId, Toast.LENGTH_SHORT).show();
                                connectedEndPoint = endpointId;
                                break;
                            case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                                Toast.makeText(context, "Refused to: " + endpointId, Toast.LENGTH_SHORT).show();
                                break;
                            case ConnectionsStatusCodes.STATUS_ERROR:
                                Toast.makeText(context, "Error to: " + endpointId, Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(context, "Unknown to: " + endpointId, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDisconnected(@NonNull String endpointId) {
                        Toast.makeText(context, "Disconnected from: " + endpointId, Toast.LENGTH_SHORT).show();
                    }
                };
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
                        (Void unused) -> Toast.makeText(context, "Discovery started!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(
                        (Exception e) -> Toast.makeText(context, "Discovery NOT started!", Toast.LENGTH_SHORT).show());
    }

    public static void RequestConnection(Context context, DeviceModel toDevice) {
        Nearby.getConnectionsClient(context)
                .requestConnection(MainActivity.username, toDevice.EndpointId, connectionLifecycleCallback)
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
        Nearby.getConnectionsClient(context).sendPayload(connectedEndPoint, bytesPayload);
    }

}
