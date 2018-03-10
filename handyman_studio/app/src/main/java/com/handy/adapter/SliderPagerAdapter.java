package com.handy.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.handy.R;

/**
 * Created by User20 on 6/10/2015.
 */
public class SliderPagerAdapter extends PagerAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	LogoAdapter logoAdapter;

	public SliderPagerAdapter(Context context) {
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return 5;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((RelativeLayout) object);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((RelativeLayout) object);

	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View vi = mInflater.inflate(R.layout.row_advert, container, false);
		
		switch (position) {
			
		case 0:
			((ImageView)vi.findViewById(R.id.image)).setImageResource(R.drawable.hm_slider_1);
			((ImageView)vi.findViewById(R.id.image_new)).setVisibility(View.GONE);
			break;
			
		case 1:
			((ImageView)vi.findViewById(R.id.image)).setImageResource(R.drawable.hm_slider_2);
			((ImageView)vi.findViewById(R.id.image_new)).setVisibility(View.GONE);
			break;
			
		case 2:
			((ImageView)vi.findViewById(R.id.image)).setImageResource(R.drawable.hm_slider_3);
			((ImageView)vi.findViewById(R.id.image_new)).setVisibility(View.GONE);
			break;
			
		case 3:
			((ImageView)vi.findViewById(R.id.image)).setImageResource(R.drawable.hm_slider_4);
			((ImageView)vi.findViewById(R.id.image_new)).setVisibility(View.GONE);
			break;
			
		case 4:
			((ImageView)vi.findViewById(R.id.image)).setVisibility(View.GONE);
			((ImageView)vi.findViewById(R.id.image_new)).setVisibility(View.VISIBLE);
//			logoAdapter = new LogoAdapter(mContext);
//			logoAdapter.notifyDataSetChanged();
			
			break;

		}

		((ViewPager) container).addView(vi);

		return vi;
	}
}
