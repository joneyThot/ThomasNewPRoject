package com.handyman.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.handyman.MainActivity;
import com.handyman.R;
import com.handyman.SplashActivity;
import com.handyman.adapter.CityAdapter;
import com.handyman.adapter.StateAdapter;
import com.handyman.crop.CropImageIntentBuilder;
import com.handyman.logger.Logger;
import com.handyman.model.CityModel;
import com.handyman.model.MyProfileModel;
import com.handyman.model.RegisterModel;
import com.handyman.model.StateModel;
import com.handyman.service.AsyncCallListener;
import com.handyman.service.EditProfileRequestTask;
import com.handyman.service.GetCityTask;
import com.handyman.service.GetStateTask;
import com.handyman.service.Utils;
import com.handyman.view.TextViewPlus;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.NoSuchElementException;

public class EditProfileFragment extends BaseFragment implements OnClickListener {

    private static String TAG = "EditProfileFragment";
    private static final int REQUEST_IMAGE_FROM_GALLERY = 101;
    private static int REQUEST_PICTURE = 1;
    private static int REQUEST_CROP_PICTURE = 2;

    private ArrayList<StateModel> mStateModelList = new ArrayList<StateModel>();
    private ArrayList<CityModel> mCityModelList = new ArrayList<CityModel>();
    private int mDeviceWidth = 480;

    private SharedPreferences mSharedPreferences;

    //	MyProfileModel myProfileModel = new MyProfileModel();
    ArrayList<MyProfileModel> myProfileModelList = new ArrayList<MyProfileModel>();
    StateAdapter stateAdapter;
    CityAdapter cityAdapter;
    Fragment fr;
    View mRootView;

    static EditText mBirthDate;
    EditText mFirstName, mLastName, mEmailId, mWhatsappID, mQualifiaction, mExperience, mProvider, mAddress, mStreet, mLandmark, mPincode;
    LinearLayout mImageLayout;
    TextViewPlus mProfilePic;
    ImageView mMaleImg, mFemaleImg, mProfileImg;
    Spinner mStateSpinner, mCitySpinner;
    TextViewPlus mUpdateProfile;

    String selectedImagePath, base64String = "", mMaleFemale, mCurrentPhotoPath = "";
    String fname = "", lname = "", bdate = "", emailid = "", whatsupid = "", qualification = "", experience = "", address = "", street = "", landmark = "",
            provider = "", website = "", state = "", city = "", state_id = "", city_id = "", pincode = "", img = "", img_path = "";

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.activity_register_edit, container, false);

        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP) {
            // Do something for lollipop and above versions
//			if (!Utils.denyFlag) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if ((getActivity().checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                        (getActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                        (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
            }
//			}
        }

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

    @SuppressWarnings("unchecked")
    private void initview() {
        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);
        ((MainActivity) getActivity()).setTitleText("", getString(R.string.edit_profile), "", View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE);
        getActivity().findViewById(R.id.title).setVisibility(View.GONE);
        getActivity().findViewById(R.id.txtUrl).setVisibility(View.GONE);

        mRootView.findViewById(R.id.titleLayout).setVisibility(View.GONE);

        mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
        mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        mRootView.findViewById(R.id.register_mobile_edittxt).setVisibility(View.GONE);
        mRootView.findViewById(R.id.register_confirm_password).setVisibility(View.GONE);
        mFirstName = (EditText) mRootView.findViewById(R.id.register_firstname_edittxt);
        mLastName = (EditText) mRootView.findViewById(R.id.register_lastname_edittxt);
        mBirthDate = (EditText) mRootView.findViewById(R.id.register_birthdate);
        mBirthDate.setOnClickListener(this);
        mEmailId = (EditText) mRootView.findViewById(R.id.register_emailid);
        mWhatsappID = (EditText) mRootView.findViewById(R.id.register_whatsappid);
        mQualifiaction = (EditText) mRootView.findViewById(R.id.register_qualification);
        mExperience = (EditText) mRootView.findViewById(R.id.register_experience);
        mProvider = (EditText) mRootView.findViewById(R.id.register_provider);
        mRootView.findViewById(R.id.register_password).setVisibility(View.GONE);
        mAddress = (EditText) mRootView.findViewById(R.id.register_address);
        mStreet = (EditText) mRootView.findViewById(R.id.register_street);
        mLandmark = (EditText) mRootView.findViewById(R.id.register_landmark);
        mProfilePic = (TextViewPlus) mRootView.findViewById(R.id.profile_txt);
        mProfilePic.setVisibility(View.GONE);
        mStateSpinner = (Spinner) mRootView.findViewById(R.id.register_spinner_state);
        mCitySpinner = (Spinner) mRootView.findViewById(R.id.register_spinner_city);
        mPincode = (EditText) mRootView.findViewById(R.id.register_pincode);

        mMaleImg = (ImageView) mRootView.findViewById(R.id.male_img);
        mFemaleImg = (ImageView) mRootView.findViewById(R.id.female_img);
        mProfileImg = (ImageView) mRootView.findViewById(R.id.profile_image);

        mImageLayout = (LinearLayout) mRootView.findViewById(R.id.image_layout);
        mImageLayout.setVisibility(View.VISIBLE);

        mRootView.findViewById(R.id.chk_box_layout).setVisibility(View.GONE);

        mRootView.findViewById(R.id.male_layout).setOnClickListener(this);
        mRootView.findViewById(R.id.female_layout).setOnClickListener(this);

        mUpdateProfile = (TextViewPlus) mRootView.findViewById(R.id.register_signup_Button);
        mUpdateProfile.setText("UPDATE PROFILE");
        mUpdateProfile.setOnClickListener(this);

        mRootView.findViewById(R.id.browserbtn).setOnClickListener(this);

        mPincode.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_GO) {
                    mRootView.findViewById(R.id.register_signup_Button).performClick();
                }
                return false;
            }
        });

        getState();

        if (getArguments() != null) {
            myProfileModelList = (ArrayList<MyProfileModel>) getArguments().get(Utils.MY_PROFILE_LIST);
            fname = myProfileModelList.get(0).getFirstname();
            lname = myProfileModelList.get(0).getLastname();
            mMaleFemale = myProfileModelList.get(0).getGender();
            bdate = myProfileModelList.get(0).getDob();
            emailid = myProfileModelList.get(0).getEmail();
            whatsupid = myProfileModelList.get(0).getWhatsapp_id();
            qualification = myProfileModelList.get(0).getQualification();
            experience = myProfileModelList.get(0).getExperience();
            provider = myProfileModelList.get(0).getProvider();
            address = myProfileModelList.get(0).getAddress();
            street = myProfileModelList.get(0).getStreet();
            landmark = myProfileModelList.get(0).getLandmark();
            state_id = myProfileModelList.get(0).getState();
            state = myProfileModelList.get(0).getState_name();
            website = myProfileModelList.get(0).getWebsite();
            city_id = myProfileModelList.get(0).getCity();
            city = myProfileModelList.get(0).getCity_name();
            pincode = myProfileModelList.get(0).getPincode();
            img = myProfileModelList.get(0).getImg();
            img_path = Utils.IMAGE_URL + myProfileModelList.get(0).getImg_path();

            // set values
            mFirstName.setText(fname);
            mLastName.setText(lname);
            if (mMaleFemale.equalsIgnoreCase("male")) {
                mMaleImg.setImageResource(R.drawable.radio_checked);
                mFemaleImg.setImageResource(R.drawable.radio_normal);
            } else {
                mFemaleImg.setImageResource(R.drawable.radio_checked);
                mMaleImg.setImageResource(R.drawable.radio_normal);
            }
            mBirthDate.setText(bdate);
            mEmailId.setText(emailid);
            mWhatsappID.setText(whatsupid);
            mAddress.setText(address);
            mStreet.setText(street);
            mLandmark.setText(landmark);

            mStateSpinner.setSelection(Integer.parseInt(state_id));
            mStateSpinner.getSelectedItemPosition();

            mCitySpinner.setSelection(Integer.parseInt(city_id));
            mCitySpinner.getSelectedItemPosition();

            mPincode.setText(pincode);

            if (Utils.validateString(img_path)) {
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

                Picasso.with(getActivity())
                        .load(img_path)
                        .error(R.drawable.avtar_images)
                        .transform(transformation)
                        .centerCrop()
                        .resize(mDeviceWidth, (int) (mDeviceWidth))
                        .into(mProfileImg);
            }
        }

        mStateSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position > 0) {
                    getCity(((StateModel) mStateSpinner.getSelectedItem()).id);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.register_birthdate:
                DialogFragment newDateFragment = new SelectDateFragment();
                newDateFragment.show(getFragmentManager(), "Date");
                break;

            case R.id.register_signup_Button:
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                String b_date = mBirthDate.getText().toString();
                String bDate = b_date.substring(6, 10) + "-" + b_date.substring(3, 5) + "-" + b_date.substring(0, 2);

//			 if(mSharedPreferences.getString(Utils.USER_TYPE, "").equalsIgnoreCase("customer")){

                if (fieldValidation()) {
                    onEditProfile(mSharedPreferences.getString(Utils.USER_ID, ""), mFirstName.getText().toString(), mLastName.getText().toString(),
                            mMaleFemale, bDate, mEmailId.getText().toString(), mWhatsappID.getText().toString(), mAddress.getText().toString(),
                            mStreet.getText().toString(), mLandmark.getText().toString(), ((CityModel) mCitySpinner.getSelectedItem()).id, mPincode.getText().toString(),
                            ((StateModel) mStateSpinner.getSelectedItem()).id, String.valueOf(SplashActivity.latitude),
                            String.valueOf(SplashActivity.longitude), mCurrentPhotoPath);
                }
                break;

            case R.id.browserbtn:
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                } else {
                    Intent intent1 = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent1, REQUEST_PICTURE);
                }
                break;

            case R.id.male_layout:
                mMaleImg.setImageResource(R.drawable.radio_checked);
                mFemaleImg.setImageResource(R.drawable.radio_normal);
                mMaleFemale = "male";
                break;

            case R.id.female_layout:
                mFemaleImg.setImageResource(R.drawable.radio_checked);
                mMaleImg.setImageResource(R.drawable.radio_normal);
                mMaleFemale = "female";
        }
    }

    private void getState() {
        if (Utils.checkInternetConnection(getActivity())) {
            GetStateTask getStateTask = new GetStateTask(getActivity());
            getStateTask.setAsyncCallListener(new AsyncCallListener() {

                @SuppressWarnings("unchecked")
                @Override
                public void onResponseReceived(Object response) {
                    mStateModelList.add(new StateModel("", "State", "", "", "", "", "0"));
                    mStateModelList.addAll((ArrayList<StateModel>) response);
                    if (mStateModelList.size() > 0) {
                        stateAdapter = new StateAdapter(getActivity(), mStateModelList);
                        mStateSpinner.setAdapter(stateAdapter);

                        mCityModelList.clear();
                        mCityModelList.add(new CityModel("", "", "City", "", "", "", "", "0"));
                        cityAdapter = new CityAdapter(getActivity(), mCityModelList);
                        mCitySpinner.setAdapter(cityAdapter);

                        String count = "";
                        for (int i = 0; i < mStateModelList.size(); i++) {
//							CityModel cityModel = new CityModel();
                            if (state.equalsIgnoreCase(mStateModelList.get(i).name)) {
                                Logger.d("STATE", "" + state);
                                Logger.d("COUNT", "" + mStateModelList.get(i).count);
                                count = mStateModelList.get(i).count;
                            }
                        }
                        if (Utils.validateString(count)) {
                            mStateSpinner.setSelection(stateAdapter.getPosition(Integer.parseInt(count)));
                        } else {
                            mStateSpinner.setAdapter(stateAdapter);
                            stateAdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            getStateTask.execute();
        } else {
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert),
                    getResources().getString(R.string.connection));
        }
    }

    private void getCity(String state_id) {
        if (Utils.checkInternetConnection(getActivity())) {
            GetCityTask getCityTask = new GetCityTask(getActivity());
            getCityTask.setAsyncCallListener(new AsyncCallListener() {

                @SuppressWarnings("unchecked")
                @Override
                public void onResponseReceived(Object response) {
                    mCityModelList.clear();
                    mCityModelList.add(new CityModel("", "", "City", "", "", "", "", "0"));
                    mCityModelList.addAll((ArrayList<CityModel>) response);
                    Logger.e(TAG, "CITY SIZE ::" + mCityModelList.size());
                    if (mCityModelList.size() > 0) {
                        cityAdapter = new CityAdapter(getActivity(), mCityModelList);
                        mCitySpinner.setAdapter(cityAdapter);
                        if (mCityModelList.size() > 1) {
                            String count = "";
                            for (int i = 0; i < mCityModelList.size(); i++) {
                                if (city.equalsIgnoreCase(mCityModelList.get(i).name)) {
                                    Logger.d("CITY", "" + city);
                                    Logger.d("COUNT", "" + mCityModelList.get(i).count);
                                    count = mCityModelList.get(i).count;
                                }
                            }
                            if (Utils.validateString(count)) {
                                mCitySpinner.setSelection(cityAdapter.getPosition(Integer.parseInt(count)));
                            } else {
                                mCitySpinner.setAdapter(cityAdapter);
                                cityAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            getCityTask.execute(state_id);
        } else {
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert),
                    getResources().getString(R.string.connection));
        }
    }

    private void onEditProfile(String id, String firstname, String lastname,
                               String gender, String dob, String email, String whatsapp_id,
                               String address, String street, String landmark,
                               String city, String pincode, String state, String lat, String lng, String img) {

        if (Utils.checkInternetConnection(getActivity())) {
            EditProfileRequestTask editProfileRequestTask = new EditProfileRequestTask(getActivity());
            editProfileRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    RegisterModel registerModel = (RegisterModel) response;
                    if (registerModel.success.equalsIgnoreCase("1")) {
                        Toast.makeText(getActivity(), registerModel.message, Toast.LENGTH_SHORT).show();
//                            getActivity().getSupportFragmentManager().popBackStack();

                        Utils.storeString(mSharedPreferences, Utils.EDIT_PROFILE_IMAGE, "edit_profile_image");

                        getActivity().getSupportFragmentManager().popBackStack("MyProfileFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MyProfileFragment()).commit();

                    } else if (registerModel.success.equalsIgnoreCase("0")) {
                        Toast.makeText(getActivity(), registerModel.message, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            editProfileRequestTask.execute(id, firstname, lastname, gender, dob, email, whatsapp_id, address, street, landmark, city, pincode, state, lat, lng, img);
        } else {
            Toast.makeText(getActivity(), getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
//		if (resultCode == Activity.RESULT_OK) {
//			if (data != null) {
//				Uri selectedImage = data.getData();
//				String[] filePath = {MediaStore.Images.Media.DATA};
//				Cursor c = getActivity().getContentResolver().query(selectedImage, filePath, null, null, null);
//				c.moveToFirst();
//				int columnIndex = c.getColumnIndex(filePath[0]);
//				String picturePath = c.getString(columnIndex);
//				c.close();
//				mCurrentPhotoPath = picturePath;
//
//				Bitmap bitmap;
//
//				try {
//                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
//                    mProfileImg.setImageBitmap(bitmap);
//                    scaleImage(mProfileImg);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//				Logger.e(TAG, "picturepath--" + picturePath + "");
//			}
//		}

        File croppedImageFile = new File(getActivity().getFilesDir(), "test.jpg");

        if ((requestCode == REQUEST_PICTURE) && (resultCode == Activity.RESULT_OK)) {
            Uri croppedImage = Uri.fromFile(croppedImageFile);
            CropImageIntentBuilder cropImage = new CropImageIntentBuilder(200, 200, croppedImage);
            cropImage.setSourceImage(data.getData());

            startActivityForResult(cropImage.getIntent(getActivity()), REQUEST_CROP_PICTURE);
        } else if ((requestCode == REQUEST_CROP_PICTURE) && (resultCode == Activity.RESULT_OK)) {
            // When we are done cropping, display it in the ImageView.
            mProfileImg.setImageBitmap(BitmapFactory.decodeFile(croppedImageFile.getAbsolutePath()));
            mCurrentPhotoPath = croppedImageFile.getAbsolutePath().toString();
            mProfilePic.setVisibility(View.GONE);
            mImageLayout.setVisibility(View.VISIBLE);
        }
    }

    private void scaleImage(ImageView view) throws NoSuchElementException {
        // Get bitmap from the the ImageView.
        Bitmap bitmap = null;

        try {
            Drawable drawing = view.getDrawable();
            bitmap = ((BitmapDrawable) drawing).getBitmap();
        } catch (NullPointerException e) {
            throw new NoSuchElementException("No drawable on given view");
        } catch (ClassCastException e) {
            // Check bitmap is Ion drawable
//	        bitmap = Ion.with(view).getBitmap();
        }

        // Get current dimensions AND the desired bounding box
        int width = 0;

        try {
            width = bitmap.getWidth();
        } catch (NullPointerException e) {
            throw new NoSuchElementException("Can't find bitmap on given view/drawable");
        }

        int height = bitmap.getHeight();
        int bounding = dpToPx(150);
        Logger.i("Test", "original width = " + Integer.toString(width));
        Logger.i("Test", "original height = " + Integer.toString(height));
        Logger.i("Test", "bounding = " + Integer.toString(bounding));

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;
        Logger.i("Test", "xScale = " + Float.toString(xScale));
        Logger.i("Test", "yScale = " + Float.toString(yScale));
        Logger.i("Test", "scale = " + Float.toString(scale));

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        width = scaledBitmap.getWidth(); // re-use
        height = scaledBitmap.getHeight(); // re-use
        BitmapDrawable result = new BitmapDrawable(scaledBitmap);
        Logger.i("Test", "scaled width = " + Integer.toString(width));
        Logger.i("Test", "scaled height = " + Integer.toString(height));

        // Apply the scaled bitmap
        view.setImageDrawable(result);

        // Now change ImageView's dimensions to match the scaled image
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);

        Logger.i("Test", "done");
    }

    private int dpToPx(int dp) {
        float density = getActivity().getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            c.add(Calendar.YEAR, -18);

            long maxDate = c.getTime().getTime();

            int year = 0, month = 0, day = 0;
            if (mBirthDate.getText().toString().trim().equalsIgnoreCase("")) {
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            } else {
                String d = mBirthDate.getText().toString();
                year = Integer.parseInt(d.substring(6, 10));
                month = Integer.parseInt(d.substring(3, 5)) - 1;
                day = Integer.parseInt(d.substring(0, 2));
            }

            DatePickerDialog dpd = new DatePickerDialog(getActivity(), R.style.DialogTheme, this, year, month, day);
            dpd.getDatePicker().setMaxDate(maxDate);

            return dpd;
        }

        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            String day = String.valueOf(dd);
            String month = String.valueOf(mm + 1);
            if (day.trim().length() == 1)
                day = "0" + day;
            if (month.trim().length() == 1)
                month = "0" + month;
            mBirthDate.setText(new StringBuilder().append(day).append("/").append(month).append("/").append(yy).append(" "));
        }

    }

    public boolean fieldValidation() {
        boolean flag = true;
        if (!Utils.validateString(mFirstName.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.enter_first_name));
            mFirstName.requestFocus();
//			mFirstName.setError(getResources().getString(R.string.enter_first_name));
        } else if (!Utils.validateString(mLastName.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.enter_last_name));
            mLastName.requestFocus();
        } else if (!Utils.validateString(mBirthDate.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.select_gender_birthdate));
            mBirthDate.requestFocus();
        } else if (!Utils.validateString(mEmailId.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.enter_email));
            mEmailId.requestFocus();
        } else if (mEmailId.getText().toString().length() > 0 && !Utils.isEmailValid(mEmailId.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.invalid_email));
            mEmailId.requestFocus();
        }/* else if(!Utils.validateString(mQualifiaction.getText().toString())){
            flag = false;
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.enter_qualification));
			mQualifiaction.requestFocus();
		} else if(!Utils.validateString(mExperience.getText().toString())){
			flag = false;
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.enter_experience));
			mExperience.requestFocus();
		}  else if(!Utils.validateString(mProvider.getText().toString())){
			flag = false;
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.enter_experience));
			mProvider.requestFocus();
		}else if (!Utils.validateString(mWhatsappID.getText().toString())) {
			flag = false;
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.enter_whatsup_id));
		} else if (mWhatsappID.getText().toString().trim().length() < 10) {
			flag = false;
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.check_whatsup_length));
		}*/ else if (!Utils.validateString(mAddress.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.enter_address));
            mAddress.requestFocus();
        } else if (!Utils.validateString(mStreet.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.enter_street_road));
            mStreet.requestFocus();
        } else if (!Utils.validateString(mLandmark.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.enter_landmark));
            mLandmark.requestFocus();
        } else if (mStateSpinner.getSelectedItemPosition() == 0) {
            flag = false;
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.select_state));
            mStateSpinner.requestFocus();
        } else if (mCitySpinner.getSelectedItemPosition() == 0) {
            flag = false;
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.select_city));
            mCitySpinner.requestFocus();
        } else if (!Utils.validateString(mPincode.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.enter_pincode));
            mPincode.requestFocus();
        } else if (mPincode.getText().toString().trim().length() < 6) {
            flag = false;
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.check_pincode_length));
        }
        return flag;
    }


}
