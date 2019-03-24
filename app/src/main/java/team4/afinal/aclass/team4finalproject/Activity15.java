package team4.afinal.aclass.team4finalproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.pixsee.fcm.Message;
import org.pixsee.fcm.Sender;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import team4.afinal.aclass.team4finalproject.adapter.ChatAdapter;
import team4.afinal.aclass.team4finalproject.adapter.ExchangeAdapter;
import team4.afinal.aclass.team4finalproject.bean.ChatBean;
import team4.afinal.aclass.team4finalproject.bean.ExchangeBean;
import team4.afinal.aclass.team4finalproject.fcm.MyFirebaseMessagingService;
import team4.afinal.aclass.team4finalproject.util.PrefUtil;

import static team4.afinal.aclass.team4finalproject.Activity1.mLoginedInfoBean;

//품앗이 상세보기 화면

public class Activity15 extends AppCompatActivity {

    TextView txtMember, txtContents, txtTitle;
    ListView lstChat;
    Button btnEnter, btnDel, btnModify, uploadChat;
    EditText edtChat;
    public static ExchangeBean exchangeBean;

    private FirebaseDatabase mDatabase;

    private List<ChatBean> mChatList = new ArrayList<>();
    private ChatAdapter mChatAdapter;
    private ChatBean chatBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_15);

        mDatabase = FirebaseDatabase.getInstance();
        Intent intent = getIntent();
        exchangeBean = (ExchangeBean) getIntent().getSerializableExtra(ExchangeBean.class.getName());

        txtMember=findViewById(R.id.txtMember);
        txtContents=findViewById(R.id.txtContents);
        txtTitle = findViewById(R.id.txtTitle);

        btnDel = findViewById(R.id.btnDel);
        btnEnter = findViewById(R.id.btnEnter);
        btnModify = findViewById(R.id.btnModify);
        uploadChat = findViewById(R.id.btnUploadChat);

        lstChat = findViewById(R.id.lstChat);
        edtChat = findViewById(R.id.edtChat);

        txtMember.setText(exchangeBean.num);
        txtContents.setText(exchangeBean.contents);
        txtTitle.setText(exchangeBean.title);

        // 오카 입장
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent kakao = new Intent(Intent.ACTION_VIEW, Uri.parse(exchangeBean.kakaoUrl));

                //fcm 메시지 보내기
                Sender fcm = new Sender(MyFirebaseMessagingService.API_KEY);
                Message message = new Message.MessageBuilder()
                        .toToken(exchangeBean.key) // single android/ios device
                        .addData("title", "회원님이 오픈 채팅방에 누군가가 입장했습니다.")
                        .addData("content", exchangeBean.studentId+" 회원님의 품앗이 글에 등록해놓으신 오픈 채팅방에 누군가가 입장했습니다..")
                        .addData("notiType", "A")
                        .build();
                fcm.send(message);

                notiUpload();

                startActivity(kakao);
            }
        });

        // 수정 페이지로 이동
        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity1 ac1 = new Activity1();

                if(exchangeBean.studentId.equals(ac1.mLoginedInfoBean.num)){
                    Intent i = new Intent(Activity15.this, Activity15Modify.class);
                    i.putExtra("modifydata", exchangeBean);
                    startActivity(i);
                } else {
                    Toast.makeText(Activity15.this, "본인이 작성한 글이 아니어서 수정을 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 삭제
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity15.this);
                builder.setTitle("알림");
                builder.setMessage("정말로 삭제하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Activity1 ac1= new Activity1();
                        if(exchangeBean.studentId.equals(ac1.mLoginedInfoBean.num)) {
                            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                            String emailUUID = Activity2.getUserIdFromUUID(email);
                            FirebaseDatabase.getInstance().getReference().child("exchange").child(exchangeBean.category).child(exchangeBean.id).removeValue();
                            Intent i = new Intent(Activity15.this, Activity13.class);
                            startActivity(i);
                        }else Toast.makeText(Activity15.this, "본인이 작성한 글이 아니어서 삭제를 할 수 없습니다.", Toast.LENGTH_SHORT).show();
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

        // 등록

        uploadChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fcm 메시지 보내기
                Sender fcm = new Sender(MyFirebaseMessagingService.API_KEY);
                Message message = new Message.MessageBuilder()
                        .toToken(exchangeBean.key) // single android/ios device
                        .addData("title", "회원님의 글에 댓글이 달렸습니다.")
                        .addData("content", exchangeBean.studentId+" 회원님의 품앗이 글에 댓글이 달렸습니다..")
                        .addData("notiType", "B")
                        .addData("cate", exchangeBean.category)
                        .addData("exchId", exchangeBean.id)
                        .build();
                fcm.send(message);
                upload();
            }
        });


        // 로고 클릭 시 메인화면 이동
        Button btnGoMain = findViewById(R.id.btnGoMain);
        btnGoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Activity15.this, Activity3.class);
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
                Intent i = new Intent(Activity15.this, Activity16.class);
                startActivity(i);
                //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //finish();
            }
        });

        DatabaseReference chat = mDatabase.getReference().child("chat");

        if(chat!=null){
            chat.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //실시간으로 서버가 변경된 내용이 있을 경우 호출된다.

                    //기존 리스트는 날려버린다.
                    if(mChatList!=null) {
                        mChatList.clear();
                    }

                    //리스트를 서버로부터 온 데이터로 새로 만든다.
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String str = snapshot.getKey();
                        if(str.toString().equals(exchangeBean.id)) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                ChatBean bean = snapshot1.getValue(ChatBean.class);
                                mChatList.add(bean);
                            }
                        }
                    }

                    //어댑터 적용
                    mChatAdapter = new ChatAdapter(Activity15.this, mChatList);
                    lstChat.setAdapter(mChatAdapter);
                    setListViewHeightBasedOnChildren(lstChat);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }


            });


        }
    }   //end onCreate()

    void upload(){
        //이미지 업로드가 끝나면 호출되는 CallBack 메서드
        //해야될 일 : Uploaded된 이미지 URL과 사용자가 작성한
        //메모의 내용을 RealTime DB에 업로드 시킨다.
        DatabaseReference firebaseRef = mDatabase.getReference();
        String id = firebaseRef.push().getKey();

        //DATABASE 에 저장한다.
        ChatBean mChatBean = new ChatBean();
        Activity1 ac1 = new Activity1();

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Calendar time = Calendar.getInstance();
        String format_time1 = format.format(time.getTime());
        mChatBean.textId = exchangeBean.id;
        mChatBean.reId= id;
        mChatBean.num =ac1.mLoginedInfoBean.num;
        mChatBean.time = format_time1;
        mChatBean.contents =  edtChat.getText().toString();



        firebaseRef.child("chat").child(mChatBean.textId).child(mChatBean.reId).setValue(mChatBean);
        Toast.makeText(Activity15.this, "서버 글쓰기 성공", Toast.LENGTH_SHORT).show();

        edtChat.setText("");



    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
    private void notiUpload() {

        DatabaseReference firebaseRef = mDatabase.getReference();

        //Database에 저장한다.
        team4.afinal.aclass.team4finalproject.bean.NotiBean bean= new team4.afinal.aclass.team4finalproject.bean.NotiBean();

        bean.requestID = Activity1.mLoginedInfoBean.num;
        bean.studentID = exchangeBean.studentId;
        bean.notiId = exchangeBean.id;
        bean.category = "1";

        firebaseRef.child("noti").child(bean.studentID).child(bean.notiId).setValue(bean);
        Toast.makeText(Activity15.this, "요청보내기 성공!!",Toast.LENGTH_SHORT).show();

        finish();

    }//end Upload

}