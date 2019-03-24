package team4.afinal.aclass.team4finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.UUID;
import java.util.regex.Pattern;
import team4.afinal.aclass.team4finalproject.bean.InfoBean;
import team4.afinal.aclass.team4finalproject.util.PrefUtil;
import team4.afinal.aclass.team4finalproject.util.Utils;


public class Activity1 extends AppCompatActivity {
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");

    //파이어베이스 인증 객체 생성
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase mDatabase;

    private EditText edtId, edtPw;
    private Button btnLogin, btnJoin;
    private CheckBox btnCheck;      //자동로그인 체크박스 버튼

    //로그인 사용자의 정보
    public static InfoBean mLoginedInfoBean;
    String newToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);

//FCM 토큰을 받아온다.
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                //단말기에서 토큰을 뽑아내서 쓸라고
                newToken = instanceIdResult.getToken();
                Log.e("SWU", newToken);

                // 1. prefutil에 저장
                PrefUtil.setData(Activity1.this, "token", newToken);

                //서버로 업로드 해야함
                //회원가입을 할 때 bean에 값을 넣어야 함

                //TODO LIST
                // 1. 회원가입 페이지로 토큰 값 넘기기
                // 2. 회원가입 페이지에서 토큰 값 받기
                // 3. infoBean에 있는 key 변수에 토큰 값 저장
                // 4. 서버에 infoBean 업데이트
                // 5. 로그인 페이지에 돌아와서 서버 info database를 받아옴
                // 6. 서버 info database에 있는 key 값과 현재 newToken 값이 같다면 그대로 놔두고
                // 6-1. 다르다면 현재 token값을 key값에 넣어서 서버로 다시 올림.
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        btnCheck = findViewById(R.id.btnCheck);
        edtId = findViewById(R.id.edtId);
        edtPw = findViewById(R.id.edtPw);
        btnJoin = findViewById(R.id.btnJoin);
        btnLogin = findViewById(R.id.btnLogin);

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Activity1.this,Activity2.class);
                startActivity(i);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = edtId.getText().toString() + "@naver.com";
                String pass = edtPw.getText().toString();

                if(isValidEmail(id) && isValidPasswd(pass)) {
                    //로그인 하겠다. FireBase에
                    //자동 로그인 체크 확인
                    Utils.setData(Activity1.this,"auto",btnCheck.isChecked());
                    //로그인 실행
                    loginUser(id,pass);
                }
            }
        });

    }   //end oncreate


    private void loginUser(String email, String pass) {
        //다이얼로그 보이기
        Utils.showProgress(Activity1.this);
        firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(Activity1.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(Activity1.this,"로그인 성공",Toast.LENGTH_SHORT).show();

                    //TODO 서버에서 성공한 사람의 정보를 가져온다.
                    //데이터 목록을 Firebase로부터 가져온다.
                    // 5. 로그인 페이지에 돌아와서 서버 info database를 받아옴
                    String emailUUID = Activity2.getUserIdFromUUID(firebaseAuth.getCurrentUser().getEmail());
                    mDatabase.getReference().child("info").child(emailUUID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // 실시간으로 서버가 변경된 내용이 있을 경우 호출된다.
                            InfoBean bean = dataSnapshot.getValue(InfoBean.class);
                            // 6. 서버 info database에 있는 key 값과 현재 newToken 값이 같다면 그대로 놔두고
                            // 6-1. 다르다면 현재 token값을 key값에 넣어서 서버로 다시 올림.
                            if(newToken.equals(bean.key)) {
                                mLoginedInfoBean = bean;
                            }else{
                                DatabaseReference firebaseRef = mDatabase.getReference();
                                String userIdUUID = getUserIdFromUUID(bean.userId);

                                bean.key=newToken;
                                mLoginedInfoBean = bean;
                                firebaseRef.child("info").child(userIdUUID).setValue(bean);
                            }
                            goBoardActivity();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });

                }
                else {
                    Toast.makeText(Activity1.this,"로그인 실패",Toast.LENGTH_SHORT).show();
                }
                //다이얼로그 숨기기
                Utils.hideProgress(Activity1.this);
            }
        });
    }

    private boolean isValidEmail(String email) {
        if (email.isEmpty()) {
            // 이메일 공백
            Toast.makeText(Activity1.this,"이메일 공백입니다.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // 이메일 형식 불일치
            Toast.makeText(Activity1.this,"이메일 형식 불일치입니다.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    // 비밀번호 유효성 검사
    private boolean isValidPasswd(String password) {
        if (password.isEmpty()) {
            // 비밀번호 공백
            Toast.makeText(Activity1.this,"비밀번호 공백입니다.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            // 비밀번호 형식 불일치
            Toast.makeText(Activity1.this,"비밀번호 형식 불일치입니다.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }


    private void goBoardActivity() {
        Intent i = new Intent(Activity1.this,Activity3.class);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //자동로그인이 체크되었나 확인하고 되어있으면 자동으로 Activity3로 넘어감
        boolean isAutoLoginChecked = Utils.getData(Activity1.this,"auto");

        if(isAutoLoginChecked) {
            //TODO 서버에서 성공한 사람의 정보를 가져온다.
            //데이터 목록을 Firebase로부터 가져온다.
            String emailUUID = Activity2.getUserIdFromUUID(firebaseAuth.getCurrentUser().getEmail());
            mDatabase.getReference().child("info").child(emailUUID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // 실시간으로 서버가 변경된 내용이 있을 경우 호출된다.
                    InfoBean bean = dataSnapshot.getValue(InfoBean.class);

                    if(newToken.equals(bean.key)) {
                        mLoginedInfoBean = bean;
                    }else{
                        DatabaseReference firebaseRef = mDatabase.getReference();
                        String userIdUUID = getUserIdFromUUID(bean.userId);

                        bean.key=newToken;
                        mLoginedInfoBean = bean;
                        firebaseRef.child("info").child(userIdUUID).setValue(bean);
                    }

                    goBoardActivity();
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }


    public static String getUserIdFromUUID(String userEamil) {
        long val = UUID.nameUUIDFromBytes(userEamil.getBytes()).getMostSignificantBits();
        return val + "";
    }

}