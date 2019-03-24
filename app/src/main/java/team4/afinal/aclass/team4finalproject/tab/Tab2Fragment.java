package team4.afinal.aclass.team4finalproject.tab;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import team4.afinal.aclass.team4finalproject.Activity7;
import team4.afinal.aclass.team4finalproject.Activity8;
import team4.afinal.aclass.team4finalproject.R;
import team4.afinal.aclass.team4finalproject.adapter.GiverAdapter;
import team4.afinal.aclass.team4finalproject.adapter.ReceiverAdapter;
import team4.afinal.aclass.team4finalproject.bean.GiverBean;
import team4.afinal.aclass.team4finalproject.bean.ReceiverBean;


public class Tab2Fragment extends Fragment {

    //private Button btnReceiverWrite;

    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private FirebaseDatabase mDatabase;

    public ListView lstMain;
    private List<ReceiverBean> mReceiverList = new ArrayList<ReceiverBean>();
    private ReceiverAdapter mReceiverAdapter;

    private CheckBox chkIT, chkLang, chkArt, chkLiving, chkJob, chkEtc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab2, null);

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        //btnReceiverWrite=view.findViewById(R.id.btnReceiverWrite);
        lstMain=view.findViewById(R.id.lstMain);

        //체크박스 값 받아오기
        chkIT = view.findViewById(R.id.chkIT);
        chkLang = view.findViewById(R.id.chkLang);
        chkArt = view.findViewById(R.id.chkArt);
        chkLiving = view.findViewById(R.id.chkLiving);
        chkJob = view.findViewById(R.id.chkJob);
        chkEtc = view.findViewById(R.id.chkEtc);

        view.findViewById(R.id.btnReceiverWrite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), Activity8.class);
                startActivity(i);
            }
        });

        /*btnReceiverWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), Activity8.class);
                startActivity(i);
            }
        });
*/
        chkIT.setOnClickListener(checkBoxClick);

        chkLiving.setOnClickListener(checkBoxClick);

        chkLang.setOnClickListener(checkBoxClick);

        chkArt.setOnClickListener(checkBoxClick);

        chkJob.setOnClickListener(checkBoxClick);

        chkEtc.setOnClickListener(checkBoxClick);


        return view;
    } // end onCreateView

    private View.OnClickListener checkBoxClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            check(chkIT.isChecked(), chkLang.isChecked(), chkArt.isChecked(), chkLiving.isChecked(), chkJob.isChecked(), chkEtc.isChecked());
        }
    };

   @Override
   public void onResume() {
        super.onResume();

        DatabaseReference receiver = mDatabase.getReference().child("receiver");
        if (receiver != null ) {
            receiver.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //실시간으로 서버에서 변경된 내용이 있을경우 호출된다.

                    //기존에 리스트는 날려버린다.
                    mReceiverList.clear();

                    //리스트를 서버로부터 온 데이터로 새로 만든다.
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        for(DataSnapshot snapshot2 : snapshot.getChildren()) {
                                ReceiverBean bean = snapshot2.getValue(ReceiverBean.class);
                                mReceiverList.add(bean);
                        }
                    }

                    //어댑터를 변경하는 메소드를 호출한다.
                    mReceiverAdapter = new ReceiverAdapter(getActivity(), mReceiverList);
                    lstMain.setAdapter(mReceiverAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    void check(boolean bIt, boolean bLang, boolean bArt, boolean bLiving, boolean bJob, boolean bEtc) {
        List<ReceiverBean> newList = new ArrayList<>();

        if(!bIt && !bLang && !bArt && !bLiving && !bJob && !bEtc) {
            newList.addAll(mReceiverList);
        }

        for(ReceiverBean bean : mReceiverList) {
            if(bIt) {
                if(bean.field.equals("IT")) {
                    newList.add(bean);
                }
            }
            if(bLang) {
                if(bean.field.equals("언어")) {
                    newList.add(bean);
                }
            }
            if(bArt) {
                if(bean.field.equals("예체능")) {
                    newList.add(bean);
                }
            }
            if(bLiving) {
                if(bean.field.equals("실생활")) {
                    newList.add(bean);
                }
            }
            if(bJob) {
                if(bean.field.equals("취업")) {
                    newList.add(bean);
                }
            }
            if(bEtc) {
                if(bean.field.equals("기타")) {
                    newList.add(bean);
                }
            }
        }//end for

        //어댑터를 변경하는 메소드를 호출한다.
        mReceiverAdapter = new ReceiverAdapter(getActivity(), newList);
        lstMain.setAdapter(mReceiverAdapter);
    }//end check

}