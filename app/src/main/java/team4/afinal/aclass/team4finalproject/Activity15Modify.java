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
import com.google.firebase.storage.FirebaseStorage;

import team4.afinal.aclass.team4finalproject.bean.ExchangeBean;
import team4.afinal.aclass.team4finalproject.bean.GiverBean;
import team4.afinal.aclass.team4finalproject.tab.Activity4_tabMain;

public class Activity15Modify extends AppCompatActivity {

    // 제목, 인원, 내용, 오카주소
    private EditText edtTitle, edtMember, edtContents, edtKakaoUrl;
    private Button btnModify;
    private Spinner spinner; // 분야

    private String category;
    private String categoryNum;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private ExchangeBean bean;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_15modify);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        spinner = findViewById(R.id.spinner);
        edtTitle = findViewById(R.id.edtTitle);
        edtMember = findViewById(R.id.edtMember);
        edtContents = findViewById(R.id.edtContents);
        edtKakaoUrl = findViewById(R.id.edtKakaoUrl);

        btnModify = findViewById(R.id.btnModify);

        intent = getIntent();
        bean = (ExchangeBean)intent.getSerializableExtra("modifydata");

        edtTitle.setText(bean.title);
        edtMember.setText(bean.num);
        edtContents.setText(bean.contents);
        edtKakaoUrl.setText(bean.kakaoUrl);

        // 수정 완료하고 상세 페이지로 이동
        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = spinner.getSelectedItem().toString();
                upload();
                Intent i = new Intent(Activity15Modify.this, Activity15.class);
                i.putExtra(ExchangeBean.class.getName(), bean);
                startActivity(i);
                finish();
            }
        });

        // 로고 클릭 시 메인화면 이동
        Button btnGoMain = findViewById(R.id.btnGoMain);
        btnGoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Activity15Modify.this, Activity3.class);
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
                Intent i = new Intent(Activity15Modify.this, Activity16.class);
                startActivity(i);
                //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //finish();
            }
        });
    } // end onCreate()


    private void upload() {
        DatabaseReference firebaseRef = mDatabase.getReference();
        String id = firebaseRef.push().getKey();

        //Database에 저장한다.
        Activity1 ac1 = new Activity1();

        bean.category = category;
        bean.title = edtTitle.getText().toString();
        bean.num = edtMember.getText().toString();
        bean.contents = edtContents.getText().toString();
        bean.kakaoUrl = edtKakaoUrl.getText().toString();

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

        bean.category = categoryNum;

        firebaseRef.child("exchange").child(categoryNum).child(bean.id).setValue(bean);
        Toast.makeText(Activity15Modify.this, "성공적으로 수정되었습니다!",Toast.LENGTH_SHORT).show();

        finish();

    }//end Upload
}
