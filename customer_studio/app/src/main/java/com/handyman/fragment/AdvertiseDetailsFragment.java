package com.handyman.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.handyman.MainActivity;
import com.handyman.R;
import com.handyman.logger.Logger;
import com.handyman.model.AdvertiseListModel;
import com.handyman.service.AsyncCallListener;
import com.handyman.service.SelectAdvertiseRequestTask;
import com.handyman.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.squareup.picasso.Picasso;

public class AdvertiseDetailsFragment extends BaseFragment implements OnClickListener {

    private static String TAG = "AdvertiseDetailsFragment";

    private SharedPreferences mSharedPreferences;
    AdvertiseListModel mAdvertiseListModel = new AdvertiseListModel();
//	ArrayList<AdvertiseListModel> advertiseListModels = new ArrayList<AdvertiseListModel>();

    Fragment fr;
    View mRootView;
    private int mDeviceWidth = 480;
    ImageView imageView;
    TextView advr_details_more_desc;

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_advertise_details, container, false);


        WindowManager w = ((Activity) getActivity()).getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            w.getDefaultDisplay().getSize(size);
            mDeviceWidth = size.x;
        } else {
            Display d = w.getDefaultDisplay();
            mDeviceWidth = d.getWidth();
        }

        initview();
        return mRootView;
    }

    private void initview() {
        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);
        ((MainActivity) getActivity()).setTitleText("", "What's New Details","", View.VISIBLE, View.GONE, View.GONE, View.GONE,View.GONE);
        getActivity().findViewById(R.id.title).setVisibility(View.GONE);
        getActivity().findViewById(R.id.txtUrl).setVisibility(View.GONE);
        getActivity().findViewById(R.id.menuBtn).setOnClickListener(this);

        mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
        mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        mRootView.findViewById(R.id.titlebar).setVisibility(View.GONE);
        imageView = (ImageView) mRootView.findViewById(R.id.adve_details_img);
        advr_details_more_desc = (TextView) mRootView.findViewById(R.id.advr_details_more_desc);

        if (getArguments() != null) {
            mAdvertiseListModel = (AdvertiseListModel) getArguments().get(Utils.HANDYMAN_ADVERTISE);
        }

//		Utils.storeString(mSharedPreferences, Utils.NOTI_ADVERTISE, "");

        if (mAdvertiseListModel != null) {

            if (Utils.validateString(mAdvertiseListModel.banner_path)) {
                imageView.setVisibility(View.VISIBLE);
                Picasso.with(getActivity()).load(Utils.IMAGE_URL + mAdvertiseListModel.banner_path).resize(mDeviceWidth, (int) (mDeviceWidth))./*centerCrop().*/error(R.drawable.placeholder).into(imageView);
            } else {
                imageView.setVisibility(View.GONE);
            }

            if (Utils.validateString(mAdvertiseListModel.title)) {
                ((TextView) mRootView.findViewById(R.id.advr_details_more_title)).setText(mAdvertiseListModel.title);

            }
            if (Utils.validateString(mAdvertiseListModel.description)) {
//				advr_details_more_desc.setMovementMethod(LinkMovementMethod.getInstance());
//				advr_details_more_desc.setText(Html.fromHtml(mAdvertiseListModel.description));
                setTextViewHTML(advr_details_more_desc, mAdvertiseListModel.description);
//				advr_details_more_desc.setLinkTextColor(Color.parseColor("#0000ff"));
//				Linkify.addLinks(advr_details_more_desc, Linkify.ALL);

            }

            if (Utils.validateString(mAdvertiseListModel.is_read)) {
                if (mAdvertiseListModel.getIs_read().equalsIgnoreCase("0")) {
                    selectAdvertise(mSharedPreferences.getString(Utils.USER_ID, ""), mAdvertiseListModel.banner_id, "1");
                }
            }

			/*advertiseListModels.clear();
            advertiseListModels.add(mAdvertiseListModel);
			for (int i = 0; i < advertiseListModels.size(); i++) {
				if(Utils.validateString(advertiseListModels.get(i).getIs_read())) {
					Utils.storeString(mSharedPreferences, Utils.NOTI_ADVERTISE, "");
					if (advertiseListModels.get(i).getIs_read().equalsIgnoreCase("0")) {
						Utils.storeString(mSharedPreferences, Utils.NOTI_ADVERTISE, Utils.NOTI_ADVERTISE);
					}
				}
			}*/

        }

    }

    protected void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                // Do something with span.getURL() to handle the link click...
                Logger.d(TAG, "" + span.getURL());
                if(Utils.validateString(span.getURL())) {
                    OpenWebviewFragment openWebviewFragment = new OpenWebviewFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Utils.URL, span.getURL());
                    openWebviewFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, openWebviewFragment).addToBackStack(null).commit();
                } else {
                    Toast.makeText(getActivity(),"Your link is not valid.",Toast.LENGTH_SHORT).show();
                }
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    protected void setTextViewHTML(TextView text, String html) {
        CharSequence sequence = Html.fromHtml(html);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for (URLSpan span : urls) {
            makeLinkClickable(strBuilder, span);
        }
        text.setText(strBuilder);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
        }
    }

    private void selectAdvertise(String users_id, String banner_id, String is_read) {
        if (Utils.checkInternetConnection(getActivity())) {
            SelectAdvertiseRequestTask getAdvertiseRequestTask = new SelectAdvertiseRequestTask(getActivity());
            getAdvertiseRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    AdvertiseListModel advertiseModel = (AdvertiseListModel) response;

                    if (advertiseModel.success.equalsIgnoreCase("0")) {
                        Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), advertiseModel.message);
                    }
                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            getAdvertiseRequestTask.execute(users_id, banner_id, is_read);
        } else {
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert),
                    getResources().getString(R.string.connection));
        }
    }


}
