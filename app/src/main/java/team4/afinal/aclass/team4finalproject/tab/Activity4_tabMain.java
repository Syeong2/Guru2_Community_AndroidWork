package team4.afinal.aclass.team4finalproject.tab;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import team4.afinal.aclass.team4finalproject.Activity1;
import team4.afinal.aclass.team4finalproject.Activity16;
import team4.afinal.aclass.team4finalproject.Activity3;
import team4.afinal.aclass.team4finalproject.R;
import team4.afinal.aclass.team4finalproject.adapter.PagerAdapter;
import team4.afinal.aclass.team4finalproject.bean.GiverBean;

public class Activity4_tabMain extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_4);

        mTabLayout = findViewById(R.id.tabLayout);
        mPager = findViewById(R.id.pager);

		//탭 추가
        mTabLayout.addTab( mTabLayout.newTab().setText("재능을 나눠요") );
        mTabLayout.addTab( mTabLayout.newTab().setText("재능을 원해요") );

        // 탭 가로 사이즈 지정
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);

        // 어댑터
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(),
                mTabLayout.getTabCount());
        mPager.setAdapter(adapter);

        // TabLayout 과 ViewPager를 연결
        // ViewPager가 움직일 때 탭이 바뀌게끔
        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        // TabLayout이 움직일 때 ViewPager가 움직이도록
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //현재 사용자가 클릭한 탭의 이벤트가 실행된다.
                mPager.setCurrentItem( tab.getPosition() );
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // 로고 클릭 시 메인화면 이동
        Button btnGoMain = findViewById(R.id.btnGoMain);
        btnGoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Activity4_tabMain.this, Activity3.class);
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
                Intent i = new Intent(Activity4_tabMain.this, Activity16.class);
                startActivity(i);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
            }
        });

    }// end onCreate();


}
