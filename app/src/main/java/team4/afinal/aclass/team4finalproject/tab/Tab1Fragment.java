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
import team4.afinal.aclass.team4finalproject.bean.GiverBean;
import team4.afinal.aclass.team4finalproject.R;
import team4.afinal.aclass.team4finalproject.adapter.GiverAdapter;


public class Tab1Fragment extends Fragment {

    private Button btnGiverWrite;

    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private FirebaseDatabase mDatabase;

    public ListView lstMain;
    private List<GiverBean> mGiverList = new ArrayList<GiverBean>();
    private GiverAdapter mGiverAdapter;
    DatabaseReference giver;

    private CheckBox chkIT, chkLang, chkArt, chkLiving, chkJob, chkEtc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab1, null);

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        lstMain=view.findViewById(R.id.lstMain);

        //체크박스 값 받아오기
        chkIT = view.findViewById(R.id.chkIT);
        chkLang = view.findViewById(R.id.chkLang);
        chkArt = view.findViewById(R.id.chkArt);
        chkLiving = view.findViewById(R.id.chkLiving);
        chkJob = view.findViewById(R.id.chkJob);
        chkEtc = view.findViewById(R.id.chkEtc);

        /*btnGiverWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), Activity7.class);
                startActivity(i);
            }
        });*/

        view.findViewById(R.id.btnGiverWrite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), Activity7.class);
                startActivity(i);
            }
        });

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
        //체크박스 다 선택했거나, 다 선택하지 않았을 시
        if((chkIT.isChecked() && chkLang.isChecked() && chkLiving.isChecked() && chkEtc.isChecked() && chkJob.isChecked() && chkArt.isChecked())||
                (chkIT.isChecked()==false && chkLiving.isChecked()==false && chkLang.isChecked()==false && chkArt.isChecked()==false
                        && chkEtc.isChecked()==false && chkJob.isChecked()==false)) {
            giver = mDatabase.getReference().child("giver");
            if (giver != null) {
                giver.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //실시간으로 서버에서 변경된 내용이 있을경우 호출된다.
                        //기존 리스트가 있다면 날려버린다.
                        mGiverList.clear();

                        //리스트를 서버로부터 온 데이터로 새로 만든다.
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                GiverBean bean = snapshot2.getValue(GiverBean.class);
                                mGiverList.add(bean);
                            }
                        }
                        //어댑터를 변경하는 메소드를 호출한다.
                        mGiverAdapter = new GiverAdapter(getActivity(), mGiverList);
                        lstMain.setAdapter(mGiverAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }//end if
    }//end onResume

    void check(boolean bIt, boolean bLang, boolean bArt, boolean bLiving, boolean bJob, boolean bEtc) {
        List<GiverBean> newList = new ArrayList<>();

        if(!bIt && !bLang && !bArt && !bLiving && !bJob && !bEtc) {
            newList.addAll(mGiverList);
        }

        for(GiverBean bean : mGiverList) {
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
        mGiverAdapter = new GiverAdapter(getActivity(), newList);
        lstMain.setAdapter(mGiverAdapter);
    }//end check
}
