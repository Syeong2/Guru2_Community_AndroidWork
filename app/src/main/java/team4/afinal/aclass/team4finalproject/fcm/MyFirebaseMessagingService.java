package team4.afinal.aclass.team4finalproject.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;

import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import team4.afinal.aclass.team4finalproject.Activity15;
import team4.afinal.aclass.team4finalproject.Activity16;
import team4.afinal.aclass.team4finalproject.R;
import team4.afinal.aclass.team4finalproject.adapter.ExchangeAdapter;
import team4.afinal.aclass.team4finalproject.bean.ExchangeBean;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String API_KEY = "AAAAVU0Qu1s:APA91bFqxiEgcOnEYOhkVfwhZvnVBmyK6E0YdTy32memGICXEBGB5zBRGBCEli9Q1qYPy5rq0B7ArRGcPaYqeE8oJFhYoTAa2KgAszjLUZ77CGYQAmy7wZPV3KVHL3-Kgq6Jm5HDa03-";
    Intent notiIntent;
    ExchangeBean exchangeBean = null;
    private FirebaseDatabase mDatabase;

    @Override
    public void onNewToken(String s) {
        Log.e("SWU", s);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String message =data.get("content");
        String notiType = data.get("notiType");
        String cate = data.get("cate");
        String exchId = data.get("exchId");

        mDatabase = FirebaseDatabase.getInstance();

        if(cate!=null && exchId!=null) {
            DatabaseReference exchange = mDatabase.getReference().child("exchange").child(cate).child(exchId);
            if (exchange != null) {
                exchange.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        exchangeBean = dataSnapshot.getValue(ExchangeBean.class);

                        notiAlarm(title, message, notiType);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            } else {
            notiAlarm(title, message, notiType);
        }


    }

    public void notiAlarm(String title, String message, String notiType){        //오레오부터 바뀐 Noti 적용
        String channelId = "channel";
        String channelName = "Channel Name";

        NotificationManager notiMng =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        //오레오 버젼부터 변경된 코드 적용
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel =
                    new NotificationChannel(channelId, channelName, importance);
            notiMng.createNotificationChannel(mChannel);
        }

        //다이얼로그 생성하듯이 빌더를 통해서 노티를 생성한다.
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), channelId);

        int requestID = (int)System.currentTimeMillis();
        //노티 클릭시 이동하고 싶은 Activity 지정
        if(notiType.equals("A")) {
            notiIntent = new Intent(getApplicationContext(), Activity16.class);
        }else {
            notiIntent = new Intent(getApplicationContext(), Activity15.class);
            if(exchangeBean != null) {
                notiIntent.putExtra(ExchangeBean.class.getName(), exchangeBean);
            }
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                requestID,
                notiIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );

        //기본빌더 설정
        builder.setContentTitle(title)
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_ALL) //알림 + 사운드진동 설정
                .setAutoCancel(true) //알림 터치후 노티삭제됨
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(android.R.drawable.btn_star)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.main_logo_no_round))
                .setContentIntent(pendingIntent);

        //최종적으로 노티를 실행하는 한다.
        Notification noti = builder.build();
        noti.number = 10; //미확인 노티갯수를 10개로 지정한다.
        // 10개이상 쌓이면
        // + 로 표시된다.

        notiMng.notify(requestID, noti);
    }
}
