package ro.randr.tictactoe.Models;

import androidx.annotation.Nullable;

public class DeviceModel {
    public String Name;
    public String EndpointId;
    public String ServiceId;

    public DeviceModel(String name, String endpointId, String serviceId) {
        Name = name;
        EndpointId = endpointId;
        ServiceId = serviceId;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return (this.Name.equals(((DeviceModel) obj).Name) && this.EndpointId.equals(((DeviceModel) obj).EndpointId) && this.ServiceId.equals(((DeviceModel) obj).ServiceId));
    }
}
