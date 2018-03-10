package com.handy.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Base64;
import android.widget.Toast;

import com.handy.logger.Logger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;


public class Utils {


    public static SharedPreferences sharedPreferences;
//    public static String URL_SERVER_ADDRESS = "http://192.168.1.113/myhandyman/api/";
//    public static String IMAGE_URL = "http://192.168.1.113/myhandyman/";
//	public static String URL_SERVER_ADDRESS = "http://113.20.17.215/myhandyman/api/";
//	public static String IMAGE_URL = "http://113.20.17.215/myhandyman/";
    public static String URL_SERVER_ADDRESS = "http://servicesathome.in/alpha/api/";
    public static String IMAGE_URL = "http://servicesathome.in/alpha/";
//    public static String URL_SERVER_ADDRESS = "http://servicesathome.in/beta/api/";
//    public static String IMAGE_URL = "http://servicesathome.in/beta/";

    public static String DOMIN_URL = "http://servicesathome.in/alpha/api/default/getdomainname";
//    public static String DOMIN_URL = "http://servicesathome.in/beta/api/default/getdomainname";

//    public static String URL_SERVER_ADDRESS = "";
//    public static String IMAGE_URL = "";

    public static String SENDER_ID = "1085788808372";
    public static final String REG_ID = "registration_id";
    public static final String DEVICE_ID = "device_id";

    public static boolean denyFlag = false, callFalg = false;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final String DISPLAY_MESSAGE_ACTION = "com.handy.pushnotifications.DISPLAY_MESSAGE";

    public static final String EXTRA_MESSAGE = "message";

    public static String REGISTER = "users/register";
    public static String LOGOUT = "users/logout";
    public static String ISLOGIN = "users/checkuserlogin";
    public static String IMAGE = "users/gethandymanimage";
    public static String GET_STATE = "state/getstate";
    public static String GET_CITY = "city/getcity";
    public static String LOGIN = "users/login";
    public static String CHANGE_PASSWORD = "users/changepassword";
    public static String FORGOT_PASSWORD = "users/forgotpassword";
    public static String OTP_RESEND_FORGOT = "users/forgotresendotp";
    public static String OTP_CODECHECK = "users/otpcheck";
    public static String OTP_CODECHECK_REGISTER = "users/registerotpcheck";

    public static String GET_CATEGORY_LIST = "category/getcategory";
    public static String GET_SUB_CATEGORY_LIST = "subcategory/getsubcategory";
    public static String GET_ALL_HANDYMAN_LIST = "users/nearbyhandyman";
    public static String GET_SEARCH_BY_HANDYMAN = "users/searchbyhandymanname";
    public static String GET_PROFILE_HANDYMAN_LIST = "users/gethmprofile";
    public static String GET_CUSTOMER_CREDIT = "customercredit/creditlog";
    public static String GET_PROFILE = "users/getmyprofile";
    public static String CHANGE_HANDYMAN_STATUS = "users/changestatus";
    public static String GET_MY_HIRINGS = "hire/getmyhiring";
    public static String GET_HANDYMAN_HIRINGS = "hire/gethmhiring";
    public static String GET_HANDYMAN_CHANGE_STATUS = "hire/changestatus";
    public static String GET_CATEGORY_OF_HANDYMAN = "hire/getcategoryofhandyman";
    public static String GET_FAVOURITE_HANDYMAN_LIST = "favourite/myfavouritehandyman";
    public static String GET_CONTACT_SUPPORT = "config/contactsupport";
    public static String GET_TOTAL_CREDITS_OF_HANDYMAN = "users/gettotalcreditofhandyman";
    public static String CONVERT_CREDIT_CALCULATION = "credittocash/creditconvertcalculation";
    public static String HANDYMAN_REQUEST_CREDIT_TO_CASH = "credittocash/handymanrequestcredittocash";
    public static String HANDYMAN_SEND_COMPLETE_NOTIFICATION = "hire/sendcompletenotification";
    public static String GET_REQUEST_STATUS_LIST = "credittocash/getmyrequest";
    public static String GET_CUSTOMER_COINS = "hire/getcustomercoins";
    public static String GET_COINS = "hire/getcoinvalue";

    public static String REMOVE_CREDIT = "customercredit/removelog";
    public static String JOB_COMPELETED = "hire/jobcompletedform";
    public static String APPLY_DISCOUNT = "hire/applydiscount";
    public static String GET_MY_REVIEW = "rating/handymanratingbyservices";
    //	public static String GET_HM_COLLECTION = "hire/gethmcollection";
//	public static String GET_HM_COLLECTION_OLD = "hire/gethmcollectionold2";
    public static String GET_HM_COLLECTION = "hire/gethmcollection";

    public static String HIRE_HANDYMAN = "hire/hirehandyman";
    public static String CUSTOMER_CONFIRM_CREDIT = "customercredit/confirmcredit";
    public static String EDIT_PROFILE = "users/editprofile";
    public static String EDIT_HANDYMAN_PROFILE = "users/edithandymanprofile";
    public static String FAVOURITE_HANDYMAN = "favourite/likehandyman";
    public static String RATE_HANDYMAN = "rating/ratehandyman";
    public static String RATE_CHECK_HANDYMAN = "rating/checkrating";
    public static String COMPLAIN_HANDYMAN = "complain/registercomplain";

    public static String TERMS_AND_CONDITION = "siteContent/getcontent?id=6";
    public static String ABOUT_US = "siteContent/getcontent?id=7";
    public static String PRIVACY_POLICY = "siteContent/getcontent?id=5";

    public static String REALTIME_LOCATION_UPDATE = "users/realtimelocationupdate";

    public static String PREF_NAME = "handyman";
    public static String TAG = "Utils";
    public static String USER_ID = "user_id";
    public static String OTP_CODE = "otp_code";

    public static String MOBILE_NO = "mobile_no";
    public static String PASSWORD = "password";
    public static String FIRSTNAME = "firstname";
    public static String LASTNAME = "lastname";
    public static String IMAGEPATH = "imagepath";
    public static String IMAGEPATH_PROFILE = "imagepath_profile";
    public static String EMAIL = "email";
    public static String USER_PROFILE = "user_profile";

    public static String CATEGORY_ITEM_DETAILS = "category_item_details";
    public static String ALL_HANDYMAN_ITEM_DETAILS = "all_handyman_item_details";

    // Category data
    public static String CATEGORY_ID = "category_id";
    public static String CATEGORY_NAME = "category_name";
    public static String CATEGORY_IMAGE_PATH = "category_image_path";

    public static String CATE_NAME = "cate_name";
    public static String SUB_CATE_NAME = "sub_cate_name";

    // Sub Category data
    public static String SUB_CATEGORY_ID = "sub_category_id";

    //Handyman data
    public static String HANDYMAN_ID = "handyman_id";
    public static String HANDYMAN_NAME = "handyman_name";
    public static String HANDYMAN_EMAIL = "handyman_email";
    public static String HANDYMAN_RATING = "handyman_rating";

    // My Profile
    public static String MY_PROFILE_LIST = "my_profile_list";

    // Handyman Hire
    public static String HANDYMAN_HIRE_PENDING_DETAILS = "handyman_hire_pending_details";
    public static String HANDYMAN_HIRE_ACCEPT_DETAILS = "handyman_hire_accept_details";
    public static String HANDYMAN_HIRE_START_DETAILS = "handyman_hire_start_details";
    public static String HANDYMAN_HIRE_CANCEL_DETAILS = "handyman_hire_cancel_details";
    public static String HANDYMAN_HIRE_COMPLETE_DETAILS = "handyman_hire_complete_details";
    public static String HANDYMAN_HIRE_REJECT_DETAILS = "handyman_hire_reject_details";
    public static String HANDYMAN_HIRE_ALL_DETAILS = "handyman_hire_all_details";
    public static String HANDYMAN_LATITUDE = "handyman_latitude";
    public static String HANDYMAN_LONGITUDE = "handyman_longitude";
    public static String CLIENT_ID = "client_id";
    public static String HANDYMAN_JOB_DESCRIPTION = "handyman_job_description";
    public static String JOB_ID = "job_id";
    public static String STATUS_ID = "status_id";

    public static String MY_COLLECTION_DAY_DETAILS = "my_collection_day_details";
    public static String MY_COLLECTION_MONTH_DETAILS = "my_collection_month_details";
    public static String MY_COLLECTION_WEEK_DETAILS = "my_collection_week_details";
    public static String MY_COLLECTION_CUSTOM_DETAILS = "my_collection_custom_details";

    public static String ACCEPT = "accept";
    public static String START = "start";
    public static String CANCEL = "cancel";
    public static String COMPELETE = "compelete";

    public static String CANCEL_A = "cancel_a";
    public static String CANCEL_S = "cancel_s";
    public static String COMPELETE_A = "compelete_a";

    public static String PENDING_ACCEPT = "pending_accept";
    public static String PENDING_CANCEL = "pending_cancel";

    public static String PENDING_ACCEPT_ALL = "pending_accept_all";
    public static String PENDING_CANCEL_ALL = "pending_cancel_all";

    public static String ACCEPT_CANCEL = "accept_cancel";
    public static String ACCEPT_CANCEL_ALL = "accept_cancel_all";
    public static String ACCEPT_START = "accept_start";
    public static String ACCEPT_START_ALL = "accept_start_all";


    public static String START_CANCEL = "start_cancel";
    public static String START_CANCEL_ALL = "start_cancel_all";
    public static String START_COMPELETE = "start_compelete";
    public static String START_COMPELETE_ALL = "start_compelete_all";


    public static String HANDYMAN_REVIEW_DETAILS = "handyman_review_details";

    public static String FROM_DATE = "from_date";
    public static String TO_DATE = "to_date";

    public static String CLIENT_NAME = "client_name";
    public static String CLIENT_EMAIL = "client_email";

    public static String NOTI_HIRE_STATUS = "noti_hire_status";
    public static String NOTI_IS_REQUEST = "noti_is_request";

    public static String LOGOUT_STATUS = "logout_stauts";

    public static boolean flag_hiring = false;
    public static boolean notificationFlag = false;

    public static String URL_ADDRESS = "URL_ADDRESS";
    public static String IMAGE_URL_ADDRESS = "IMAGE_URL_ADDRESS";

    // FCM

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";


    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }

    // return true if string is not null or empty
    public static boolean validateString(String object) {
        boolean flag = false;
        if (object != null && !object.isEmpty() && object.trim().length() > 0 && !object.equalsIgnoreCase("null") && !object.equalsIgnoreCase("")) {
            flag = true;
        }
        return flag;
    }

    public static String getReminingTime() {
        String delegate = "hh:mm aaa";
        return (String) DateFormat.format(delegate, Calendar.getInstance().getTime());
    }

    public static void storeString(SharedPreferences sharedPreferences, String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void storeInt(SharedPreferences sharedPreferences, String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void storeBoolean(SharedPreferences sharedPreferences, String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void storeStringGCM(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getHashKey(Context context) {
        String hashKey = "";
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                hashKey = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Logger.e("KeyHash:", hashKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashKey;
    }

    public static GPSTracker getCurrentLocation(Context context) {
        GPSTracker gps = new GPSTracker(context);
        // check if GPS enabled
        if (gps.canGetLocation()) {
            // double latitude = gps.getLatitude();
            // double longitude = gps.getLongitude();
            return gps;
        } else {
            // can't get location // GPS or Network is not enabled // Ask user
            // to enable GPS/network in settings
//            gps.showSettingsAlert();
        }
        return null;
    }

    public static void turnGPSOn(Context context) {
        /*Intent intent=new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
		context.sendBroadcast(intent);*/
        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!provider.contains("gps")) { //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            context.sendBroadcast(poke);
        }
    }


    public static String postRequest(String url, List<NameValuePair> nameValuePairs) {
        String request = "";
        String result = null;
        try {
            Logger.e(TAG, "url:: " + url);
            for (NameValuePair nvp : nameValuePairs) {
                request += nvp.getName() + "=" + nvp.getValue() + "&";
            }
            Logger.e(TAG, "request:: " + request);
            // Execute HTTP Post Request
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            HttpParams httpParameters = new BasicHttpParams();
            // Set the timeout in milliseconds until a connection is
            // established.
            int timeoutConnection = 20000;
            HttpConnectionParams.setConnectionTimeout(httpParameters,
                    timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT) in milliseconds which
            // is the timeout for waiting for data.
            int timeoutSocket = 20000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response1 = httpclient.execute(httppost);

            result = EntityUtils.toString(response1.getEntity());
            int maxLogSize = 1000;
            int start = 0, end = 0;
            for (int i = 0; i <= result.length() / maxLogSize; i++) {
                start = i * maxLogSize;
                end = (i + 1) * maxLogSize;
                end = end > result.length() ? result.length() : end;
                Logger.e("TAG", "result -- " + result.substring(start, end));
            }
            return result;
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }

    public static String postMultipart(String url, List<NameValuePair> nameValuePairs) {
        String request = "";
        String result = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(url);

        try {
            Logger.e(TAG, "url:: " + url);
            for (NameValuePair nvp : nameValuePairs) {
                request += nvp.getName() + "=" + nvp.getValue() + "&";
            }
            Logger.e(TAG, "request:: " + request);

            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            for (int index = 0; index < nameValuePairs.size(); index++) {
                if (nameValuePairs.get(index).getName().equalsIgnoreCase("img")) {
                    // If the key equals to "image", we use FileBody to transfer the data
                    entity.addPart(nameValuePairs.get(index).getName(), new FileBody(new File(nameValuePairs.get(index).getValue()), "image/jpeg"));
                } else {
                    // Normal string data
                    entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));
                }
            }

            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost, localContext);
            result = EntityUtils.toString(response.getEntity());
            int maxLogSize = 1000;
            int start = 0, end = 0;
            for (int i = 0; i <= result.length() / maxLogSize; i++) {
                start = i * maxLogSize;
                end = (i + 1) * maxLogSize;
                end = end > result.length() ? result.length() : end;
                Logger.e("TAG", "result -- " + result.substring(start, end));
            }
            return result;
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            return null;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String postMultipartPNG(String url, List<NameValuePair> nameValuePairs) {
        String request = "";
        String result = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(url);

        try {
            Logger.e(TAG, "url:: " + url);
            for (NameValuePair nvp : nameValuePairs) {
                request += nvp.getName() + "=" + nvp.getValue() + "&";
            }
            Logger.e(TAG, "request:: " + request);

            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            for (int index = 0; index < nameValuePairs.size(); index++) {
                if (nameValuePairs.get(index).getName().equalsIgnoreCase("img")) {
                    // If the key equals to "image", we use FileBody to transfer the data
                    entity.addPart(nameValuePairs.get(index).getName(), new FileBody(new File(nameValuePairs.get(index).getValue()), "image/png"));
                } else {
                    // Normal string data
                    entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));
                }
            }

            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost, localContext);
            result = EntityUtils.toString(response.getEntity());
            int maxLogSize = 1000;
            int start = 0, end = 0;
            for (int i = 0; i <= result.length() / maxLogSize; i++) {
                start = i * maxLogSize;
                end = (i + 1) * maxLogSize;
                end = end > result.length() ? result.length() : end;
                Logger.e("TAG", "result -- " + result.substring(start, end));
            }
            return result;
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            return null;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //HTTP post request
    public static String POST(String url, JSONObject jsonObject) {
        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();
            Logger.e(TAG, "url:: " + url);
            HttpPost httpPost = new HttpPost(url);
            HttpParams httpParameters = new BasicHttpParams();
            // Set the timeout in milliseconds until a connection is
            // established.
            int timeoutConnection = 20000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT) in milliseconds which
            // is the timeout for waiting for data.
            int timeoutSocket = 20000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            String json = "";
            Logger.e(TAG, "request:: " + jsonObject.toString());
            json = jsonObject.toString();
            StringEntity se = new StringEntity(json);
            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpclient.execute(httpPost);
            inputStream = httpResponse.getEntity().getContent();
            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
                Logger.e("TAG", "result -- " + result);
            } else {
                result = "Did not work!";
                Logger.e("TAG", "result -- " + result);
            }
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Logger.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    // convert inputstream to String
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;
    }


    public static String postRequest(String url) {
        String result = null;
        try {
            Logger.e("TAG", "url:: " + url);
            /*
             * for (NameValuePair nvp : nameValuePairs) { String name =
			 * nvp.getName(); String value = nvp.getValue(); Loggger.e("TAG",
			 * name +"="+value); }
			 */
            // Execute HTTP Post Request
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            HttpParams httpParameters = new BasicHttpParams();
            // Set the timeout in milliseconds until a connection is
            // established.
            int timeoutConnection = 200000;
            HttpConnectionParams.setConnectionTimeout(httpParameters,
                    timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT) in milliseconds which
            // is the timeout for waiting for data.
            int timeoutSocket = 200000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            // httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response1 = httpclient.execute(httppost);

            result = EntityUtils.toString(response1.getEntity());

            int maxLogSize = 1000;
            int start = 0, end = 0;
            for (int i = 0; i <= result.length() / maxLogSize; i++) {
                start = i * maxLogSize;
                end = (i + 1) * maxLogSize;
                end = end > result.length() ? result.length() : end;
                Logger.i("TAG", "" + result.substring(start, end));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }

    public static boolean checkInternetConnection(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            // Toast.makeText(context, "Conex�o com a internet indispon�vel.",
            // Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static final void showMessageDialog(Context context, String title, String message) {
        if (message != null && message.trim().length() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setCancelable(false);
            builder.setMessage(message);
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            // create alert dialog
            AlertDialog alertDialog = builder.create();
            // show it
            alertDialog.show();
        }
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        CharSequence inputStr = email;
        boolean flag = false;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            flag = true;
        }
        return flag;
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    public static boolean checkPlayServices(Context context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity) context, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(context, "This device is not supported google play services.", Toast.LENGTH_SHORT).show();
                //	            Log.i(TAG, "This device is not supported.");
                //	            finish();
            }
            return false;
        }
        return true;
    }

    // FCM

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

}