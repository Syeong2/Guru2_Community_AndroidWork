package team4.afinal.aclass.team4finalproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import team4.afinal.aclass.team4finalproject.Activity1;
import team4.afinal.aclass.team4finalproject.Activity15;

import team4.afinal.aclass.team4finalproject.Activity15Modify;
import team4.afinal.aclass.team4finalproject.Activity7Modify;
import team4.afinal.aclass.team4finalproject.bean.ExchangeBean;
import team4.afinal.aclass.team4finalproject.R;

public class ExchangeAdapter extends BaseAdapter {

    private Context mContext;
    private List<ExchangeBean> mList;

    public ExchangeAdapter(Context context, List<ExchangeBean> list){
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.view_exchange, null);

        View layTopRow = convertView.findViewById(R.id.layTopRow);
        TextView txtTitle = convertView.findViewById(R.id.txtTitle);
        TextView txtNum = convertView.findViewById(R.id.txtNum);


        if(position == 0) {
            layTopRow.setVisibility(View.VISIBLE);
        } else {
            layTopRow.setVisibility(View.GONE);
        }

        final ExchangeBean bean = mList.get(position);

        txtTitle.setText(bean.title);
        txtNum.setText(bean.num);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, Activity15.class);
                i.putExtra(ExchangeBean.class.getName(), bean);

                mContext.startActivity(i);
            }
        });

        return convertView;
    }
}