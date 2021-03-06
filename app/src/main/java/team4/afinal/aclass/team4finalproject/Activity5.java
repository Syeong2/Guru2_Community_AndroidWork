package team4.afinal.aclass.team4finalproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.storage.FirebaseStorage;

import org.pixsee.fcm.Message;
import org.pixsee.fcm.Sender;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import team4.afinal.aclass.team4finalproject.bean.ExchangeBean;
import team4.afinal.aclass.team4finalproject.bean.GiverBean;
import team4.afinal.aclass.team4finalproject.bean.NotiBean;
import team4.afinal.aclass.team4finalproject.fcm.MyFirebaseMessagingService;
import team4.afinal.aclass.team4finalproject.util.Utils;

public class Activity5 extends AppCompatActivity implements OnMapReadyCallback {

    private ImageView imgGiver;
    private TextView txtName, txtField, txtPay, txtCareer, txtContents;
    public static List<GiverBean> mFoundBeanList = new ArrayList<GiverBean>();


    private GiverBean mGiverBean;


    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private FirebaseDatabase mDatabase;

    private GoogleMap mMap;
    private Button btnAdr;
    private Geocoder geocoder;
    private TextView edtAdr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_5);




        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        imgGiver = findViewById(R.id.imgGiver);
        txtName = findViewById(R.id.txtName);
        txtField = findViewById(R.id.txtField);
        txtPay = findViewById(R.id.txtPay);
        txtCareer = findViewById(R.id.txtCareer);
        txtContents = findViewById(R.id.txtContents);
        btnAdr = findViewById(R.id.btnAdr);
        edtAdr = findViewById(R.id.edtAdr);

        // DB 에서 정보 받아와 bean에 입력
        mGiverBean = (GiverBean) getIntent().getSerializableExtra("detail");

        try {
            new DownImgTask(imgGiver).execute(new URL(mGiverBean.imgUrl));
        } catch (Exception e){
            e.printStackTrace();
        }


        Log.e("SWU", mGiverBean.key);
        // 상세화면 텍스트를 bean값으로 설정
        txtName.setText(mGiverBean.studentId);
        txtField.setText(mGiverBean.field);
        txtPay.setText(mGiverBean.pay);
        txtCareer.setText(mGiverBean.career);
        txtContents.setText(mGiverBean.contents);
        edtAdr.setText((mGiverBean.address));

       /* if(mGiverBean != null) {
            Sender fcm = new Sender(MyFirebaseMessagingService.API_KEY);
            Message message = new Message.MessageBuilder()
                    .toToken(mGiverBean.key) // single android/ios device
                    .addData("title", "글이 등록 되었습니다.")
                    .addData("content", "당신의 글에 댓글이 등록 되었습니다.")
                    .build();
            fcm.send(message);
        }*/

        // 요청 이벤트
       Button btnRequest = findViewById(R.id.btnRequest);
       btnRequest.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               //요청하기 눌렀을 때 메시지 보내기
               if(mGiverBean != null) {
                   AlertDialog.Builder builder = new AlertDialog.Builder(Activity5.this);
                   builder.setTitle("요청하기");
                   builder.setMessage( mGiverBean.studentId + "님께 요청하시겠습니까?");
                   builder.setCancelable(false);
                   builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           //프로그레스 바 보이기
                           Utils.showProgress(Activity5.this);

                           //fcm 메시지 보내기
                           Sender fcm = new Sender(MyFirebaseMessagingService.API_KEY);
                           Message message = new Message.MessageBuilder()
                                   .toToken(mGiverBean.key) // single android/ios device
                                   .addData("title", "회원님이 작성하신 글에 요청이 들어왔습니다.")
                                   .addData("content", mGiverBean.studentId+"회원님의 재능나눔 주기 글에 요청이 들어왔습니다.")
                                   .addData("notiType", "A")
                                   .build();
                           fcm.send(message);
                           upload();

                           //프로그레스 바 숨기기
                           Utils.hideProgress(Activity5.this);
                       }
                   }); // setPositive

                   builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i) {
                           Toast.makeText(Activity5.this, "요청이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                       }
                   }); // setNegative
                   builder.create().show();
               }
           }
       });

        // 로고 클릭 시 메인화면 이동
        Button btnGoMain = findViewById(R.id.btnGoMain);
        btnGoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Activity5.this, Activity3.class);
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
                Intent i = new Intent(Activity5.this, Activity16.class);
                startActivity(i);
                //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //finish();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    } // end onCreate()

    private void upload() {

        DatabaseReference firebaseRef = mDatabase.getReference();

        //Database에 저장한다.
        NotiBean bean= new NotiBean();

        bean.requestID = Activity1.mLoginedInfoBean.num;
        bean.studentID = mGiverBean.studentId;
        bean.kakaoID= Activity1.mLoginedInfoBean.kakaoID;
        bean.notiId = mGiverBean.name;
        bean.category = "1";

        firebaseRef.child("noti").child(bean.studentID).child(bean.notiId).setValue(bean);
        Toast.makeText(Activity5.this, "요청보내기 성공!!",Toast.LENGTH_SHORT).show();

        finish();

    }//end Upload

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        geocoder = new Geocoder(this);

        // 맵 터치 이벤트 구현 //
        /*mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng point) {
                MarkerOptions mOptions = new MarkerOptions();
                // 마커 타이틀
                mOptions.title("마커 좌표");
                Double latitude = point.latitude; // 위도
                Double longitude = point.longitude; // 경도
                // 마커의 스니펫(간단한 텍스트) 설정
                mOptions.snippet(latitude.toString() + ", " + longitude.toString());
                // LatLng: 위도 경도 쌍을 나타냄
                mOptions.position(new LatLng(latitude, longitude));
                // 마커(핀) 추가
                googleMap.addMarker(mOptions);
            }
        });*/
        ////////////////////

        String str=edtAdr.getText().toString();
        List<Address> addressList = null;
        try {
            // editText에 입력한 텍스트(주소, 지역, 장소 등)을 지오 코딩을 이용해 변환
            addressList = geocoder.getFromLocationName(
                    str, // 주소
                    10); // 최대 검색 결과 개수
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(addressList.get(0).toString());
        // 콤마를 기준으로 split
        String []splitStr = addressList.get(0).toString().split(",");
        String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1,splitStr[0].length() - 2); // 주소
        System.out.println(address);

        String latitude = splitStr[10].substring(splitStr[10].indexOf("=") + 1); // 위도
        String longitude = splitStr[12].substring(splitStr[12].indexOf("=") + 1); // 경도
        System.out.println(latitude);
        System.out.println(longitude);

        // 좌표(위도, 경도) 생성
        LatLng point = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        // 마커 생성
        MarkerOptions mOptions2 = new MarkerOptions();
        mOptions2.title(str);
        mOptions2.snippet(address);
        mOptions2.position(point);
        // 마커 추가
        mMap.addMarker(mOptions2);
        // 해당 좌표로 화면 줌
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,17));

        // 버튼 이벤트
        /*btnAdr.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                String str=edtAdr.getText().toString();
                List<Address> addressList = null;
                try {
                    // editText에 입력한 텍스트(주소, 지역, 장소 등)을 지오 코딩을 이용해 변환
                    addressList = geocoder.getFromLocationName(
                            str, // 주소
                            10); // 최대 검색 결과 개수
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println(addressList.get(0).toString());
                // 콤마를 기준으로 split
                String []splitStr = addressList.get(0).toString().split(",");
                String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1,splitStr[0].length() - 2); // 주소
                System.out.println(address);

                String latitude = splitStr[10].substring(splitStr[10].indexOf("=") + 1); // 위도
                String longitude = splitStr[12].substring(splitStr[12].indexOf("=") + 1); // 경도
                System.out.println(latitude);
                System.out.println(longitude);

                // 좌표(위도, 경도) 생성
                LatLng point = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                // 마커 생성
                MarkerOptions mOptions2 = new MarkerOptions();
                mOptions2.title("search result");
                mOptions2.snippet(address);
                mOptions2.position(point);
                // 마커 추가
                mMap.addMarker(mOptions2);
                // 해당 좌표로 화면 줌
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,17));
            }
        });*/
        ////////////////////

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(37.6281894, 127.0897268);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
    }


}