package team4.afinal.aclass.team4finalproject.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import team4.afinal.aclass.team4finalproject.tab.Tab1Fragment;
import team4.afinal.aclass.team4finalproject.tab.Tab2Fragment;

public class PagerAdapter extends FragmentStatePagerAdapter {

    // 탭 개수 변수
    private int mNumOfTab;

    // 생성자
    public PagerAdapter(FragmentManager fm, int numOfTab) {
        super(fm);
        mNumOfTab = numOfTab;
    }

    @Override
    public Fragment getItem(int position) {
        // position 값 = 선택된 tab의 index

        switch (position) {
            case 0:
                Tab1Fragment tab1 = new Tab1Fragment();
                return tab1;
            case 1:
                Tab2Fragment tab2 = new Tab2Fragment();
                return tab2;
        }
        return null;
    }

    @Override
    public int getCount() {
        return mNumOfTab;
    }
}
