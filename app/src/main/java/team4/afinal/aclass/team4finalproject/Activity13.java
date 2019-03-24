package team4.afinal.aclass.team4finalproject;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import team4.afinal.aclass.team4finalproject.bean.ExchangeBean;
import team4.afinal.aclass.team4finalproject.adapter.ExchangeAdapter;
import team4.afinal.aclass.team4finalproject.util.Utils;


// 품앗이 리스트 뷰 뜨는 화면

public class Activity13 extends AppCompatActivity {

    private Button btnIt, btnEconomy, btnScience, btnLanguage, btnArt, btnEtc;
    private Button btnWrite;
    private ListView lstContents;
    public String category;

    private FirebaseDatabase mDatabase;

    private List<ExchangeBean> mExchangeList = new ArrayList<>();
    private ExchangeAdapter mExchangeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_13);

        mDatabase = FirebaseDatabase.getInstance();

        //btnWrite = findViewById(R.id.btnWrite);

        btnIt = findViewById(R.id.btnIt);
        btnEconomy = findViewById(R.id.btnEconomy);
        btnScience = findViewById(R.id.btnScience);
        btnLanguage = findViewById(R.id.btnLanguage);
        btnArt = findViewById(R.id.btnArt);
        btnEtc = findViewById(R.id.btnEtc);

        lstContents = findViewById(R.id.lstContents);

        //It 버튼 이벤트
        btnIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = "1";
                check();
            }
        });

        btnEconomy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = "2";
                check();
            }
        });

        btnScience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = "3";
                check();
            }
        });

        btnLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = "4";
                check();
            }
        });

        btnArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = "5";
                check();
            }
        });

        btnEtc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = "6";
                check();
            }
        });


        //글쓰기 버튼 이벤트
        findViewById(R.id.btnWrite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Activity13.this, Activity14.class);
                startActivity(i);
            }
        });

        // 로고 클릭 시 메인화면 이동
        Button btnGoMain = findViewById(R.id.btnGoMain);
        btnGoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Activity13.this, Activity3.class);
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
                Intent i = new Intent(Activity13.this, Activity16.class);
                startActivity(i);
                //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //finish();
            }
        });
    }// end OnCreate();

    void check(){
        mDatabase.getReference().child("exchange").child(category).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //실시간으로 서버가 변경된 내용이 있을 경우 호출된다.

                //기존 리스트는 날려버린다.
                mExchangeList.clear();

                //리스트를 서버로부터 온 데이터로 새로 만든다.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ExchangeBean bean = snapshot.getValue(ExchangeBean.class);
                    mExchangeList.add(bean);
                }

                mExchangeAdapter = new ExchangeAdapter(Activity13.this, mExchangeList);
                lstContents.setAdapter(mExchangeAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //내용 다 받아오기
        DatabaseReference exchange = mDatabase.getReference().child("exchange");

        if(exchange!=null){
            exchange.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //실시간으로 서버가 변경된 내용이 있을 경우 호출된다.

                    //기존 리스트는 날려버린다.
                    if(mExchangeList!=null) {
                        mExchangeList.clear();
                    }

                    //리스트를 서버로부터 온 데이터로 새로 만든다.
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                            ExchangeBean bean = snapshot1.getValue(ExchangeBean.class);
                            mExchangeList.add(bean);
                        }
                    }

                    //어댑터 적용
                    mExchangeAdapter = new ExchangeAdapter(Activity13.this, mExchangeList);
                    lstContents.setAdapter(mExchangeAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}