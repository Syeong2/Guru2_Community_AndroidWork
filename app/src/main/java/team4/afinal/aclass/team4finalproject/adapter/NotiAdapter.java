package team4.afinal.aclass.team4finalproject.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.net.URL;
import java.util.List;

import team4.afinal.aclass.team4finalproject.Activity1;
import team4.afinal.aclass.team4finalproject.Activity15;
import team4.afinal.aclass.team4finalproject.Activity16;
import team4.afinal.aclass.team4finalproject.DownImgTask;
import team4.afinal.aclass.team4finalproject.R;
import team4.afinal.aclass.team4finalproject.bean.ExchangeBean;
import team4.afinal.aclass.team4finalproject.bean.NotiBean;

public class NotiAdapter extends BaseAdapter {

    private Context mContext;
    private List<NotiBean> mList;


    public NotiAdapter(Context context, List<NotiBean> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.view_noti, null);

        final NotiBean bean = mList.get(position);

        TextView txtAlarm = convertView.findViewById(R.id.txtAlarm);
        TextView txtStudentId = convertView.findViewById(R.id.txtStudentId);
        TextView txtKakaoId = convertView.findViewById(R.id.txtKakaoId);
        View layTopRow = convertView.findViewById(R.id.layTopRow);

        String category;
        if(bean.category.equals("1")) {
            category = "재능매칭 나누기 페이지에서 요청이 왔습니다.";
        }
        else if(bean.category.equals("2")) {
            category = "재능매칭 받기 페이지에서 요청이 왔습니다.";
        }
        else {
            category = "품앗이 페이지에서 요청이 왔습니다.";
        }

        txtAlarm.setText(category);
        txtStudentId.setText(bean.requestID+"님이 요청을 보냈습니다.");
        if(bean.kakaoID!=null) {
            txtKakaoId.setText("요청하신 분의 카카오톡 아이디는 "+bean.kakaoID + "입니다.");
        }else txtKakaoId.setText("");

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, Activity16.class);
                mContext.startActivity(i);
            }
        });
        return convertView;
    }
}
