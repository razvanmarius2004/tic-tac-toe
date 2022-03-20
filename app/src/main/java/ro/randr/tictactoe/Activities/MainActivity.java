package ro.randr.tictactoe.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import ro.randr.tictactoe.Adapters.RecycleViewDevicesAdapter;
import ro.randr.tictactoe.Interfaces.TwoOptionsDialog;
import ro.randr.tictactoe.Models.DeviceModel;
import ro.randr.tictactoe.R;
import ro.randr.tictactoe.Utils.ConnectionUtils;
import ro.randr.tictactoe.Utils.Dialog;

public class MainActivity extends AppCompatActivity {
    private AppCompatButton btn_start_discovery;
    private AppCompatButton btn_start_advertising;
    private AppCompatImageView iv_edit_username;
    private AppCompatImageView iv_cancel;
    private AppCompatImageView iv_done;
    private AppCompatEditText et_username;
    private RecyclerView rv_devices;
    public static String username;
    public static RecycleViewDevicesAdapter mAdapter;
    public static List<DeviceModel> devices = new ArrayList<>();
    private static final int REQUEST_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getUsername();
        getViews();
        setViews();
    }

    private void getUsername() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        username = sharedPref.getString(getString(R.string.username), getString(R.string.default_username));
    }

    private void getViews() {
        btn_start_discovery = findViewById(R.id.btn_start_discovery);
        btn_start_advertising = findViewById(R.id.btn_start_advertising);
        iv_edit_username = findViewById(R.id.iv_edit_username);
        iv_cancel = findViewById(R.id.iv_cancel);
        iv_done = findViewById(R.id.iv_done);
        et_username = findViewById(R.id.et_username);
        rv_devices = findViewById(R.id.rv_devices);
    }



    private void setViews() {
        btn_start_discovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDiscovery();
            }
        });
        btn_start_advertising.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAdvertising();
            }
        });

        iv_edit_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyUsername();
            }
        });

        iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelModifyUsername();
            }
        });

        iv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUsername();
            }
        });

        et_username.setText(username);
        et_username.setEnabled(false);

        iv_done.setVisibility(View.INVISIBLE);
        iv_cancel.setVisibility(View.INVISIBLE);

        rv_devices.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        rv_devices.setLayoutManager(mLayoutManager);
        rv_devices.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new RecycleViewDevicesAdapter(new WeakReference<>(this),devices);
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
        username = et_username.getText().toString();

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

    private void startAdvertising() {
        if (!arePermsOk()) {
            showDisclaimerDialog();
        } else {
            ConnectionUtils.StartAdvertising(this);
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
}