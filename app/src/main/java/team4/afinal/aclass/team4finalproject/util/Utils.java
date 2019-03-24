package team4.afinal.aclass.team4finalproject.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;

import team4.afinal.aclass.team4finalproject.R;

public class Utils {

    private static Dialog mProgDlg;

    //화면에 프로그레스를 보여준다.
    public static void showProgress(Context context) {
        if (mProgDlg != null && mProgDlg.isShowing()) {
            mProgDlg.hide();
        }


        mProgDlg = new Dialog(context);
        mProgDlg.setContentView(R.layout.view_progress);
        mProgDlg.setCancelable(false);
        mProgDlg.show();

    }   //end showProgress()

    public static void hideProgress(Context context) {
        if(mProgDlg != null) {
            mProgDlg.hide();
        }
    }   //end hideprogress

    public static void setData(Context context, String key, boolean value) {
        SharedPreferences pref = context.getSharedPreferences("test1", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key,value);
        editor.commit();
    }

    public static boolean getData(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences("test1",Activity.MODE_PRIVATE);
        return pref.getBoolean(key,false);
    }
}
