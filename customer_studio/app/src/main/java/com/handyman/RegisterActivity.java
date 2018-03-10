package com.handyman;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.provider.Settings.Secure;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.handyman.adapter.CityAdapter;
import com.handyman.adapter.StateAdapter;
import com.handyman.crop.CropImageIntentBuilder;
import com.handyman.logger.Logger;
import com.handyman.model.CityModel;
import com.handyman.model.RegisterModel;
import com.handyman.model.StateModel;
import com.handyman.service.AsyncCallListener;
import com.handyman.service.GetCityTask;
import com.handyman.service.GetStateTask;
import com.handyman.service.RegisterRequestTask;
import com.handyman.service.Utils;
import com.handyman.view.TextViewPlus;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.NoSuchElementException;

public class RegisterActivity extends Activity implements OnClickListener, OnDateSetListener {

    private static String TAG = "RegisterActivity";
    private SharedPreferences mSharedPreferences;
    private static final int SELECT_PICTURE = 1;
    private static final int REQUEST_IMAGE_FROM_GALLERY = 101;
    private static final String TEMP_PHOTO_FILE = "2";
    private static int REQUEST_PICTURE = 1;
    private static int REQUEST_CROP_PICTURE = 2;
    private static int REQUEST_CAMERA = 3;
    private int mDeviceWidth = 480;
    private ArrayList<StateModel> mStateModelList = new ArrayList<StateModel>();
    private ArrayList<CityModel> mCityModelList = new ArrayList<CityModel>();

    StateAdapter stateAdapter;
    CityAdapter cityAdapter;

    String selectedImagePath, base64String = "", mMaleFemale = null, mCurrentPhotoPath = "", device_Id = "", regId = "";

    EditText mMobileNo, mFirstName, mLastName, mBirthDate, mEmailId, mWhatsappID,
            mPassword, mConfirmPass, mAddress, mStreet, mLandmark, /*mState, mCity,*/
            mPincode;
    LinearLayout mImageLayout;
    CheckBox mTermsAndCondition;
    TextViewPlus mProfilePic;
    ImageView mMaleImg, mFemaleImg, mProfileImg;
    Spinner mStateSpinner, mCitySpinner;
    private BroadcastReceiver mRegistrationBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion < Build.VERSION_CODES.LOLLIPOP) {
            getActionBar().hide();
        }
        setContentView(R.layout.activity_register);

//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//			if ((this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) &&
//					(this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
//				requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 0);
//			}
//		}

        device_Id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
        initView();
        getState();

    }

    @SuppressLint("NewApi")
    private void initView() {
        mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
        ((ImageView) findViewById(R.id.backBtn)).setVisibility(View.VISIBLE);
        ((ImageView) findViewById(R.id.backBtn)).setOnClickListener(this);
        findViewById(R.id.title_back).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.title_back)).setText(getString(R.string.sign_up));

        mMobileNo = (EditText) findViewById(R.id.register_mobile_edittxt);
        mFirstName = (EditText) findViewById(R.id.register_firstname_edittxt);
        mLastName = (EditText) findViewById(R.id.register_lastname_edittxt);
        mBirthDate = (EditText) findViewById(R.id.register_birthdate);
        mBirthDate.setOnClickListener(this);
        mEmailId = (EditText) findViewById(R.id.register_emailid);
        mWhatsappID = (EditText) findViewById(R.id.register_whatsappid);
        mPassword = (EditText) findViewById(R.id.register_password);
        mConfirmPass = (EditText) findViewById(R.id.register_confirm_password);
        mAddress = (EditText) findViewById(R.id.register_address);
        mStreet = (EditText) findViewById(R.id.register_street);
        mLandmark = (EditText) findViewById(R.id.register_landmark);
//		mState = (EditText) findViewById(R.id.register_state);
//		mCity = (EditText) findViewById(R.id.register_city);
        mProfilePic = (TextViewPlus) findViewById(R.id.profile_txt);
        mStateSpinner = (Spinner) findViewById(R.id.register_spinner_state);
        mCitySpinner = (Spinner) findViewById(R.id.register_spinner_city);
        mPincode = (EditText) findViewById(R.id.register_pincode);

        mMaleImg = (ImageView) findViewById(R.id.male_img);
        mFemaleImg = (ImageView) findViewById(R.id.female_img);
        mProfileImg = (ImageView) findViewById(R.id.profile_image);

        mImageLayout = (LinearLayout) findViewById(R.id.image_layout);

        mTermsAndCondition = (CheckBox) findViewById(R.id.terms_and_conditon_checkbox);

        findViewById(R.id.terms_and_condition).setOnClickListener(this);
        findViewById(R.id.register_privacy_policy).setOnClickListener(this);

        findViewById(R.id.male_layout).setOnClickListener(this);
        findViewById(R.id.female_layout).setOnClickListener(this);

        findViewById(R.id.register_signup_Button).setOnClickListener(this);
        findViewById(R.id.browserbtn).setOnClickListener(this);

        boolean isNetAvailable = Utils.checkInternetConnection(this);
        if (!isNetAvailable) {
            Utils.showMessageDialog(this, getResources().getString(R.string.Error), getResources().getString(R.string.connection));
        } else {
//			if (Utils.checkPlayServices(this)) {
//				GCMRegistrar.checkDevice(this);
//				GCMRegistrar.checkManifest(this);
//				regId = GCMRegistrar.getRegistrationId(this);
//				Utils.storeString(mSharedPreferences, Utils.REG_ID, regId);
//
//				registerReceiver(mHandleMessageReceiver, new IntentFilter(Utils.DISPLAY_MESSAGE_ACTION));
//				
////				if (!GCMRegistrar.isRegistered(this)) {
////					GCMRegistrar.register(this, Utils.SENDER_ID);
////					
////				} else {
////					String GCMRegistrationId = mSharedPreferences.getString(Utils.REG_ID, "");
////					Logger.e("GCMRegistrationId", "GCMRegistrationId : " + GCMRegistrationId);
////				}
//				
//				if(!GCMRegistrar.isRegistered(this)){
//					GCMRegistrar.register(this, Utils.SENDER_ID);
//				} else {
//					String GCMRegistrationId = mSharedPreferences.getString(Utils.REG_ID, "");
//					Logger.e("GCMRegistrationId", "GCMRegistrationId : " + GCMRegistrationId);
//				}

           /* if (Utils.checkPlayServices(this)) {
                GCMRegistrar.checkDevice(this);
                GCMRegistrar.checkManifest(this);

                registerReceiver(mHandleMessageReceiver, new IntentFilter(Utils.DISPLAY_MESSAGE_ACTION));

                if (!GCMRegistrar.isRegistered(this)) {
                    GCMRegistrar.register(this, Utils.SENDER_ID);
                } else {
                    String GCMRegistrationId = mSharedPreferences.getString(Utils.REG_ID, "");
                    Logger.e("GCMRegistrationId", "GCMRegistrationId : " + GCMRegistrationId);
                }
            } else {
                Toast.makeText(this, getResources().getString(R.string.install_googleplay), Toast.LENGTH_LONG).show();
            }*/

            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    // checking for type intent filter
                    if (intent.getAction().equals(Utils.REGISTRATION_COMPLETE)) {
                        // gcm successfully registered
                        // now subscribe to `global` topic to receive app wide notifications
                        FirebaseMessaging.getInstance().subscribeToTopic(Utils.TOPIC_GLOBAL);

                        String GCMRegistrationId = mSharedPreferences.getString(Utils.REG_ID, "");
                        Logger.e(TAG, "GCMRegistrationId : " + GCMRegistrationId);

                    } else if (intent.getAction().equals(Utils.PUSH_NOTIFICATION)) {
                        // new push notification is received
//						String message = intent.getStringExtra("message");
//						Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                    }
                }
            };

        }

        mPincode.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_GO) {
                    findViewById(R.id.register_signup_Button).performClick();
                }
                return false;
            }
        });

        mStateSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    getCity(((StateModel) mStateSpinner.getSelectedItem()).id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        WindowManager w = getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            w.getDefaultDisplay().getSize(size);
            mDeviceWidth = size.x;
        } else {
            Display d = w.getDefaultDisplay();
            mDeviceWidth = d.getWidth();
        }

    }

    /**
     * Receiving push messages
     */
   /* private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Waking up mobile if it is sleeping
            WakeLocker.acquire(getApplicationContext());
            // Releasing wake lock
            WakeLocker.release();
        }
    };

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(mHandleMessageReceiver);
            GCMRegistrar.onDestroy(this);
        } catch (Exception e) {
            Logger.e("UnRegister Receiver Error", "> " + e.getMessage());
        }
        super.onDestroy();
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Utils.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Utils.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
//		NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.backBtn:
                finish();
                break;

            case R.id.register_signup_Button:
                if (fieldValidation()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Registration");
                    builder.setCancelable(false);
                    builder.setMessage("Are you sure for register.");
                    builder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    if (getCurrentFocus() != null) {
                                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                    }

//								if(mCurrentPhotoPath.equalsIgnoreCase(null)){
//									mCurrentPhotoPath = "";
//								}

                                    String b_date = mBirthDate.getText().toString();
                                    String birth_date = b_date.substring(6, 10) + "-" + b_date.substring(3, 5) + "-" + b_date.substring(0, 2);

                                    onRegister(mMobileNo.getText().toString(), mFirstName.getText().toString(), mLastName.getText().toString(), mMaleFemale, birth_date,
                                            mEmailId.getText().toString(), mWhatsappID.getText().toString(), mPassword.getText().toString(), mAddress.getText().toString(),
                                            mStreet.getText().toString(), mLandmark.getText().toString(), ((CityModel) mCitySpinner.getSelectedItem()).id, mPincode.getText().toString(),
                                            ((StateModel) mStateSpinner.getSelectedItem()).id, "www.google.com", String.valueOf(SplashActivity.latitude),
                                            String.valueOf(SplashActivity.longitude), device_Id, mSharedPreferences.getString(Utils.REG_ID, ""), mCurrentPhotoPath);
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
                }
                break;

            case R.id.register_birthdate:
//			Calendar c = Calendar.getInstance();
//			int year = c.get(Calendar.YEAR);
//			int month = c.get(Calendar.MONTH);
//			int day = c.get(Calendar.DAY_OF_MONTH);

                Calendar c = Calendar.getInstance();
                c.add(Calendar.YEAR, -18);
                int year = 0, month = 0, day = 0;
                if (mBirthDate.getText().toString().trim().equalsIgnoreCase("")) {
                    year = c.get(Calendar.YEAR);
                    month = c.get(Calendar.MONTH);
                    day = c.get(Calendar.DAY_OF_MONTH);
                } else {
//		    	11/11/1111
                    String d = mBirthDate.getText().toString();

                    day = Integer.parseInt(d.substring(0, 2));
                    month = Integer.parseInt(d.substring(3, 5)) - 1;
                    year = Integer.parseInt(d.substring(6, 10));

                }

                long maxDate = c.getTime().getTime();

                DatePickerDialog dialog = new DatePickerDialog(this, R.style.DialogTheme, this, year, month, day);
                dialog.getDatePicker().setMaxDate(maxDate);
                dialog.setTitle("");
                dialog.show();
                break;

            case R.id.browserbtn:
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                } else {

                    Intent intent1 = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    Intent intent1 = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent1, REQUEST_PICTURE);
//                    startActivityForResult(MediaStoreUtils.getPickImageIntent(this), REQUEST_PICTURE);
                }

//			try  {
//		          // Launch picker to choose photo for selected contact
//		          Intent intent =  new  Intent (Intent.ACTION_GET_CONTENT,  null );
//		          intent.setType ( "Image / *" );
//		          intent.putExtra ( "crop" ,  "true" );
//		          intent.putExtra (MediaStore.EXTRA_OUTPUT, getTempUri ());
//		          startActivityForResult (intent, REQUEST_IMAGE_FROM_GALLERY);
//		     	}  catch (ActivityNotFoundException e) {
//		        	  Toast.makeText (this, "Not Found", Toast.LENGTH_LONG) .show ();
//			}

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
                break;

            case R.id.terms_and_condition:
                startActivity(new Intent(RegisterActivity.this, TermsAndConditionActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                break;

            case R.id.register_privacy_policy:
                startActivity(new Intent(RegisterActivity.this, PrivacyPolicyActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//		mBirthDate.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
        String day = String.valueOf(dayOfMonth);
        String month = String.valueOf(monthOfYear + 1);
        if (day.trim().length() == 1)
            day = "0" + day;
        if (month.trim().length() == 1)
            month = "0" + month;
        mBirthDate.setText(new StringBuilder().append(day).append("/").append(month).append("/").append(year).append(" "));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (resultCode == Activity.RESULT_OK) {
//            if (data != null) {
//                Uri selectedImage = data.getData();
//                String[] filePath = {MediaStore.Images.Media.DATA};
//                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
//                c.moveToFirst();
//                int columnIndex = c.getColumnIndex(filePath[0]);
//                String picturePath = c.getString(columnIndex);
//                c.close();
//                mCurrentPhotoPath = picturePath;
//                mProfilePic.setVisibility(View.GONE);
//                mImageLayout.setVisibility(View.VISIBLE);
//
//                Bitmap bitmap;
//
//                try {
//                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
//                    mProfileImg.setImageBitmap(bitmap);
//                    scaleImage(mProfileImg);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                Logger.e(TAG, "picturepath--" + picturePath + "");
//            }
//        }


        File croppedImageFile = new File(getFilesDir(), "test.jpg");

        if ((requestCode == REQUEST_PICTURE) && (resultCode == RESULT_OK)) {
            Uri croppedImage = Uri.fromFile(croppedImageFile);
            CropImageIntentBuilder cropImage = new CropImageIntentBuilder(200, 200, croppedImage);
            cropImage.setSourceImage(data.getData());

            startActivityForResult(cropImage.getIntent(this), REQUEST_CROP_PICTURE);
        } /*else if((requestCode == REQUEST_CAMERA) && (resultCode == RESULT_OK)){

            Bitmap btimap = (Bitmap) data.getExtras().get("data");
            Uri croppedImage = Uri.fromFile(croppedImageFile);
            CropImageIntentBuilder cropImage = new CropImageIntentBuilder(200, 200, croppedImage);
            cropImage.setBitmap(btimap);

            startActivityForResult(cropImage.getIntent(this), REQUEST_CROP_PICTURE);
        }*/ else if ((requestCode == REQUEST_CROP_PICTURE) && (resultCode == RESULT_OK)) {
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
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private void getState() {
        if (Utils.checkInternetConnection(this)) {
            GetStateTask getStateTask = new GetStateTask(this);
            getStateTask.setAsyncCallListener(new AsyncCallListener() {

                @SuppressWarnings("unchecked")
                @Override
                public void onResponseReceived(Object response) {
                    mStateModelList.add(new StateModel("", "State", "", "", "", "", ""));
                    mStateModelList.addAll((ArrayList<StateModel>) response);
                    if (mStateModelList.size() > 0) {
                        stateAdapter = new StateAdapter(RegisterActivity.this, mStateModelList);
                        mStateSpinner.setAdapter(stateAdapter);

                        mCityModelList.clear();
                        mCityModelList.add(new CityModel("", "", "City", "", "", "", "", ""));
                        cityAdapter = new CityAdapter(RegisterActivity.this, mCityModelList);
                        mCitySpinner.setAdapter(cityAdapter);
                    }
                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getApplicationContext(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            getStateTask.execute();
        } else {
            Utils.showMessageDialog(this, getResources().getString(R.string.alert),
                    getResources().getString(R.string.connection));
        }
    }

    private void getCity(String state_id) {
        if (Utils.checkInternetConnection(this)) {
            GetCityTask getCityTask = new GetCityTask(this);
            getCityTask.setAsyncCallListener(new AsyncCallListener() {

                @SuppressWarnings("unchecked")
                @Override
                public void onResponseReceived(Object response) {
                    mCityModelList.clear();
                    mCityModelList.add(new CityModel("", "", "City", "", "", "", "", ""));
                    mCityModelList.addAll((ArrayList<CityModel>) response);

                    if (mCityModelList.size() > 0) {
                        cityAdapter = new CityAdapter(RegisterActivity.this, mCityModelList);
                        mCitySpinner.setAdapter(cityAdapter);
                        cityAdapter.notifyDataSetChanged();
                    }

                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getApplicationContext(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            getCityTask.execute(state_id);
        } else {
            Utils.showMessageDialog(this, getResources().getString(R.string.alert),
                    getResources().getString(R.string.connection));
        }
    }

    private void onRegister(String mobile, String firstname, String lastname,
                            String gender, String dob, String email, String whatsapp_id,
                            String password, String address, String street, String landmark,
                            String city, String pincode, String state, String website,
                            String lat, String lng, String device_id, String device_token, String img) {

        if (Utils.checkInternetConnection(this)) {
            RegisterRequestTask registerRequestTask = new RegisterRequestTask(this);
            registerRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    RegisterModel registerModel = (RegisterModel) response;
                    if (registerModel.success.equalsIgnoreCase("1")) {
                        Toast.makeText(RegisterActivity.this, registerModel.message, Toast.LENGTH_SHORT).show();
//                            Utils.showMessageDialog(RegisterActivity.this, getResources().getString(R.string.alert), registerModel.message);
                        Logger.i(TAG, "USER ID :: " + mSharedPreferences.getString(Utils.USER_ID, ""));

                        Bundle bundle = new Bundle();
                        bundle.putString(Utils.MOBILE_NO, mSharedPreferences.getString(Utils.MOBILE_NO, ""));
//                        bundle.putString(Utils.OTP_CODE, registerModel.user.otp);
                        bundle.putString("FROM", "REGISTER");
                        Intent intent = new Intent(RegisterActivity.this, OtpPasswordActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                        finish();

                    } else if (registerModel.success.equalsIgnoreCase("0")) {
//                            Toast.makeText(RegisterActivity.this, registerModel.message , Toast.LENGTH_SHORT).show();
                        Utils.showMessageDialog(RegisterActivity.this, getResources().getString(R.string.alert), registerModel.message);
                    }
                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            registerRequestTask.execute(mobile, firstname, lastname, gender, dob, email, whatsapp_id, password, address, street, landmark, city, pincode, state, website, lat, lng, device_id, device_token, img);
        } else {
            Toast.makeText(RegisterActivity.this, getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }
    }


    public boolean fieldValidation() {
        boolean flag = true;
        if (!Utils.validateString(mMobileNo.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.enter_mobile_no));
//			mMobileNo.setError(getResources().getString(R.string.enter_mobile_no));
            mMobileNo.requestFocus();
        } else if (mMobileNo.getText().toString().trim().length() < 10) {
            flag = false;
            Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.enter_mobile_no_length));
//			mMobileNo.setError(getResources().getString(R.string.enter_mobile_no_length));
            mMobileNo.requestFocus();
        } else if (!Utils.validateString(mPassword.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.enter_password));
//			mPassword.setError(getResources().getString(R.string.enter_password));
            mPassword.requestFocus();
        } else if (mPassword.getText().toString().trim().length() < 8) {
            flag = false;
            Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.check_password_length));
//			mPassword.setError(getResources().getString(R.string.check_password_length));
            mPassword.requestFocus();
        } else if (!Utils.validateString(mConfirmPass.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.enter_con_password));
//			mConfirmPass.setError(getResources().getString(R.string.enter_con_password));
            mConfirmPass.requestFocus();
        } else if (!mPassword.getText().toString().equalsIgnoreCase(mConfirmPass.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.password_not_match));
//			mConfirmPass.setError(getResources().getString(R.string.password_not_match));
            mConfirmPass.requestFocus();
        } else if (!Utils.validateString(mFirstName.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.enter_first_name));
//			mFirstName.setError(getResources().getString(R.string.enter_first_name));
            mFirstName.requestFocus();
        } else if (!Utils.validateString(mLastName.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.enter_last_name));
            mLastName.requestFocus();
        } else if (!Utils.validateString(mMaleFemale)) {
            flag = false;
            Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.select_gender_female));
        } else if (!Utils.validateString(mBirthDate.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.select_gender_birthdate));
            mBirthDate.requestFocus();
        } else if (!Utils.validateString(mEmailId.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.enter_email));
            mEmailId.requestFocus();
        } else if (mEmailId.getText().toString().length() > 0 && !Utils.isEmailValid(mEmailId.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.invalid_email));
            mEmailId.requestFocus();
        } /*else if (!Utils.validateString(mWhatsappID.getText().toString())) {
            flag = false;
			Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.enter_whatsup_id));
			mWhatsappID.requestFocus();
		} else if (mWhatsappID.getText().toString().trim().length() < 10) {
			flag = false;
			Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.check_whatsup_length));
		} */ else if (!Utils.validateString(mAddress.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.enter_address));
            mAddress.requestFocus();
        } else if (!Utils.validateString(mStreet.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.enter_street_road));
            mStreet.requestFocus();
        } else if (!Utils.validateString(mLandmark.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.enter_landmark));
            mLandmark.requestFocus();
        } else if (mStateSpinner.getSelectedItemPosition() == 0) {
            flag = false;
            Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.select_state));
            mStateSpinner.requestFocus();
        } else if (mCitySpinner.getSelectedItemPosition() == 0) {
            flag = false;
            Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.select_city));
            mCitySpinner.requestFocus();
        } else if (!Utils.validateString(mPincode.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.enter_pincode));
            mPincode.requestFocus();
        } else if (mPincode.getText().toString().trim().length() < 6) {
            flag = false;
            Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.check_pincode_length));
        } else if (mTermsAndCondition.isChecked() != true) {
            flag = false;
            Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.checked_terms_condition));
        }
        return flag;
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }

}
