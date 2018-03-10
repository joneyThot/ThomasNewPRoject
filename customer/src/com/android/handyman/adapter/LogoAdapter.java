package com.android.handyman.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.android.handyman.R;

public class LogoAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater infalter;

	public LogoAdapter(Context mContext) {
		super();
		this.mContext = mContext;

	}

	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	View vi;

	@SuppressLint("ViewHolder") @Override
	public View getView(int position, View convertView, ViewGroup parent) {

		vi = convertView;
		if (convertView == null) {
			infalter = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		vi = infalter.inflate(R.layout.row_logo, null);
		
		Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.alpha);
		anim.reset();
		LinearLayout l = (LinearLayout) vi.findViewById(R.id.splash_layout);
		l.clearAnimation();
		l.startAnimation(anim);

		anim = AnimationUtils.loadAnimation(mContext, R.anim.translate_splash);
		anim.reset();
		LinearLayout layout = (LinearLayout) vi.findViewById(R.id.animation_layout);
		layout.clearAnimation();
		layout.startAnimation(anim);

		return vi;
	}


}
