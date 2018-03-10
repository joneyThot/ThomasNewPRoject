package com.android.handyman.adapter;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.handyman.R;
import com.android.handyman.model.FavouriteHandymanModel;
import com.android.handyman.model.StateModel;
import com.android.handyman.service.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class FavouriteAdapter extends BaseAdapter{

	private Context mContext;
	private LayoutInflater infalter;
	private ArrayList<FavouriteHandymanModel> mFavouriteHandymanModel;
	private OnClickListener mListClickListener;
	private int mDeviceWidth = 480;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2) public FavouriteAdapter(Context mContext, ArrayList<FavouriteHandymanModel> mFavouriteHandymanModel, OnClickListener mListClickListener) {
		super();
		this.mContext = mContext;
		this.mFavouriteHandymanModel = mFavouriteHandymanModel;
		this.mListClickListener = mListClickListener;
		
		WindowManager w = null;
		if(mContext != null){
			 w = ((Activity) mContext).getWindowManager();
			 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
					Point size = new Point();
					w.getDefaultDisplay().getSize(size);
					mDeviceWidth = size.x;
				} else {
					Display d = w.getDefaultDisplay();
					mDeviceWidth = d.getWidth();
				}
		}

	}

	@Override
	public int getCount() {
		return mFavouriteHandymanModel.size();
	}
	
	@Override
	public Object getItem(int position) {
		return mFavouriteHandymanModel.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	View vi ;
	@SuppressLint("ViewHolder") @Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		vi = convertView;
		if(convertView == null){
			infalter = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		vi = infalter.inflate(R.layout.row_favourite, null);
		
		ImageView mFavImg = (ImageView) vi.findViewById(R.id.fav_img);
		
		if(Utils.validateString(mFavouriteHandymanModel.get(position).img_path)) {
			Transformation transformation = new Transformation() {

				@Override public Bitmap transform(Bitmap source) {
					int targetWidth = mDeviceWidth;

					double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
					int targetHeight = (int) (targetWidth * aspectRatio);
					if(targetHeight > targetWidth) {
						targetHeight = targetWidth;
					}
					Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
					if (result != source) {
						// Same bitmap is returned if sizes are the same
						source.recycle();
					}
					return result;
				}

				@Override public String key() {
					return "transformation" + " desiredWidth";
				}
			};

			Picasso.with(mContext)
			.load(Utils.IMAGE_URL + mFavouriteHandymanModel.get(position).img_path)
			.placeholder(R.drawable.avtar_fav_img)
			.error(R.drawable.avtar_fav_img)
			.transform(transformation)
			.centerCrop()
			.resize(mDeviceWidth, (int) (mDeviceWidth))
			.into(mFavImg);
		}
		
		if(Utils.validateString(mFavouriteHandymanModel.get(position).firstname)){
			((TextView) vi.findViewById(R.id.favourite_handyman_text)).setText(mFavouriteHandymanModel.get(position).firstname + " " + mFavouriteHandymanModel.get(position).lastname);
		}
		
		if(Utils.validateString(mFavouriteHandymanModel.get(position).category_name)){
			((TextView) vi.findViewById(R.id.favourite_handyman_category_text)).setText(mFavouriteHandymanModel.get(position).category_name);
		}

		vi.setTag(position);
		vi.setOnClickListener(mListClickListener);
		return vi; 
	}
}
