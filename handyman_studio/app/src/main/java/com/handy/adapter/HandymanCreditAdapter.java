package com.handy.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.handy.R;
import com.handy.model.HandymanCreditsModel;
import com.handy.model.MyCollectionModel;
import com.handy.service.Utils;

import java.util.ArrayList;

public class HandymanCreditAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater infalter;
    private ArrayList<HandymanCreditsModel> mHandymanCreditsList;

    public HandymanCreditAdapter(Context mContext, ArrayList<HandymanCreditsModel> mHandymanCreditsList) {
        super();
        this.mContext = mContext;
        this.mHandymanCreditsList = mHandymanCreditsList;

    }

    @Override
    public int getCount() {
        return mHandymanCreditsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mHandymanCreditsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    View vi;

    @SuppressLint({"ViewHolder"})
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        vi = convertView;
        if (convertView == null) {
            infalter = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        vi = infalter.inflate(R.layout.row_handyman_credit_status, null);

        if(Utils.validateString(mHandymanCreditsList.get(position).credit_use_for_convert)){
            ((TextView) vi.findViewById(R.id.txtCreditConvert)).setText(mHandymanCreditsList.get(position).credit_use_for_convert);
        }

//        if(Utils.validateString(mHandymanCreditsList.get(position).id)){
//            ((TextView) vi.findViewById(R.id.txtCreditId)).setText(mHandymanCreditsList.get(position).id);
//        }

        if(Utils.validateString(mHandymanCreditsList.get(position).created_date)){
            ((TextView) vi.findViewById(R.id.txtCreditDate)).setText(mHandymanCreditsList.get(position).created_date);
        }

        if(Utils.validateString(mHandymanCreditsList.get(position).total_cash)){
            ((TextView) vi.findViewById(R.id.txtCreditAmount)).setText(mHandymanCreditsList.get(position).total_cash + "/-");
        }

        return vi;
    }


    public void setList(ArrayList<HandymanCreditsModel> mTempHiringslist) {

    }

}