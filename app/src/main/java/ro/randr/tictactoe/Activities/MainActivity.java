package ro.randr.tictactoe.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import ro.randr.tictactoe.Adapters.RecycleViewDevicesAdapter;
import ro.randr.tictactoe.Interfaces.TwoOptionsDialog;
import ro.randr.tictactoe.Models.ConnectionPayloadModel;
import ro.randr.tictactoe.Models.DeviceModel;
import ro.randr.tictactoe.Observables.ConnectionStateObservable;
import ro.randr.tictactoe.Observables.MainActivityStateObservable;
import ro.randr.tictactoe.R;
import ro.randr.tictactoe.Utils.ConnectionUtils;
import ro.randr.tictactoe.Utils.Dialog;

public class MainActivity extends AppCompatActivity implements Observer {

    private AppCompatButton btn_ready_not_ready;
    private AppCompatImageView iv_edit_username;
    private AppCompatImageView iv_cancel;
    private AppCompatImageView iv_done;
    private AppCompatEditText et_username;
    private RecyclerView rv_devices;
    public static String username;
    private RecycleViewDevicesAdapter mAdapter;
    public static List<DeviceModel> devices = new ArrayList<>();
    private static final int REQUEST_PERMISSIONS = 1;

    private boolean areYouReady = false;

    private Observable mainActivityState;
    private Observable connectionStateObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addObservers();
        getUsername();
        getViews();
        setViews();
    }

    private void addObservers() {
        mainActivityState = MainActivityStateObservable.getInstance();
        mainActivityState.addObserver(this);
        connectionStateObservable = ConnectionStateObservable.getInstance();
        connectionStateObservable.addObserver(this);
    }

    private void getUsername() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        username = sharedPref.getString(getString(R.string.username), getString(R.string.default_username));
    }

    private void getViews() {

        btn_ready_not_ready = findViewById(R.id.btn_ready_not_ready);
        iv_edit_username = findViewById(R.id.iv_edit_username);
        iv_cancel = findViewById(R.id.iv_cancel);
        iv_done = findViewById(R.id.iv_done);
        et_username = findViewById(R.id.et_username);
        rv_devices = findViewById(R.id.rv_devices);
    }


    private void setViews() {
        btn_ready_not_ready.setOnClickListener(view -> setReadyNotReady());

        iv_edit_username.setOnClickListener(view -> modifyUsername());

        iv_cancel.setOnClickListener(view -> cancelModifyUsername());

        iv_done.setOnClickListener(view -> setUsername());

        et_username.setText(username);
        et_username.setEnabled(false);

        iv_done.setVisibility(View.INVISIBLE);
        iv_cancel.setVisibility(View.INVISIBLE);

        rv_devices.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        rv_devices.setLayoutManager(mLayoutManager);
        rv_devices.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new RecycleViewDevicesAdapter(devices);
        rv_devices.setAdapter(mAdapter);

    }

    private void modifyUsername() {
        et_username.setEnabled(true);
        iv_done.setVisibility(View.VISIBLE);
        iv_cancel.setVisibility(View.VISIBLE);
    }

    private void cancelModifyUsername() {
        et_username.setText(username);
        et_username.setEnabled(false);
        iv_done.setVisibility(View.INVISIBLE);
        iv_cancel.setVisibility(View.INVISIBLE);
    }

    private void setUsername() {
        if (et_username.getText() != null) {
            username = et_username.getText().toString();
        }

        et_username.setEnabled(false);
        iv_done.setVisibility(View.INVISIBLE);
        iv_cancel.setVisibility(View.INVISIBLE);
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.username), username);
        editor.apply();
    }

    private void startDiscovery() {
        if (!arePermsOk()) {
            showDisclaimerDialog();
        } else {
            ConnectionUtils.StartDiscovery(this);
        }

    }

    private void setReadyNotReady() {
        if (!arePermsOk()) {
            showDisclaimerDialog();
        } else {
            if (!areYouReady) {
                ConnectionUtils.StartAdvertising(this);
                ConnectionUtils.StartDiscovery(this);
                btn_ready_not_ready.setText(R.string.btn_set_not_ready);
                iv_edit_username.setVisibility(View.INVISIBLE);
                areYouReady = true;
            } else {
                ConnectionUtils.StopAdvertising(this);
                ConnectionUtils.StopDiscovery(this);
                mAdapter.removeAll();
                MainActivityStateObservable.getInstance().removeAllDevices();
                btn_ready_not_ready.setText(R.string.btn_set_ready);
                iv_edit_username.setVisibility(View.VISIBLE);
                areYouReady = false;
            }
        }
    }

    private boolean arePermsOk() {
        if ((ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                        this, Manifest.permission.BLUETOOTH_ADVERTISE) ==
                        PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                        this, Manifest.permission.BLUETOOTH_CONNECT) ==
                        PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                        this, Manifest.permission.BLUETOOTH_SCAN) ==
                        PackageManager.PERMISSION_GRANTED)
                ||
                (ContextCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(
                                this, Manifest.permission.BLUETOOTH_ADMIN) ==
                                PackageManager.PERMISSION_GRANTED)) {
            return true;
        }
        return false;
    }

    private void showDisclaimerDialog() {
        Dialog dialog = new Dialog(MainActivity.this, "disclaimer", "Disclaimer", new TwoOptionsDialog() {
            @Override
            public void onPositive() {
                requestPerm();
            }

            @Override
            public void onNegative() {
            }
        });
        dialog.show();
    }

    private void requestPerm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_ADVERTISE, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN},
                    REQUEST_PERMISSIONS);
        } else {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS);
        }
    }

    @Override
    public void update(Observable observable, Object o) {

        if (o instanceof DeviceModel) {
            DeviceModel device = (DeviceModel) o;
            if (MainActivityStateObservable.getInstance().isConnectionRequested() && et_username.getText() != null) {
                ConnectionUtils.RequestConnection(this, device, et_username.getText().toString());
            } else {
                mAdapter.modifyList(device);
            }
        }

        if (o instanceof ConnectionPayloadModel) {
            ConnectionPayloadModel connectionPayload = (ConnectionPayloadModel) o;
            Dialog dialog = new Dialog(MainActivity.this, "accept_connection", "token: " + connectionPayload.getAuthenticationToken(), new TwoOptionsDialog() {
                @Override
                public void onPositive() {
                    ConnectionUtils.acceptConn(getApplicationContext(), connectionPayload.getEndpointId());
                    ConnectionUtils.StopDiscovery(getApplicationContext());
                    ConnectionUtils.StopAdvertising(getApplicationContext());
                    Intent intent = new Intent(MainActivity.this, GameAndChatActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onNegative() {
                    ConnectionUtils.rejectConn(getApplicationContext(), connectionPayload.getEndpointId());
                    MainActivityStateObservable.getInstance().setConnectionInitiated(false);
                    MainActivityStateObservable.getInstance().setConnectionRequested(false);
                    mAdapter.notConnected(connectionPayload.getEndpointId());
                }
            });
            dialog.show();
        }


        if (observable instanceof ConnectionStateObservable) {
            ConnectionStateObservable connectionState = (ConnectionStateObservable) observable;
            switch (connectionState.getConnectionState()) {
                case STATUS_OK:
                    Toast.makeText(this, "Connected to: " + connectionState.getEndPointId(), Toast.LENGTH_SHORT).show();
                    break;
                case STATUS_CONNECTION_REJECTED:
                    Toast.makeText(this, "Refused by: " + connectionState.getEndPointId(), Toast.LENGTH_SHORT).show();
                    break;
                case STATUS_ERROR:
                    Toast.makeText(this, "Error connecting to: " + connectionState.getEndPointId(), Toast.LENGTH_SHORT).show();
                    break;
                case STATUS_UNKNOWN:
                    Toast.makeText(this, "Unknown error connecting to: " + connectionState.getEndPointId(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainActivityState.deleteObserver(this);
        connectionStateObservable.deleteObserver(this);
    }
}