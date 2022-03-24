package ro.randr.tictactoe.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Optional;

import ro.randr.tictactoe.Models.DeviceModel;
import ro.randr.tictactoe.Observables.MainActivityStateObservable;
import ro.randr.tictactoe.R;


public class RecycleViewDevicesAdapter extends RecyclerView.Adapter<RecycleViewDevicesAdapter.RecycleViewHolder> {

    private final List<DeviceModel> devices;

    public RecycleViewDevicesAdapter(List<DeviceModel> devices) {
        this.devices = devices;
    }

    public void modifyList(DeviceModel device) {
        Optional<DeviceModel> deviceToFInd = devices.stream().filter(p -> p.EndpointId.equals(device.EndpointId)).findFirst();
        if (deviceToFInd.isPresent()) {
            devices.remove(device);
        } else {
            devices.add(device);
        }
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public RecycleViewDevicesAdapter.RecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.devices_cardview, parent, false);

        return new RecycleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewDevicesAdapter.RecycleViewHolder holder,
                                 int position) {
        AppCompatTextView tv_name = holder.tv_name;
        AppCompatImageView iv_connect = holder.iv_connect;
        tv_name.setText(devices.get(position).EndpointId + " " + devices.get(position).Name);
        iv_connect.setOnClickListener(view -> MainActivityStateObservable.getInstance().requestConnection(holder.getAdapterPosition()));

    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public static class RecycleViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView tv_name;
        AppCompatImageView iv_connect;

        public RecycleViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.iv_connect = itemView.findViewById(R.id.iv_connect);
        }
    }
}
