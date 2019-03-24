package team4.afinal.aclass.team4finalproject;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.io.IOException;
import java.util.List;

import team4.afinal.aclass.team4finalproject.bean.GiverBean;
import team4.afinal.aclass.team4finalproject.bean.ReceiverBean;
import team4.afinal.aclass.team4finalproject.tab.Activity4_tabMain;

public class Activity8Modify extends AppCompatActivity implements OnMapReadyCallback {

    private EditText edtPay, edtContents;
    private Button btnModify;
    private Spinner spinner;

    private String field;
    private String nField;

    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private FirebaseDatabase mDatabase;
    private ReceiverBean bean;

    private Intent intent;
    private GoogleMap mMap;
    private Button btnAdr;
    private Geocoder geocoder;
    private EditText edtAdr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_8modify);

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        spinner = findViewById(R.id.spinner2);
        edtPay = findViewById(R.id.edtPay);
        edtContents = findViewById(R.id.edtContents);
        btnModify = findViewById(R.id.btnModify);
        intent = getIntent();
        btnAdr = findViewById(R.id.btnAdr);
        edtAdr = findViewById(R.id.edtAdr);
        bean = (ReceiverBean) intent.getSerializableExtra("modifydata");

        edtContents.setText(bean.contents);
        edtPay.setText(bean.pay);
        edtAdr.setText(bean.address);


        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                field = spinner.getSelectedItem().toString();
                upload();
                Intent i = new Intent(Activity8Modify.this, Activity4_tabMain.class);
                startActivity(i);
                finish();
            }
        });


        // 로고 클릭 시 메인화면 이동
        Button btnGoMain = findViewById(R.id.btnGoMain);
        btnGoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Activity8Modify.this, Activity3.class);
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
                Intent i = new Intent(Activity8Modify.this, Activity16.class);
                startActivity(i);
                //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //finish();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    } // end onCreate(

    private void upload() {

        DatabaseReference firebaseRef = mDatabase.getReference();
        String id = firebaseRef.push().getKey();

        FirebaseDatabase.getInstance().getReference().child("receiver").child(bean.field).child(bean.name).removeValue();

        //Database에 저장한다.
        Activity1 ac1 = new Activity1();
        bean.field = field;
        bean.pay = edtPay.getText().toString();
        bean.contents = edtContents.getText().toString();
        bean.address = edtAdr.getText().toString();

        firebaseRef.child("receiver").child(field).child(bean.name).setValue(bean);
        Toast.makeText(Activity8Modify.this, "성공적으로 수정되었습니다!",Toast.LENGTH_SHORT).show();

        //다이얼로그 숨기기
        //Utils.hideProgress(Activity7.this);
        finish();

    }//end Upload

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        geocoder = new Geocoder(this);

        // 맵 터치 이벤트 구현 //
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
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
        });
        ////////////////////

        // 버튼 이벤트
        btnAdr.setOnClickListener(new Button.OnClickListener(){
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
        });
        ////////////////////

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(37.6281894, 127.0897268);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
    }

}
