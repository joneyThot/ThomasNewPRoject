package com.handyman.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.handyman.LocationUpdaterService;
import com.handyman.LoginActivity;
import com.handyman.MainActivity;
import com.handyman.R;
import com.handyman.logger.Logger;
import com.handyman.model.RegisterModel;
import com.handyman.service.AsyncCallListener;
import com.handyman.service.LogoutRequestTask;
import com.handyman.service.Utils;
import com.handyman.view.TextViewPlus;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class SlidingMenuFragment extends BaseFragment implements
        OnClickListener {

    private static String TAG = "SlidingMenuFragment";
    public static View mRoot;
    private static LinearLayout mMenuLayout;
    public static ScrollView sc;
    public static int pos_x = 0, pos_y = 0;
    private int mDeviceWidth = 480;

    Fragment fr = null;
    TextViewPlus titleText;
    ImageView mUserImg;
    boolean locpressed = false;
    SharedPreferences sharedPreferences;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @SuppressLint("ResourceAsColor")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.list, null);
        sharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);

        mMenuLayout = (LinearLayout) mRoot.findViewById(R.id.menuLayout);
        getActivity().findViewById(R.id.title).setVisibility(View.VISIBLE);
        titleText = (TextViewPlus) getActivity().findViewById(R.id.title);
        mUserImg = (ImageView) mRoot.findViewById(R.id.user_img);
        mUserImg.setOnClickListener(this);

        sc = (ScrollView) mRoot.findViewById(R.id.scrollLayout);

        sc.post(new Runnable() {
            @Override
            public void run() {
                pos_x = sc.getScrollX();
                pos_y = sc.getScrollY();
            }
        });

        for (int i = 1; i < mMenuLayout.getChildCount(); i++) {
//			if (i != 14) {
            mMenuLayout.getChildAt(i).setOnClickListener(this);
//			}
        }

        selectMenu(1);
        setColorNoti(mActivity);
        setImage(mActivity);

        return mRoot;
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @SuppressLint("ResourceAsColor")
    public void setImage(Context context) {
        Logger.e(TAG, "setImage");
        sharedPreferences = context.getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);

        WindowManager w = ((Activity) context).getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            w.getDefaultDisplay().getSize(size);
            mDeviceWidth = size.x;
        } else {
            Display d = w.getDefaultDisplay();
            mDeviceWidth = d.getWidth();
        }

        mUserImg = (ImageView) mRoot.findViewById(R.id.user_img);

        if (!sharedPreferences.getString(Utils.IMAGEPATH, "").isEmpty()) {

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


            Picasso.with(context)
                    .load(Utils.IMAGE_URL + sharedPreferences.getString(Utils.IMAGEPATH, ""))
                    .placeholder(R.drawable.splash_logo_xxxhdpi)
                    .error(R.drawable.splash_logo_xxxhdpi)
                    .transform(transformation)
                    .centerCrop()
                    .resize(mDeviceWidth, (int) (mDeviceWidth))
                    .into(mUserImg);
        }
    }

    public void setColorNoti(Context context) {
        Logger.e(TAG, "setColorNoti");
        sharedPreferences = context.getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);

        if ((!sharedPreferences.getString(Utils.NOTI_ADVER, "").isEmpty())) {
            ((TextView) mRoot.findViewById(R.id.txtNoti)).setVisibility(View.VISIBLE);
        } else {
            ((TextView) mRoot.findViewById(R.id.txtNoti)).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        sc.post(new Runnable() {
            @Override
            public void run() {
                pos_x = sc.getScrollX();
                pos_y = sc.getScrollY();
                // sc.scrollTo(pos_x, pos_y);
            }
        });

        switch (view.getId()) {

            case R.id.user_img:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MyProfileFragment()).commit();
                selectMenu(3);
                MainActivity.doubleBackToExitPressedOnce = false;
                break;

            case R.id.menu_homebtn_layout:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ServiceAtHomeFragment()).commit();
                MainActivity.doubleBackToExitPressedOnce = true;
                selectMenu(1);
                break;

	/*	case R.id.menu_find_near_by_handyman_layout:
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new FindNearByHandymanFragment()).commit();
			break;*/

            case R.id.menu_my_hirings_layout:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MyHiringsFragment()).commit();
                selectMenu(2);
                MainActivity.doubleBackToExitPressedOnce = false;
                break;

            case R.id.menu_my_profile_layout:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MyProfileFragment()).commit();
                MainActivity.doubleBackToExitPressedOnce = false;
                selectMenu(3);
                break;

            case R.id.menu_my_cradits_layout:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MyCreditsFragment()).commit();
                MainActivity.doubleBackToExitPressedOnce = false;
                selectMenu(4);
                break;

            case R.id.menu_favourite_handyman_layout:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new FavouriteHandymanFragment()).commit();
                MainActivity.doubleBackToExitPressedOnce = false;
                selectMenu(5);
                break;

		/*case R.id.menu_settings_layout:
			titleText.setText(R.string.menu_settings);
			// titleText.setText(R.string.title_editshoppinglisttext);
			// ((DashboardLoginActivity)
			// getActivity()).pushFragments(AppConstants.TAB_SHOPPING, new
			// ShoppingListEditFragment(), false, true);

			break;*/

            case R.id.menu_feedback_suggestions_layout:
//			Toast.makeText(getActivity(), "When store app on play store than give directly feedback same as other apps.",	Toast.LENGTH_LONG).show();
//			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.whatsapp&hl=en")));
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new FeedbackFragment()).commit();
                MainActivity.doubleBackToExitPressedOnce = false;
                selectMenu(6);
                break;

            case R.id.menu_advertise_layout:

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new AdvertiseFragment()).commit();
                MainActivity.doubleBackToExitPressedOnce = false;
                selectMenu(7);
                break;


            case R.id.menu_logout_layout:

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Logout");
                builder.setCancelable(false);
                builder.setMessage("Are you sure for logout.");
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                onLogout(sharedPreferences.getString(Utils.USER_ID, ""), "0");
                            }
                        });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }

                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();


                break;

            case R.id.view_layout:
                break;

            case R.id.menu_about_service_at_home_layout:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new AboutServiceFragment()).commit();
                MainActivity.doubleBackToExitPressedOnce = false;
                selectMenu(10);
                break;


            case R.id.menu_contact_support_layout:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ContactSupportFragment()).commit();
                MainActivity.doubleBackToExitPressedOnce = false;
                selectMenu(11);
                break;

            case R.id.menu_privacy_policy_layout:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new PrivacyPolicyFragment()).commit();
                MainActivity.doubleBackToExitPressedOnce = false;
                selectMenu(12);
                break;


            case R.id.menu_terms_and_conditions_layout:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new TermsAndConditionFragment()).commit();
                MainActivity.doubleBackToExitPressedOnce = false;
                selectMenu(13);
                break;

            default:
                break;

        }
        ((MainActivity) getActivity()).getSlidingMenu().toggle();

    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.e("", "onResume menu");
    }

    public static void viewSelectedPosition() {
        sc.post(new Runnable() {
            public void run() {
                // pos_x = sc.getScrollX();
                // pos_y = sc.getScrollY();
                sc.scrollTo(pos_x, pos_y);
            }
        });
    }

    public static void selectMenu(int pos) {
        for (int i = 1; i < mMenuLayout.getChildCount(); i++) {
            if (LinearLayout.class.isInstance(mMenuLayout.getChildAt(i))) {
                LinearLayout layout = (LinearLayout) mMenuLayout.getChildAt(i);
                if (i == pos) {
                    layout.setBackgroundResource(R.color.light_red);
                } else {
                    layout.setBackgroundResource(android.R.color.transparent);
                }
            }

        }
    }

    private void onLogout(String id, String is_login) {

        if (Utils.checkInternetConnection(getActivity())) {
            LogoutRequestTask logoutRequestTask = new LogoutRequestTask(getActivity());
            logoutRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    RegisterModel registerModel = (RegisterModel) response;
                    if (registerModel.success.equalsIgnoreCase("1")) {
                        Toast.makeText(getActivity(), registerModel.message, Toast.LENGTH_SHORT).show();

                        Utils.storeString(sharedPreferences, Utils.LOGOUT_STATUS, "logout");

                        getActivity().stopService(new Intent(getActivity(), LocationUpdaterService.class));

                        Utils.storeString(sharedPreferences, Utils.MOBILE_NO, "");
                        Utils.storeString(sharedPreferences, Utils.PASSWORD, "");
                        Utils.storeString(sharedPreferences, Utils.USER_ID, "");
                        Utils.storeString(sharedPreferences, Utils.FIRSTNAME, "");
                        Utils.storeString(sharedPreferences, Utils.LASTNAME, "");
                        Utils.storeString(sharedPreferences, Utils.IMAGEPATH, "");
                        Utils.storeString(sharedPreferences, Utils.EMAIL, "");
                        Utils.storeString(sharedPreferences, Utils.USER_PROFILE, "");
                        Utils.storeString(sharedPreferences, Utils.NOTI_HIRE_STATUS, "");
                        Utils.storeString(sharedPreferences, Utils.NOTI_ADVERTISE, "");
//							Utils.storeString(sharedPreferences, Utils.DEVICE_ID, "");
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();

                    } else if (registerModel.success.equalsIgnoreCase("0")) {
                        Toast.makeText(getActivity(), registerModel.message, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            logoutRequestTask.execute(id, is_login);
        } else {
            Toast.makeText(getActivity(), getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }
    }
}
