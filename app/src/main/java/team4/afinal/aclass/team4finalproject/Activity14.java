package team4.afinal.aclass.team4finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import team4.afinal.aclass.team4finalproject.bean.ExchangeBean;
import team4.afinal.aclass.team4finalproject.util.Utils;


//품앗이 글쓰기 화면

public class Activity14 extends AppCompatActivity {

    private Spinner spinner;
    private EditText edtTitle, edtNum, edtUrl, edtContents;
    private Button btnWrite;

    private String category;
    private String categoryNum;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_14);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        spinner=findViewById(R.id.Spinner);

        edtTitle = findViewById(R.id.edtTitle);
        edtNum = findViewById(R.id.edtNum);
        edtUrl = findViewById(R.id.edtUrl);
        edtContents = findViewById(R.id.edtContents);
        btnWrite = findViewById(R.id.btnWrite);

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = spinner.getSelectedItem().toString();
                upload();
            }
        });

        // 로고 클릭 시 메인화면 이동
        Button btnGoMain = findViewById(R.id.btnGoMain);
        btnGoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Activity14.this, Activity3.class);
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
                Intent i = new Intent(Activity14.this, Activity16.class);
                startActivity(i);
                //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //finish();
            }
        });
    }// end onCreate()

    private void upload(){

        //프로그레스바 보이기 시작

        //이미지 업로드가 끝나면 호출되는 CallBack 메서드
        //해야될 일 : Uploaded된 이미지 URL과 사용자가 작성한
        //메모의 내용을 RealTime DB에 업로드 시킨다.
        DatabaseReference firebaseRef = mDatabase.getReference();
        String id = firebaseRef.push().getKey();

        //DATABASE 에 저장한다.
        ExchangeBean mExchangeBean = new ExchangeBean();
        Activity1 ac1 = new Activity1();

        mExchangeBean.id=id;
        mExchangeBean.studentId=ac1.mLoginedInfoBean.num;
        //키 값 받아오기
        mExchangeBean.key = ac1.mLoginedInfoBean.key;
        mExchangeBean.title = edtTitle.getText().toString();
        mExchangeBean.contents =  edtContents.getText().toString();
        mExchangeBean.num = edtNum.getText().toString();
        mExchangeBean.kakaoUrl = edtUrl.getText().toString();

        if(category.equals("IT")){
            categoryNum = "1";
        }else if(category.equals("경영/경제")){
            categoryNum = "2";
        }else if(category.equals("자연/과학")){
            categoryNum = "3";
        }else if(category.equals("언어")){
            categoryNum = "4";
        }else if(category.equals("예체능")){
            categoryNum = "5";
        }else categoryNum = "6";

        mExchangeBean.category = categoryNum;

        firebaseRef.child("exchange").child(categoryNum).child(mExchangeBean.id).setValue(mExchangeBean);
        Toast.makeText(Activity14.this, "서버 글쓰기 성공", Toast.LENGTH_SHORT).show();

        finish();
    }
}