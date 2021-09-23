package net.simplifiedcoding.carrypark.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import net.rest.model.FreeDeviceModel;
import net.simplifiedcoding.carrypark.R;

import java.util.List;

public class DeviceListAdapter  extends RecyclerView.Adapter<DeviceListAdapter.SectionViewHolder> {
    private IDeviceListAdapter iDeviceListAdapter;
    String lockOrUnlock;
    class SectionViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_deviceName, tv_device_status;
        private Button btn_connect;

        public SectionViewHolder(View itemView) {
            super(itemView);
            tv_deviceName = (TextView) itemView.findViewById(R.id.tv_deviceName);
            tv_device_status = (TextView) itemView.findViewById(R.id.tv_device_status);
            btn_connect =(Button) itemView.findViewById(R.id.btn_connect);
        }
    }

    private Context context;


    public List<FreeDeviceModel> freeDeviceModelList;

    public DeviceListAdapter(Context context, List<FreeDeviceModel> freeDeviceModelsList, Context applicationContext,String lockOrUnlock,IDeviceListAdapter iDevicesInUseCall) {
        this.context = context;
        this.freeDeviceModelList = freeDeviceModelList;
        this.iDeviceListAdapter =iDevicesInUseCall;
        this.lockOrUnlock =lockOrUnlock;
    }

    @Override
    public DeviceListAdapter.SectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_devicelist_map, parent, false);
        return new DeviceListAdapter.SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DeviceListAdapter.SectionViewHolder holder, final int position) {
        final FreeDeviceModel freeDeviceModel = freeDeviceModelList.get(position);
        holder.tv_device_status.setText(lockOrUnlock);
        holder.tv_deviceName.setText(freeDeviceModelList.get(position).getDevice_name());


        holder.btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                iDeviceListAdapter.IDevicesConnect(freeDeviceModelList.get(position));

            }
        });


    }

    @Override
    public int getItemCount() {

        if (freeDeviceModelList!=null)
        {
            if (freeDeviceModelList.size()>0)
            {
                return freeDeviceModelList.size();
            }
            else
            {
                return 0;
            }
        }
      return 0;
    }
    public interface IDeviceListAdapter {
        void IDevicesConnect(FreeDeviceModel freeDeviceModel);
    }

}
