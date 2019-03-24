package team4.afinal.aclass.team4finalproject;

import android.Manifest;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import team4.afinal.aclass.team4finalproject.bean.InfoBean;
import team4.afinal.aclass.team4finalproject.util.PrefUtil;
import team4.afinal.aclass.team4finalproject.util.Utils;


public class Activity2 extends AppCompatActivity /*implements OnMapReadyCallback*/{

    private GoogleMap mMap;
    //사진이 저장된 경로
    private Uri mCaptureUri;
    //사진이 저장된 단말기상의 실제경로
    private String mPhotoPath;
    //onActivityResult에서 사용하는 구분값
    public static final int REQUEST_IMAGE_CAPTURE = 200;

    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private FirebaseDatabase mDatabase;

    private Button btnJoin;
    private EditText edtName, edtNum, edtPw, edtKakaoID;
    private ImageView imgPhoto;

    private Button btnAdr;
    private Geocoder geocoder;
    private EditText edtAdr;


    //final TextView tv = findViewById(R.id.txtConvert); // 결과창

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();

        btnJoin = findViewById(R.id.btnJoin);
        edtName = findViewById(R.id.edtName);
        edtNum = findViewById(R.id.edtNum);
        edtPw = findViewById(R.id.edtPw);
        imgPhoto = findViewById(R.id.imgPhoto);
        edtKakaoID = findViewById(R.id.edtKakaoID);

        btnAdr = findViewById(R.id.btnAdr);
        edtAdr = findViewById(R.id.edtAdr);


        //카메라 사용하기 위한 퍼미션을 요청한다.
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA
        }, 0);

        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //createUser를 사용하여 아이디와 비밀번호를 등록한다.
                String id = edtNum.getText().toString() + "@naver.com";
                String pw = edtPw.getText().toString();
                createUser(id, pw);
            }
        });

       /* SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
*/
    }   //end oncreate


    //사용자로 등록하기
    private void createUser(String email, String pass) {
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //회원가입을 성공하면 upload를 호출해 정보를 입력한다.
                    upload(mCaptureUri);
                } else {
                    //회원가입 실패
                    Toast.makeText(Activity2.this, "중복된 학번입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }       //end createUser

    /***************** 카메라 관련 Functions - Start *****************/
    private void takePicture() {

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mCaptureUri = Uri.fromFile(getOutPutMediaFile());
        } else {
            mCaptureUri = FileProvider.getUriForFile(this,
                    "team4.afinal.aclass.team4finalproject.fileprovider", getOutPutMediaFile());
        }

        i.putExtra(MediaStore.EXTRA_OUTPUT, mCaptureUri);

        //내가 원하는 액티비티로 이동하고, 그 액티비티가 종료되면 (finish되면)
        //다시금 나의 액티비티의 onActivityResult() 메서드가 호출되는 구조이다.
        //내가 어떤 데이터를 받고 싶을때 상대 액티비티를 호출해주고 그 액티비티에서
        //호출한 나의 액티비티로 데이터를 넘겨주는 구조이다. 이때 호출되는 메서드가
        //onActivityResult() 메서드 이다.
        startActivityForResult(i, REQUEST_IMAGE_CAPTURE);

    }

    private File getOutPutMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "cameraDemo");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        mPhotoPath = file.getAbsolutePath();

        return file;
    }

    private void sendPicture(String imgFilePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imgFilePath);
        Bitmap resizedBmp = getResizedBitmap(bitmap, 4, 100, 100);

        bitmap.recycle();

        //사진이 캡쳐되서 들어오면 뒤집어져 있다. 이애를 다시 원상복구 시킨다.
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imgFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int exifOrientation;
        int exifDegree;
        if (exif != null) {
            exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            exifDegree = exifOrientToDegree(exifOrientation);
        } else {
            exifDegree = 0;
        }
        Bitmap rotatedBmp = roate(resizedBmp, exifDegree);
        imgPhoto.setImageBitmap(rotatedBmp);
    }

    private int exifOrientToDegree(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap roate(Bitmap bmp, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
                matrix, true);
    }

    //비트맵의 사이즈를 줄여준다.
    public static Bitmap getResizedBitmap(Bitmap srcBmp, int size, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = size;
        Bitmap resized = Bitmap.createScaledBitmap(srcBmp, width, height, true);
        return resized;
    }

    public static Bitmap getResizedBitmap(Resources resources, int id, int size, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = size;
        Bitmap src = BitmapFactory.decodeResource(resources, id, options);
        Bitmap resized = Bitmap.createScaledBitmap(src, width, height, true);
        return resized;
    }

    /***************** 카메라 관련 Functions - End *****************/


    private void upload(final Uri fileUri) {
        if (fileUri == null) {
            return;
        }

        //image 파일을 파이어베이스 서버에 업로드 한다.
        //가장 먼저, FirebaseStorage 인스턴스를 생성한다.
        //Storage를 추가하면 상단에 gs://로 시작하는 스키마를 확인할 수 있다.
        FirebaseStorage fs = FirebaseStorage.getInstance("gs://swufinalproject.appspot.com/");

        //위에서 생성한 FirebaseStorage를 참조하는 storage를 생성한다.
        StorageReference storageRef = fs.getReference();

        //위의 저장소를 참조하는 images폴더를 지정해서 이미지를 올린다.
        final StorageReference imageRef = storageRef.child("images/" + fileUri.getLastPathSegment());

        //다이얼로그 보이기
        Utils.showProgress(Activity2.this);

        //실제파일 업로드 실행
        UploadTask uploadTask = imageRef.putFile(fileUri);

        //파일 업로드 성공/실패에 대한 callback 메서드를 받아서 핸들링 한다.
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                //이미지 파일이 올라간 실제 URL 주소를 리턴한다,
                //누구한테? addOnCompleteListenner 에게
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

                //이미지 업로드가 끝나면 호출되는 CallBack 메서드
                //해야될 일: Upload 된 이미지 URL 과 사용자가 작성한 메모의 내용을
                //RealTime DB에 업로드 시킨다.
                DatabaseReference firebaseRef = mDatabase.getReference();
                String id = firebaseRef.push().getKey();   //key를 id로 사용

                // 2. prefUtil에서 값 가져오기
                String token = PrefUtil.getData(Activity2.this, "token");

                //Database에 저장한다.
                final InfoBean bean = new InfoBean();
                bean.id = id;
                bean.kakaoID = edtKakaoID.getText().toString();
                bean.num = edtNum.getText().toString();
                bean.key = token; // 3. infoBean에 있는 key 변수에 토큰 값 저장
                bean.imgUri = task.getResult().toString();  //이미지 다운로드 가능한 URL
                bean.imgName = fileUri.getLastPathSegment();    //파일이름도 저장
                bean.pw = edtPw.getText().toString();
                bean.userId = edtNum.getText().toString() + "@naver.com";
                bean.name = edtName.getText().toString();
                bean.address = edtAdr.getText().toString();

                //userEmail의 고유번호를 기준으로 사용자 데이터를 쌓기 위해서 고유키를 생성한다.
                String userIdUUID = getUserIdFromUUID(bean.userId);

                // 4. 서버에 infoBean 업데이트
                firebaseRef.child("info").child(userIdUUID).setValue(bean);

                Toast.makeText(Activity2.this, "정보가 성공적으로 입력되었습니다.", Toast.LENGTH_SHORT).show();

                //다이얼로그 숨기기
                Utils.hideProgress(Activity2.this);
                finish();
            }
        });

    }   //end upload

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //카메라로부터 오는 데이터를 취득한다.
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                sendPicture(mPhotoPath);
            }
        }
    }// end onActivityResult()

    public static String getUserIdFromUUID(String userEamil) {
        long val = UUID.nameUUIDFromBytes(userEamil.getBytes()).getMostSignificantBits();
        return val + "";
    }

 /*   @Override
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
*/

}   //end class
