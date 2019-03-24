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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URL;
import java.util.List;

import team4.afinal.aclass.team4finalproject.Activity1;
import team4.afinal.aclass.team4finalproject.Activity2;
import team4.afinal.aclass.team4finalproject.Activity5;
import team4.afinal.aclass.team4finalproject.Activity7Modify;
import team4.afinal.aclass.team4finalproject.bean.GiverBean;
import team4.afinal.aclass.team4finalproject.DownImgTask;
import team4.afinal.aclass.team4finalproject.R;


public class GiverAdapter extends BaseAdapter {
    private Context mContext;
    private List<GiverBean> mList;



    // 생성자
    public GiverAdapter(Context context, List<GiverBean> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        // 인플레이팅 작업
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.view_giver, null);

        final GiverBean bean = mList.get(position);

        //해당 ROW 의 데이터를 찾는 작업
        bean.position = position;

        //인플레이팅 된 뷰에서 ID 찾는작업
        ImageView imgGiver = convertView.findViewById(R.id.imgGiver);
        TextView txtContents = convertView.findViewById(R.id.txtContents);
        TextView txtField = convertView.findViewById(R.id.txtField);
        TextView txtPay = convertView.findViewById(R.id.txtPay);
        Button btnDelete = convertView.findViewById(R.id.btnDelete);
        Button btnModify = convertView.findViewById(R.id.btnModify);


        //삭제 버튼 이벤트
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("알림");
                builder.setMessage("정말로 삭제하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Activity1 ac1= new Activity1();
                        if(bean.studentId.equals(ac1.mLoginedInfoBean.num)) {
                            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                            String emailUUID = Activity2.getUserIdFromUUID(email);
                            FirebaseDatabase.getInstance().getReference().child("giver").child(bean.field).child(bean.name).removeValue();
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


        //데이터 셋팅
        //이벤트 설정(글쓰기)

        try {
            new DownImgTask(imgGiver).execute(new URL(bean.imgUrl));
        } catch (Exception e){
            e.printStackTrace();
        }
        txtContents.setText(bean.contents);
        txtField.setText(bean.field);
        txtPay.setText(bean.pay);


        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity1 ac1 = new Activity1();
                if(bean.studentId.equals(ac1.mLoginedInfoBean.num)){
                    Intent intent = new Intent(mContext, Activity7Modify.class);
                    intent.putExtra("modifydata",bean);

                    mContext.startActivity(intent);
                } else {
                    Toast.makeText(mContext, "본인이 작성한 글이 아니어서 수정을 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, Activity5.class);
                i.putExtra("detail", bean);
                mContext.startActivity(i);

            }
        });


        return convertView;

    }



}