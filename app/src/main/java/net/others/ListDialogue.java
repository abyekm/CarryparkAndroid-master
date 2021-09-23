package net.others;

import android.app.Activity;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import net.CarryParkApplication;
import net.simplifiedcoding.carrypark.R;

import java.util.ArrayList;

public class ListDialogue {

    public static void showUniActionDialog(Activity parentActivity, String alertMessage,
                                           String positiveText, ArrayList<String> listItems, final DialogManager.IUniActionDialogOnClickListener iUniActionDialogOnClickListener) {

        View dialogView = (LinearLayout) parentActivity.getLayoutInflater().inflate(R.layout.custom_alert_list_dialog, null, false);


        //  private ArrayList<BluetoothDevice> listItems = new ArrayList<>();


        Button ok = (Button) dialogView.findViewById(R.id.bt_ok);
        ListView listView = (ListView) dialogView.findViewById(R.id.bluetooth_list);


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(CarryParkApplication.getCurrentContext(), android.R.layout.simple_list_item_1, listItems);
        listView.setAdapter(arrayAdapter);


        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity).setView(dialogView).setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                iUniActionDialogOnClickListener.onPositiveClick();
                dialog.dismiss();
            }
        });
       /* ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                iUniActionDialogOnClickListener.onPositiveClick();
                dialog.dismiss();




            }
        });
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(parentActivity.getResources().getColor(R.color.colorAccent));
*/
    }


    public interface IUniActionDialogOnClickListener {

        void onPositiveClick();

    }

    public interface IMultiActionDialogOnClickListener {

        void onPositiveClick();

        void onNegativeClick();

    }
}
