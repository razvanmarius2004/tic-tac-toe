package ro.randr.tictactoe.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ro.randr.tictactoe.Models.DeviceModel;
import ro.randr.tictactoe.R;
import ro.randr.tictactoe.Utils.ConnectionUtils;


public class RecycleViewDevicesAdapter extends RecyclerView.Adapter<RecycleViewDevicesAdapter.RecycleViewHolder> {

    private List<DeviceModel> devices;
    private Context mContext;

    public RecycleViewDevicesAdapter(Context context, List<DeviceModel> devices) {
        this.devices = devices;
        this.mContext = context;
    }

    public void addToDataSet(DeviceModel device) {
        devices.add(device);
        this.notifyDataSetChanged();
    }

    public void removeFromDataSet(String endpointId) {
        DeviceModel device = devices.stream().filter(p -> p.EndpointId.equals(endpointId)).findFirst().get();
        devices.remove(device);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecycleViewDevicesAdapter.RecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.devices_cardview, parent, false);

        return new RecycleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewDevicesAdapter.RecycleViewHolder holder, int position) {
        AppCompatTextView tv_name = holder.tv_name;
        AppCompatImageView iv_connect = holder.iv_connect;
        tv_name.setText(devices.get(position).EndpointId + " " + devices.get(position).Name);
        iv_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectionUtils.RequestConnection(mContext, devices.get(position));
            }
        });

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
