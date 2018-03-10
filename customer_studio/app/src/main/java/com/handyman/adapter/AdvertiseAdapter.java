package com.handyman.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
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
import com.handyman.model.AdvertiseListModel;
import com.handyman.model.DataModel;
import com.handyman.model.StateModel;
import com.handyman.service.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class AdvertiseAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater infalter;
    private ArrayList<AdvertiseListModel> mAdvertiseList;
    private int mDeviceWidth = 480;
    ProgressBar progressBar = null;
    private OnClickListener onClickListener;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public AdvertiseAdapter(Context mContext, ArrayList<AdvertiseListModel> advertiseListModels, OnClickListener onClickListener) {
        super();
        this.mContext = mContext;
        this.mAdvertiseList = advertiseListModels;
        this.onClickListener = onClickListener;

        WindowManager w = null;
        if (mContext != null) {
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
        return mAdvertiseList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAdvertiseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    View vi;

    @SuppressLint("ViewHolder")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        vi = convertView;
        if (convertView == null) {
            infalter = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        vi = infalter.inflate(R.layout.row_advertise, null);

        progressBar = (ProgressBar) vi.findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);

        final ImageView image = (ImageView) vi.findViewById(R.id.adv_img);
        image.setTag(R.id.progress, progressBar);

        if (mAdvertiseList.get(position).isSelection() == true || mAdvertiseList.get(position).getIs_read().equalsIgnoreCase("1")) {
//            ((TextView) vi.findViewById(R.id.adv_descr)).setTypeface(Utils.getTypeFace(mContext));
            ((TextView) vi.findViewById(R.id.adv_title)).setTypeface(Utils.getTypeFace(mContext));

        }

//        if(mAdvertiseList.get(position).getIs_read().equalsIgnoreCase("0")){
//            Utils.storeString(mSharedPreferences, Utils.NOTI_ADVERTISE, Utils.NOTI_ADVERTISE);
//        }

        if (Utils.validateString(mAdvertiseList.get(position).title)) {
            ((TextView) vi.findViewById(R.id.adv_title)).setText(mAdvertiseList.get(position).title);
        }

//        if (Utils.validateString(mAdvertiseList.get(position).description)) {
//            ((TextView) vi.findViewById(R.id.adv_descr)).setText(mAdvertiseList.get(position).description);
//        }

        if (Utils.validateString(mAdvertiseList.get(position).banner_path)) {
            Transformation transformation = new Transformation() {

                @Override
                public Bitmap transform(Bitmap source) {
                    int targetWidth = mDeviceWidth;

                    double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                    int targetHeight = (int) (targetWidth * aspectRatio);
                    if (targetHeight > targetWidth) {
                        targetHeight = targetWidth;
                    }
                    Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                    if (result != source) {
                        // Same bitmap is returned if sizes are the same
                        source.recycle();
                    }
                    return result;
                }

                @Override
                public String key() {
                    return "transformation" + " desiredWidth";
                }
            };

            Picasso.with(mContext)
                    .load(Utils.IMAGE_URL + mAdvertiseList.get(position).banner_path)
                    .error(R.drawable.placeholder)
                    .transform(transformation)
//                    .centerCrop()
                    .resize(mDeviceWidth, (int) (mDeviceWidth))
                    .into(image, new Callback() {

                        @Override
                        public void onSuccess() {
                         /*if (progressBar != null) {
							 progressBar.setVisibility(View.GONE);
						 }*/
                            ProgressBar progressBar = (ProgressBar) image.getTag(R.id.progress);
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
							progressBar.setVisibility(View.VISIBLE);

                        }
                    });
        } else {
            image.setBackgroundResource(R.drawable.handyman_mascot);
            progressBar.setVisibility(View.GONE);
        }

//			boolean hasDrawable = (image.getDrawable() != null);
//			if(hasDrawable) {
//			    // imageView has image in it
//				progressBar.setVisibility(View.VISIBLE);
//			}
//			else {
//			    // no image assigned to image view
//				progressBar.setVisibility(View.GONE);
//			}


        vi.setTag(position);
        vi.setOnClickListener(onClickListener);


        return vi;
    }

    public void setList(ArrayList<AdvertiseListModel> mTempAdvertiselist) {
        // TODO Auto-generated method stub

    }

}
