package team4.afinal.aclass.team4finalproject.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URL;
import java.util.List;

import team4.afinal.aclass.team4finalproject.Activity1;
import team4.afinal.aclass.team4finalproject.Activity2;
import team4.afinal.aclass.team4finalproject.DownImgTask;
import team4.afinal.aclass.team4finalproject.R;
import team4.afinal.aclass.team4finalproject.bean.ChatBean;

public class ChatAdapter extends BaseAdapter {

    private Context mContext;
    private List<ChatBean> mList;


    public ChatAdapter(Context context, List<ChatBean> list) {
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
        convertView = inflater.inflate(R.layout.view_chat, null);

        final ChatBean bean = mList.get(position);


        TextView txtNum = convertView.findViewById(R.id.txtNum);
        TextView txtDate = convertView.findViewById(R.id.txtDate);
        TextView txtContents = convertView.findViewById(R.id.txtContents);
        Button btnDel = convertView.findViewById(R.id.btnDel);


        txtNum.setText(bean.num);
        txtDate.setText(bean.time);
        txtContents.setText(bean.contents);
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("알림");
                builder.setMessage("정말로 삭제하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Activity1 ac1= new Activity1();
                        if(bean.num.equals(ac1.mLoginedInfoBean.num)) {
                            FirebaseDatabase.getInstance().getReference().child("chat").child(bean.textId).child(bean.reId).removeValue();
                        }else Toast.makeText(mContext, "본인이 작성한 글이 아니어서 삭제를 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        return convertView;
    }
}