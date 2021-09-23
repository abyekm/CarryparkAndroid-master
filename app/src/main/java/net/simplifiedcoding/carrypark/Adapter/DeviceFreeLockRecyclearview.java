package net.simplifiedcoding.carrypark.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;
import net.rest.model.FreeDeviceModel;
import net.simplifiedcoding.carrypark.R;
import java.util.List;


/**
 * Created by gizmeon on 9/3/17.
 */

public class DeviceFreeLockRecyclearview extends RecyclerView.Adapter<DeviceFreeLockRecyclearview.ListViewHolder> {

    private Context context;
    private List<FreeDeviceModel> freeDeviceModelList;

    private CustomOnClickListener customOnClickListener;
    private LayoutInflater inflater = null;
    String lockOrUnlock;

    public class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView  tv_device_status;
        public Button btn_connect;
        public LinearLayout ll_outer;
        public TextView tv_deviceName;


        public ListViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            tv_deviceName = (TextView) itemView.findViewById(R.id.tv_deviceName);
            tv_device_status = (TextView) itemView.findViewById(R.id.tv_device_status);
            btn_connect =(Button) itemView.findViewById(R.id.btn_connect);
            ll_outer = (LinearLayout) itemView.findViewById(R.id.ll_outer);
        }

        @Override
        public void onClick(View v) {

        }

    }

    public DeviceFreeLockRecyclearview(Context context, Activity activity,  List<FreeDeviceModel> freeDeviceModelList,String lockOrUnlock, CustomOnClickListener customOnClickListener) {

        this.context = context;
        this.freeDeviceModelList = freeDeviceModelList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.customOnClickListener=customOnClickListener;
        this.lockOrUnlock =lockOrUnlock;


    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_devicelist_map, parent, false);
        ListViewHolder lrcv = new ListViewHolder(layoutView);
        return lrcv;
    }

    @Override
    public void onBindViewHolder(final ListViewHolder holder, int position) {

        holder.tv_device_status.setText(lockOrUnlock);
        if (position==0)
        {
            holder.tv_deviceName.setTypeface(null, Typeface.BOLD);
            holder.tv_device_status.setTypeface(null,Typeface.BOLD);
            holder.btn_connect.setTypeface(null,Typeface.BOLD);

        }
        else {
            holder.tv_deviceName.setTypeface(null, Typeface.NORMAL);
            holder.tv_device_status.setTypeface(null,Typeface.NORMAL);
            holder.btn_connect.setTypeface(null,Typeface.NORMAL);

        }
        holder.tv_deviceName.setText(freeDeviceModelList.get(position).getDevice_name());


        holder.btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               customOnClickListener .IDevicesConnect(freeDeviceModelList.get(position));


            }
        });
        holder.ll_outer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customOnClickListener .SwapPositionToTop(position);

            }
        });
        holder.tv_deviceName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customOnClickListener.SwapPositionToTop(position);
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



    public interface CustomOnClickListener {

        void IDevicesConnect(FreeDeviceModel freeDeviceModel);
        void SwapPositionToTop(int position);
    }


}
