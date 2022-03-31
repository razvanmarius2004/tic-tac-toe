package ro.randr.tictactoe.Models;

public class AdvertisingAndDiscoveryStatusModel {
    private boolean isDiscoveryOk;
    private boolean isAdvertisingOk;
    private boolean isDiscoveryProcessFinished;
    private boolean isAdvertisingProcessFinished;

    public static AdvertisingAndDiscoveryStatusModel instance;

    private AdvertisingAndDiscoveryStatusModel() {
        setInitValues();
    }

    public static AdvertisingAndDiscoveryStatusModel getInstance() {
        if (instance == null) {
            instance = new AdvertisingAndDiscoveryStatusModel();
        }
        return instance;
    }

    public boolean isDiscoveryOk() {
        return isDiscoveryOk;
    }

    public boolean isAdvertisingOk() {
        return isAdvertisingOk;
    }

    public boolean isDiscoveryProcessFinished() {
        return isDiscoveryProcessFinished;
    }

    public boolean isAdvertisingProcessFinished() {
        return isAdvertisingProcessFinished;
    }

    public void setDiscoveryOk(boolean discoveryOk) {
        isDiscoveryOk = discoveryOk;
    }

    public void setAdvertisingOk(boolean advertisingOk) {
        isAdvertisingOk = advertisingOk;
    }

    public void setDiscoveryProcessFinished(boolean discoveryProcessFinished) {
        isDiscoveryProcessFinished = discoveryProcessFinished;
    }

    public void setAdvertisingProcessFinished(boolean advertisingProcessFinished) {
        isAdvertisingProcessFinished = advertisingProcessFinished;
    }

    public static void setInstance(AdvertisingAndDiscoveryStatusModel instance) {
        AdvertisingAndDiscoveryStatusModel.instance = instance;
    }

    public void setInitValues() {
        isAdvertisingOk = false;
        isDiscoveryOk = false;
        isAdvertisingProcessFinished = false;
        isDiscoveryProcessFinished = false;
    }


}
