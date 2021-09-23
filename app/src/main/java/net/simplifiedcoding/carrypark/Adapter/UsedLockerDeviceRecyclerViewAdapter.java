package net.simplifiedcoding.carrypark.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import net.rest.model.UsedLocker;
import net.simplifiedcoding.carrypark.R;

import java.util.List;

public class UsedLockerDeviceRecyclerViewAdapter extends RecyclerView.Adapter<UsedLockerDeviceRecyclerViewAdapter.ListViewHolder> {

    private Context context;
    List<UsedLocker> usedLockerList;

    private CustomOnClickListener customOnClickListener;
    private LayoutInflater inflater = null;
    LinearLayout ll_outer;
    String lockOrUnlock;

    public class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tv_deviceName, tv_device_status;
        public Button btn_connect;
        LinearLayout ll_outer;


        public ListViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            tv_deviceName = (TextView) itemView.findViewById(R.id.tv_deviceName);
            tv_device_status = (TextView) itemView.findViewById(R.id.tv_device_status);
            btn_connect =(Button) itemView.findViewById(R.id.btn_connect);
            ll_outer =(LinearLayout)itemView.findViewById(R.id.ll_outer);

        }

        @Override
        public void onClick(View v) {

        }

    }

    public UsedLockerDeviceRecyclerViewAdapter(Context context, Activity activity,  List<UsedLocker> usedLockerList, String lockOrUnlock, CustomOnClickListener customOnClickListener) {

        this.context = context;
        this.usedLockerList = usedLockerList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.customOnClickListener=customOnClickListener;
        this.lockOrUnlock =lockOrUnlock;


    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_devicelist_map_used, parent, false);
        ListViewHolder lrcv = new ListViewHolder(layoutView);
        return lrcv;
    }

    @Override
    public void onBindViewHolder(final ListViewHolder holder, int position) {

        holder.tv_device_status.setText(lockOrUnlock);
        holder.tv_deviceName.setText(usedLockerList.get(position).getDeviceName());
        if (position==0)
        {
            holder.tv_deviceName.setTypeface(null, Typeface.BOLD);
            holder.tv_device_status.setTypeface(null, Typeface.BOLD);
            holder.btn_connect.setTypeface(null,Typeface.BOLD);


        }
        else {
            holder.tv_deviceName.setTypeface(null, Typeface.NORMAL);
            holder.tv_device_status.setTypeface(null, Typeface.NORMAL);
            holder.btn_connect.setTypeface(null,Typeface.NORMAL);


        }


        holder.btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                customOnClickListener .IDevicesConnect(usedLockerList.get(position));

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
                customOnClickListener .SwapPositionToTop(position);
            }
        });


    }


    @Override
    public int getItemCount() {
        if (usedLockerList!=null)
        {
            if (usedLockerList.size()>0)
            {
                return usedLockerList.size();
            }
            else
            {
                return 0;
            }
        }
        return 0;
    }



    public interface CustomOnClickListener {

        void IDevicesConnect(UsedLocker usedLocker);
        void SwapPositionToTop(int position);
    }


}
