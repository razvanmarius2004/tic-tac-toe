package ro.randr.tictactoe.Observables;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import ro.randr.tictactoe.Models.AdvertisingAndDiscoveryStatusModel;
import ro.randr.tictactoe.Models.ConnectionPayloadModel;
import ro.randr.tictactoe.Models.DeviceModel;

public class MainActivityStateObservable extends Observable {
    private Boolean isConnectionRequested;
    private final List<DeviceModel> devices;
    private static MainActivityStateObservable instance;

    private MainActivityStateObservable() {
        isConnectionRequested = false;
        devices = new ArrayList<>();
    }

    public static MainActivityStateObservable getInstance() {
        if (instance == null) {
            instance = new MainActivityStateObservable();
        }
        return instance;
    }

    // region setters and getters
    public boolean isConnectionRequested() {
        return isConnectionRequested;
    }

    public void setConnectionRequested(boolean connectionRequested) {
        isConnectionRequested = connectionRequested;
    }

    // endregion

    public void notifyAdvertisingAndDiscoveryStatus(AdvertisingAndDiscoveryStatusModel advertisingAndDiscoveryStatusModel){
      setChanged();
      notifyObservers(advertisingAndDiscoveryStatusModel);
    }

    public void sendConnectionPayload(ConnectionPayloadModel connectionPayload) {
        setChanged();
        notifyObservers(connectionPayload);
    }

    public void addDeviceToList(DeviceModel device) {
        devices.add(device);
        setChanged();
        notifyObservers(device);
    }

    public void removeDeviceFromList(String endpointId) {
        DeviceModel device = devices.stream().filter(p -> p.EndpointId.equals(endpointId)).findFirst().get();
        devices.remove(device);
        setChanged();
        notifyObservers(device);
    }

    public void requestConnection(int position) {
        isConnectionRequested = true;
        setChanged();
        notifyObservers(devices.get(position));
    }

    public void removeAllDevices() {
        devices.removeAll(devices);
    }

    public void reInit() {
        isConnectionRequested = false;
        removeAllDevices();
    }

}
