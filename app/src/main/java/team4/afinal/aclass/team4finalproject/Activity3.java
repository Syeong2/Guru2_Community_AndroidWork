package team4.afinal.aclass.team4finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import team4.afinal.aclass.team4finalproject.tab.Activity4_tabMain;
import team4.afinal.aclass.team4finalproject.util.Utils;

public class Activity3 extends AppCompatActivity {

    Button btnpum, btnjaneung;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);

        btnLogout = findViewById(R.id.btnLogout);
        btnjaneung = findViewById(R.id.btnjaneung);

        btnpum =findViewById(R.id.btnpum);
        btnjaneung = findViewById(R.id.btnjaneung);

        btnpum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Activity3.this, Activity13.class);
                startActivity(i);
            }
        });

        //로그아웃 버튼을 누르면 로그인 화면으로 넘어가고 자동로그인 체크 해제됨
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.setData(Activity3.this,"auto",false);
                Intent i = new Intent(Activity3.this,Activity1.class);
                startActivity(i);
                finish();
            }
        });

        btnjaneung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Activity3.this, Activity4_tabMain.class);
                startActivity(i);
            }
        });

        // 로고 클릭 시 메인화면 이동
        Button btnGoMain = findViewById(R.id.btnGoMain);
        btnGoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Activity3.this, Activity3.class);
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
                Intent i = new Intent(Activity3.this, Activity16.class);
                startActivity(i);
                //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //finish();
            }
        });

    }
}
