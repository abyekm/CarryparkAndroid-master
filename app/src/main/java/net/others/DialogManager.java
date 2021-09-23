package net.others;

/*
 * Created by Ȿ₳Ɲ @ GIZMEON ©  on 07-03-2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.print.PDFPrint;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.google.android.material.snackbar.Snackbar;
import com.hendrix.pdfmyxml.viewRenderer.AbstractViewRenderer;
import com.tejpratapsingh.pdfcreator.utils.FileManager;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;

import net.CarryParkApplication;
import net.simplifiedcoding.carrypark.R;
import net.ui.PdfViewerActivity;

import java.io.*;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class DialogManager {
    private static View mRootView;
    private static String dirpath;



    public static void showUniActionDialog(Activity parentActivity, String alertMessage,
                                           String positiveText, final IUniActionDialogOnClickListener iUniActionDialogOnClickListener) {

        View dialogView = (CardView) parentActivity.getLayoutInflater().inflate(R.layout.custom_alert_dialog, null, false);


        TextView tv_alert_message = (TextView) dialogView.findViewById(R.id.tv_alert_message);
        Button ok = (Button) dialogView.findViewById(R.id.bt_ok);


        tv_alert_message.setText(alertMessage);


        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity).setView(dialogView).setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                iUniActionDialogOnClickListener.onPositiveClick();
                dialog.dismiss();


            }
        });
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(parentActivity.getResources().getColor(R.color.colorAccent));

    }

    public static void showConfirmDialogue(Activity parentActivity, String alertMessage,
                                           String positiveText, String positiveMsg, final IMultiActionDialogOnClickListener iMultiActionDialogOnClickListener) {

        View dialogView = (CardView) parentActivity.getLayoutInflater().inflate(R.layout.custom_confir_dialomg, null, false);


        TextView tv_alert_message = (TextView) dialogView.findViewById(R.id.tv_alert_message);
        Button ok = (Button) dialogView.findViewById(R.id.bt_ok);
        Button cancel = (Button) dialogView.findViewById(R.id.btn_cancel);


        ok.setText(positiveMsg);
        tv_alert_message.setText(alertMessage);


        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity).setView(dialogView).setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                iMultiActionDialogOnClickListener.onPositiveClick();


            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                iMultiActionDialogOnClickListener.onNegativeClick();
            }
        });
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(parentActivity.getResources().getColor(R.color.colorAccent));

    }


    public static void showAlertSingleActionDialog(Activity parentActivity, String alertMessage,
                                                   String positiveText, final IUniActionDialogOnClickListener iUniActionDialogOnClickListener) {

        View dialogView = (CardView) parentActivity.getLayoutInflater().inflate(R.layout.custom_alert_single_dialog, null, false);


        TextView tv_alert_message = (TextView) dialogView.findViewById(R.id.tv_alert_message);
        Button ok = (Button) dialogView.findViewById(R.id.bt_ok);


        tv_alert_message.setText(alertMessage);


        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity).setView(dialogView).setCancelable(false);
        final AlertDialog dialog = builder.create();

        dialog.show();


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                iUniActionDialogOnClickListener.onPositiveClick();
                dialog.dismiss();


            }
        });
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(parentActivity.getResources().getColor(R.color.colorAccent));

    }

    public static void showAlertSingleActionDialogJP(Activity parentActivity, String alertMessage,
                                                     String positiveText, final IUniActionDialogOnClickListener iUniActionDialogOnClickListener) {

        View dialogView = (CardView) parentActivity.getLayoutInflater().inflate(R.layout.custom_alert_single_dialog_jp, null, false);


        TextView tv_alert_message = (TextView) dialogView.findViewById(R.id.tv_alert_message);
        Button ok = (Button) dialogView.findViewById(R.id.bt_ok);


        tv_alert_message.setText(alertMessage);


        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity).setView(dialogView).setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                iUniActionDialogOnClickListener.onPositiveClick();
                dialog.dismiss();


            }
        });
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(parentActivity.getResources().getColor(R.color.colorAccent));

    }

    public static void showMultiActionDialogue(Activity parentActivity, String alertMessage,
                                               final IMultiActionDialogOnClickListener iMultiActionDialogOnClickListener) {

        View dialogView = (CardView) parentActivity.getLayoutInflater().inflate(R.layout.custom_confir_dialo, null, false);


        TextView tv_alert_message = (TextView) dialogView.findViewById(R.id.tv_alert_message);
        Button ok = (Button) dialogView.findViewById(R.id.bt_ok);
        Button cancel = (Button) dialogView.findViewById(R.id.btn_cancel);


        tv_alert_message.setText(alertMessage);


        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity).setView(dialogView).setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                iMultiActionDialogOnClickListener.onPositiveClick();


            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                iMultiActionDialogOnClickListener.onNegativeClick();
            }
        });
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(parentActivity.getResources().getColor(R.color.colorAccent));

    }

    public static void showRescanDialogue(final IMultiActionDialogOnClickListener iMultiActionDialogOnClickListener) {

        View dialogView = (CardView) CarryParkApplication.getCurrentActivity().getLayoutInflater().inflate(R.layout.custom_rescan_dialogue, null, false);


        TextView tv_alert_message = (TextView) dialogView.findViewById(R.id.tv_alert_message);
        Button ok = (Button) dialogView.findViewById(R.id.bt_ok);
        Button cancel = (Button) dialogView.findViewById(R.id.btn_cancel);


        AlertDialog.Builder builder = new AlertDialog.Builder(CarryParkApplication.getCurrentActivity()).setView(dialogView).setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                iMultiActionDialogOnClickListener.onPositiveClick();


            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                iMultiActionDialogOnClickListener.onNegativeClick();
            }
        });
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(CarryParkApplication.getCurrentActivity().getResources().getColor(R.color.colorAccent));

    }

    public static void showLogoutDialogue(final IMultiActionDialogOnClickListener iMultiActionDialogOnClickListener) {

        View dialogView = (CardView) CarryParkApplication.getCurrentActivity().getLayoutInflater().inflate(R.layout.custom_logout_dialogue, null, false);


        TextView tv_alert_message = (TextView) dialogView.findViewById(R.id.tv_alert_message);
        Button ok = (Button) dialogView.findViewById(R.id.bt_ok);
        Button cancel = (Button) dialogView.findViewById(R.id.btn_cancel);


        AlertDialog.Builder builder = new AlertDialog.Builder(CarryParkApplication.getCurrentActivity()).setView(dialogView).setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                iMultiActionDialogOnClickListener.onPositiveClick();


            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                iMultiActionDialogOnClickListener.onNegativeClick();
            }
        });
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(CarryParkApplication.getCurrentActivity().getResources().getColor(R.color.colorAccent));

    }


    public static void showDialogueInvoice(final IMultiActionDialogOnClickListenerInVoice iMultiActionDialogOnClickListenerInVoice) {

        mRootView = (LinearLayout) CarryParkApplication.getCurrentActivity().getLayoutInflater().inflate(R.layout.dialogue_invoice, null, false);


        Button button_download = (Button) mRootView.findViewById(R.id.button_download);
        Button btn_close = (Button) mRootView.findViewById(R.id.btn_close);
        TextView tv_temp_1 =(TextView) mRootView.findViewById(R.id.tv_temp_1);
        TextView tv_carrypark_co =(TextView) mRootView.findViewById(R.id.tv_carrypark_co);
        TextView tv_telphone = (TextView) mRootView.findViewById(R.id.tv_telphone);
        TextView tv_receipt = (TextView) mRootView.findViewById(R.id.tv_receipt);
        TextView tv_amount =(TextView) mRootView.findViewById(R.id.tv_amount);
        TextView tv_date_of_use =(TextView) mRootView.findViewById(R.id.tv_date_of_use);
        TextView id_tex1 =(TextView) mRootView.findViewById(R.id.id_tex1);
        TextView id_tex2 =(TextView) mRootView.findViewById(R.id.id_tex2);
        tv_amount.setText("￥"+ CarryParkApplication.getInvoiceAmount()+"-");
        SimpleDateFormat sdf = null;
        SimpleDateFormat day = null;
        SimpleDateFormat month = null;
        SimpleDateFormat year = null;
        SimpleDateFormat amOrpm = null;
        SimpleDateFormat hh = null;
        SimpleDateFormat mm = null;
        try {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            day = new SimpleDateFormat("d");
            month = new SimpleDateFormat("M");
            year = new SimpleDateFormat("y");
            amOrpm = new SimpleDateFormat("a");
            hh = new SimpleDateFormat("hh");
            mm = new SimpleDateFormat("mm");
        } catch (Exception e) {
            e.printStackTrace();
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
            day = new SimpleDateFormat("d");
            month = new SimpleDateFormat("M");
            year = new SimpleDateFormat("y");
            amOrpm = new SimpleDateFormat("a");
            hh = new SimpleDateFormat("hh");
            mm = new SimpleDateFormat("mm");
        }
        try {
            Date date_end;
            date_end = sdf.parse(CarryParkApplication.getInvoiceDate());
            SimpleDateFormat outputFormat = new SimpleDateFormat(CarryParkApplication.getCurrentContext().getResources().getString(R.string.paymentDateFormat), Locale.ENGLISH);
            outputFormat.setTimeZone(TimeZone.getDefault());


            if (CarryParkApplication.isIsJapaneaseLang())
            {

                String  dateToStr= CommonMethod.dateFormatInJapanease(month.format(date_end),day.format(date_end),amOrpm.format(date_end),
                        year.format(date_end),hh.format(date_end),mm.format(date_end));
                tv_date_of_use.setText(CarryParkApplication.getCurrentContext().getResources().getString(R.string.date_of_use)+ " "+dateToStr);

            }
            else
            {
                tv_date_of_use.setText(CarryParkApplication.getCurrentContext().getResources().getString(R.string.date_of_use)+ " "+outputFormat.format(date_end));


            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(CarryParkApplication.getCurrentActivity()).setView(mRootView).setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();


        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                dialog.dismiss();

                iMultiActionDialogOnClickListenerInVoice.onNegativeClick();
            }
        });
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(CarryParkApplication.getCurrentActivity().getResources().getColor(R.color.colorAccent));



        AbstractViewRenderer page = new AbstractViewRenderer(CarryParkApplication.getCurrentActivity(), R.layout.dialogue_invoice) {
            private String _text;


            public void setText(String text) {
                _text = text;
            }

            @Override
            protected void initView(View view) {


            }
        };
// you can reuse the bitmap if you want
        page.setReuseBitmap(true);
        button_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String first_text =tv_temp_1.getText().toString();
                    String second_text =tv_carrypark_co.getText().toString();
                String third_text =tv_telphone.getText().toString();
                String fourth_text =tv_receipt.getText().toString();

                String fifth_text =tv_amount.getText().toString();

                String six_text =tv_date_of_use.getText().toString();
                String seventh_text =id_tex1.getText().toString();
                String  eight =id_tex2.getText().toString();
                PdfCreator(first_text,second_text,third_text,fourth_text,fifth_text,six_text,seventh_text,eight);



            }
        });
    }


    public interface IUniActionDialogOnClickListener {

        void onPositiveClick();

    }

    public interface IMultiActionDialogOnClickListener {

        void onPositiveClick();

        void onNegativeClick();

    }
    public interface IMultiActionDialogOnClickListenerInVoice {

        void onPositiveClickInvoice(Bitmap bitmap);

        void onNegativeClick();

    }




    public static void PdfCreator(String one,String two,String three,
                                  String four,String five,String six,String seven,String eight)
    {


        //////////////////////////////////
        // Create Temp File to save Pdf To
      //  final  File savedPDFFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());

        final File savedPDFFile = FileManager.getInstance().createTempFile(CarryParkApplication.getCurrentContext(), "pdf", false);
// Generate Pdf From Html


        PDFUtil.generatePDFFromHTML(CarryParkApplication.getCurrentContext(), savedPDFFile, "<!DOCTYPE html>\n" +
                "<html>\n" +
                "\n" +
                "<body>\n" +"<style>\n" +
                "h3 {text-align: center;}\n" +
                "p {text-align: center;}\n" +
                "div {text-align: center;}\n" +
                "</style>"+
                "\n" +
                "<div style=\"text-align: center\">\n" +
                "\n" +
                "<br>"+

                "<br>"+
                "<br>"+
                "<br>"+
                "<br>"+

                "<br>"+
                "<br>"+
                "<br>"+
                "<br>"+
                "<img src=\"http://167.172.39.84/images/cp_withouticon_logo.png\" width=\"200\" height=\"70\" align=\"center\">\n" +
                "</div>"+
                "\n" +
                "<p>"+one+"</p>\n" +
                "<p>"+two+"</p>\n" +
                "\n" +
                "<p>"+three+"</p>\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "<h3>"+four+"</h3>\n" +
                "<h3>"+five+"</h3>\n" +
                "\n" +

                "<hr style=\"width:100%;text-align:left;margin-left:0\">\n" +
                "\n" +
                "<p>"+six+"</p>" +
                "<br>"+
                "<br>"+
               seven+"\n" +"<br>"+
               eight+
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "</body>\n" +
                "</html>\n", new PDFPrint.OnPDFPrintListener() {
            @Override
            public void onSuccess(File file) {
                // Open Pdf Viewer
                Uri pdfUri = Uri.fromFile(savedPDFFile);
            /* File path=   Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                File sourceFile = savedPDFFile;
                File destFile = new File(path+"carrypark.pdf");

                if (sourceFile.renameTo(destFile)) {
                    System.out.println("File moved successfully");
                } else {
                    System.out.println("Failed to move file");
                }*/
              //  moveFile("/storage/emulated/0/Android/data/net.simplifiedcoding.carrypark/files/temp/1606406174843.pdf","/storage/emulated/0/Download/carryp.pdf");
                copyFileOrDirectory(pdfUri.getPath(),"/storage/emulated/0/Download/carrypark");
               CarryParkApplication.setOpenPdfLocation(pdfUri.getPath());
                Intent intentPdfViewer = new Intent(CarryParkApplication.getCurrentActivity(), PdfViewerActivity.class);
                intentPdfViewer.putExtra(PdfViewerActivity.PDF_FILE_URI, pdfUri);

                CarryParkApplication.getCurrentContext().startActivity(intentPdfViewer);
            }

            @Override
            public void onError(Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    private static void moveFile(String inputPath, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath );
            out = new FileOutputStream(outputPath );

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(inputPath ).delete();


        }

        catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }
    public static void copyFileOrDirectory(String srcDir, String dstDir) {

        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());

            if (src.isDirectory()) {

                String files[] = src.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1);

                }
            } else {
                copyFile(src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    public static void showCustDialog(Activity parentActivity, String alertMessage,
                                      String first_name, String email, String mobile, String cmpny_name, final IUniActionDialogOnClickListener iUniActionDialogOnClickListener) {

        View dialogView = (CardView) parentActivity.getLayoutInflater().inflate(R.layout.cust_alert_dialog, null, false);


        TextView tv_alert_message = (TextView) dialogView.findViewById(R.id.tv_an_error);
        TextView tv_cntct_cmpny_name = (TextView) dialogView.findViewById(R.id.tv_cmpny_name);
        TextView tv_person_charge_name = (TextView) dialogView.findViewById(R.id.tv_charge_name);
        TextView tv_charge_phn = (TextView) dialogView.findViewById(R.id.tv_charge_phn);
        TextView tv_charge_per_mail = (TextView) dialogView.findViewById(R.id.tv_charge_per_mail);

        TextView tv_person_charge_phn = (TextView) dialogView.findViewById(R.id.tv_person_charge_phn);
        TextView tv_person_charge_email = (TextView) dialogView.findViewById(R.id.tv_person_charge_email);

        tv_person_charge_phn.setPaintFlags(tv_person_charge_phn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tv_person_charge_email.setPaintFlags(tv_person_charge_phn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tv_person_charge_phn.setText(mobile);
        tv_person_charge_email.setText(email);
        Button ok = (Button) dialogView.findViewById(R.id.bt_ok);
        //tv_alert_message.setText(alertMessage);
        tv_person_charge_name.setText(parentActivity.getResources().getString(R.string.MaintenancePerson)+" "+first_name);
        tv_cntct_cmpny_name.setText(parentActivity.getResources().getString(R.string.MaintenanceCompany)+" "+cmpny_name);


        tv_charge_phn.setText(parentActivity.getResources().getString(R.string.MaintenancePersonPhone));
        tv_charge_per_mail.setText(parentActivity.getResources().getString(R.string.MaintenancePersonEmail));

        tv_person_charge_phn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent call_intent = new Intent(Intent.ACTION_DIAL);
                    call_intent.setData(Uri.parse("tel:"+mobile));
                    parentActivity.startActivity(call_intent);
                }catch(Exception e){
                }

            }
        });
        tv_person_charge_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:"+email));
                parentActivity.startActivity(Intent.createChooser(emailIntent, "Send feedback"));
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity).setView(dialogView).setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                iUniActionDialogOnClickListener.onPositiveClick();
                dialog.dismiss();


            }
        });
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(parentActivity.getResources().getColor(R.color.colorAccent));



    }
}


