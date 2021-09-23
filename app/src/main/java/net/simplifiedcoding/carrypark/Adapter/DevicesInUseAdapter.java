package net.simplifiedcoding.carrypark.Adapter;

import android.content.Context;
import android.widget.Button;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.CarryParkApplication;
import net.others.CommonMethod;
import net.rest.model.UsedLocker;
import net.simplifiedcoding.carrypark.R;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DevicesInUseAdapter extends RecyclerView.Adapter<DevicesInUseAdapter.SectionViewHolder> {
    private IDevicesInUseCall  iDevicesInUseCall;

    //sjn
    //ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();

    //ArrayList<ItemList> itemLists = new ArrayList<>();

    class SectionViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTime, tvDeviceContent_rvitem,tvStatus_rvitem;
        private Button btnScan,btnGetDirection;
       // private IntentIntegrator qrScan;

        public SectionViewHolder(View itemView) {
            super(itemView);
            tvDeviceContent_rvitem = (TextView) itemView.findViewById(R.id.tvDeviceContent_rvitem);
            tvTime =(TextView) itemView.findViewById(R.id.tvTime);

        }
    }

    private Context context;

    //private List<Datum> sectionModelArrayList;
    //Resultinterface resultinterface;
    public List<UsedLocker> usedLockerList;

    public DevicesInUseAdapter(Context context, List<UsedLocker> usedLockerList, Context applicationContext,IDevicesInUseCall  iDevicesInUseCall) {
        this.context = context;
        this.usedLockerList = usedLockerList;
        this.iDevicesInUseCall =iDevicesInUseCall;
    }

    @Override
    public SectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_used_locker_list, parent, false);
        return new SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SectionViewHolder holder, final int position) {
        final UsedLocker usedLockerModel = usedLockerList.get(position);
        holder.tvDeviceContent_rvitem.setText(""+(position+1)+". "+usedLockerModel.getDeviceName());
        String time="";
        SimpleDateFormat sdf = null;
        SimpleDateFormat day = null;
        SimpleDateFormat month = null;
        SimpleDateFormat year = null;
        SimpleDateFormat amOrpm = null;
        SimpleDateFormat hh = null;
        SimpleDateFormat mm = null;
        try {
            sdf = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
            amOrpm = new SimpleDateFormat("a");
            hh = new SimpleDateFormat("hh");
            mm = new SimpleDateFormat("mm");
        } catch (Exception e) {
            e.printStackTrace();
            sdf = new SimpleDateFormat("HH:mm:ss.SSS");
            day = new SimpleDateFormat("d");
            month = new SimpleDateFormat("M");
            year = new SimpleDateFormat("y");
            amOrpm = new SimpleDateFormat("a");
            hh = new SimpleDateFormat("hh");
            mm = new SimpleDateFormat("mm");
        }
        Date date_start =null,date_end=null;

        try {
            DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");


            if (usedLockerModel.getStart_time()!=null)
            {
                date_start = timeFormat.parse(usedLockerModel.getStart_time());
            }
            if (usedLockerModel.getEnd_time()!=null)
            {
                date_end = timeFormat.parse(usedLockerModel.getEnd_time());
            }





        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        if (CarryParkApplication.isIsJapaneaseLang())
        {


            try {
                holder.tvTime.setText(CommonMethod.timeFormatInJapaneaseAvoidZero(amOrpm.format(date_start),hh.format(date_start),mm.format(date_start)) +" ~ "+CommonMethod.timeFormatInJapanease(amOrpm.format(date_end),hh.format(date_end),mm.format(date_end)));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else
        {

            try {
                holder.tvTime.setText(CommonMethod.timeFormatInEnglish(amOrpm.format(date_start),hh.format(date_start),mm.format(date_start)) +" - "+CommonMethod.timeFormatInEnglish(amOrpm.format(date_end),hh.format(date_end),mm.format(date_end)));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    @Override
    public int getItemCount() {
        return usedLockerList.size();
    }
    public interface IDevicesInUseCall {
        void IDevicesInUseScanCall(UsedLocker usedLocker);
        void IDeviceInUseGetDirectionCall(UsedLocker usedLocker);
    }

}
