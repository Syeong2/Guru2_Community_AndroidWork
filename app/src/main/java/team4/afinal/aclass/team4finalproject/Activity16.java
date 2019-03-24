package team4.afinal.aclass.team4finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import team4.afinal.aclass.team4finalproject.adapter.ExchangeAdapter;
import team4.afinal.aclass.team4finalproject.adapter.NotiAdapter;
import team4.afinal.aclass.team4finalproject.bean.ExchangeBean;
import team4.afinal.aclass.team4finalproject.bean.NotiBean;

public class Activity16 extends AppCompatActivity {

    private FirebaseDatabase mDatabase;


    private List<NotiBean> mNotiList = new ArrayList<>();
    private NotiAdapter mNotiAdapter;
    private ListView lstAlram;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_16);

        lstAlram = findViewById(R.id.lstAlarm);
        Activity1 act1 = new Activity1();

        mDatabase = FirebaseDatabase.getInstance();

        DatabaseReference noti = mDatabase.getReference().child("noti").child(act1.mLoginedInfoBean.num);
        if(noti!=null) {
                noti.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //실시간으로 서버가 변경된 내용이 있을 경우 호출된다.

                            //기존 리스트는 날려버린다.
                            if (mNotiList != null) {
                                mNotiList.clear();
                            }

                            //리스트를 서버로부터 온 데이터로 새로 만든다.
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    NotiBean bean = snapshot.getValue(NotiBean.class);
                                    mNotiList.add(bean);
                            }

                            mNotiAdapter = new NotiAdapter(Activity16.this, mNotiList);
                            lstAlram.setAdapter(mNotiAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

        // 로고 클릭 시 메인화면 이동
        Button btnGoMain = findViewById(R.id.btnGoMain);
        btnGoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Activity16.this, Activity3.class);
                startActivity(i);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
            }
        });

        // 알림 클릭 시 알림 페이지 이동
        Button btnNoti = findViewById(R.id.btnNoti);
        btnNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Activity16.this, Activity16.class);
                startActivity(i);
                //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //finish();
            }
        });

    }
}