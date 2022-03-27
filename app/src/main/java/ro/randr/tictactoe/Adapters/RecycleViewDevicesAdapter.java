package ro.randr.tictactoe.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
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

    public void removeAll() {
        devices.removeAll(devices);
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
        holder.tv_name.setText(devices.get(position).EndpointId + " " + devices.get(position).Name);
        holder.pb_connect.setVisibility(View.GONE);
        holder.btn_connect.setVisibility(View.VISIBLE);
        holder.iv_connect.setVisibility(View.VISIBLE);
        holder.btn_connect.setOnClickListener(view -> connect(holder));

    }

    private void connect(RecycleViewDevicesAdapter.RecycleViewHolder holder) {
        MainActivityStateObservable.getInstance().requestConnection(holder.getAdapterPosition());
        holder.pb_connect.setVisibility(View.VISIBLE);
        holder.btn_connect.setVisibility(View.GONE);
        holder.iv_connect.setVisibility(View.GONE);


    }

    public void notConnected(String endpointId) {
        for (int i = 0; i < devices.size(); i++) {
            if (endpointId.equals(devices.get(i).EndpointId)) {
                notifyItemChanged(i);
                break;
            }
        }
    }


    @Override
    public int getItemCount() {
        return devices.size();
    }

    public static class RecycleViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView tv_name;
        AppCompatImageView iv_connect;
        AppCompatButton btn_connect;
        ProgressBar pb_connect;

        public RecycleViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.iv_connect = itemView.findViewById(R.id.iv_connect);
            this.pb_connect = itemView.findViewById(R.id.pb_connect);
            this.btn_connect = itemView.findViewById(R.id.btn_connect);
        }
    }
}
