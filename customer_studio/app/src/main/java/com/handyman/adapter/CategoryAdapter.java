package com.handyman.adapter;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handyman.R;
import com.handyman.model.CategoryListModel;
import com.handyman.model.StateModel;
import com.handyman.service.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class CategoryAdapter extends BaseAdapter{

	private Context mContext;
	private LayoutInflater infalter;
	private ArrayList<CategoryListModel> mCategoryList;
	private OnClickListener mListClickListener;
	private int mDeviceWidth = 480;
	ProgressBar progressBar = null;
//	public ImageLoader imageLoader; 

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2) public CategoryAdapter(Context mContext, ArrayList<CategoryListModel> mCategoryList, OnClickListener mListClickListener) {
		super();
		this.mContext = mContext;
		this.mCategoryList = mCategoryList;
		this.mListClickListener = mListClickListener;
//		imageLoader = new ImageLoader(mContext.getApplicationContext());
		
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
		return mCategoryList.size();
	}
	
	@Override
	public Object getItem(int position) {
		return mCategoryList.get(position);
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
		vi = infalter.inflate(R.layout.row_category, null);
		
		progressBar = (ProgressBar) vi.findViewById(R.id.progress);
		progressBar.setVisibility(View.VISIBLE);
		
		final ImageView image = (ImageView) vi.findViewById(R.id.category_img);
		image.setTag(R.id.progress,progressBar);
		if(Utils.validateString(mCategoryList.get(position).img_path)) {
			//DisplayImage function from ImageLoader Class
//	        imageLoader.DisplayImage(Utils.IMAGE_URL + mCategoryList.get(position).img_path, image);
//			Picasso.with(mContext).load(Utils.IMAGE_URL + mCategoryList.get(position).img_path).placeholder(R.drawable.progress_animation).error(R.drawable.placeholder).resize(mDeviceWidth, (int) (mDeviceWidth * 0.40)).centerCrop().into(((ImageView) vi.findViewById(R.id.category_img)));
			Picasso.with(mContext).load(Utils.IMAGE_URL + mCategoryList.get(position).img_path).resize(mDeviceWidth, (int) (mDeviceWidth * 0.32)).centerCrop().error(R.drawable.placeholder).into(image, new Callback() {
				
				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					 /*if (progressBar != null) {
						 progressBar.setVisibility(View.GONE);
					 }*/
					ProgressBar progressBar = (ProgressBar) image.getTag(R.id.progress);
					progressBar.setVisibility(View.GONE);
				}
				
				@Override
				public void onError() {
					// TODO Auto-generated method stub
					progressBar.setVisibility(View.VISIBLE);
				}
			});
//			new ImageLoadTask(Utils.IMAGE_URL + mCategoryList.get(position).img_path, image).execute();
			/*Transformation transformation = new Transformation() {

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
		.load(Utils.IMAGE_URL + mCategoryList.get(position).img_path)
		.resize(mDeviceWidth, mDeviceWidth*2/3)
		.transform(transformation)
		.into((ImageView) vi.findViewById(R.id.category_img));*/
		}

		if(Utils.validateString(mCategoryList.get(position).name)){
			((TextView) vi.findViewById(R.id.category_name)).setText(mCategoryList.get(position).name);
		}

		vi.setTag(position);
		vi.setOnClickListener(mListClickListener);
		return vi; 
	}
	
}
