package com.handyman.fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handyman.MainActivity;
import com.handyman.R;
import com.handyman.logger.Logger;
import com.handyman.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

@SuppressLint("CutPasteId") public class SignatureFragment extends BaseFragment implements OnClickListener{
	
	private static String TAG = "SignatureFragment";
	
	private SharedPreferences mSharedPreferences;
	
	Fragment fr;
	View mRootView;
	EditText mJobTitle,mAmount, mYourName;
	TextView mSigTextView, mSaveSignatureBtn;
	ImageView mSignatureImg;
	LinearLayout mImageLayout;
	String job_description = "";
	Dialog signatureDialog;
	private int mDeviceWidth = 480;
	
	LinearLayout mContent;
    signature mSignature;
    public static String tempDir;
    public int count = 1;
    public String current = null;
    private Bitmap mBitmap;
    View mView;
    File mypath;
    String img_url = null;

    private String uniqueId;
//    private EditText yourName;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2) @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_signature, container, false);
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
		((MainActivity) getActivity()).setTitleText("", getString(R.string.signature), "", View.VISIBLE, View.GONE, View.GONE,View.GONE,View.GONE);
		getActivity().findViewById(R.id.title).setVisibility(View.GONE);
		getActivity().findViewById(R.id.txtUrl).setVisibility(View.GONE);
		
		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		mJobTitle  = (EditText) mRootView.findViewById(R.id.signature_job_title_text);
		mAmount = (EditText) mRootView.findViewById(R.id.signature_amount_text);
		mYourName = (EditText) mRootView.findViewById(R.id.signature_name_text);
		mImageLayout = (LinearLayout) mRootView.findViewById(R.id.image_layout);
		mSigTextView = (TextView) mRootView.findViewById(R.id.signature_txt);
		mSignatureImg = (ImageView) mRootView.findViewById(R.id.signature_image);
		mRootView.findViewById(R.id.signature_btn).setOnClickListener(this);
		mRootView.findViewById(R.id.signature_submit_Button).setOnClickListener(this);
		
		if(getArguments() != null){
			job_description = getArguments().getString(Utils.HANDYMAN_JOB_DESCRIPTION);
			mJobTitle.setText(job_description);
		}
		
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.signature_btn:
			openSignatureDialog();
			break;
			
		case R.id.signature_submit_Button:
			if(fieldValidation()){
				getActivity().getSupportFragmentManager().popBackStack("HandymanCustomerHireProfileFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
				getActivity().getSupportFragmentManager().popBackStack();
			}
			break;
			
		case R.id.cancel:
			signatureDialog.dismiss();
			break;
			
		case R.id.clear:
			 Logger.v("log_tag", "Panel Cleared");
             mSignature.clear();
             mSaveSignatureBtn.setEnabled(false);
			break;
		
		case R.id.getsign:
			
			Logger.v("log_tag", "Panel Saved");
			Logger.v("log_tag", "Image Url:" + img_url);
            mView.setDrawingCacheEnabled(true);
            mSignature.save(mView);
            
            if(Utils.validateString(img_url)){
				mSigTextView.setVisibility(View.GONE);
				mImageLayout.setVisibility(View.VISIBLE);
				
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

				Picasso.with(getActivity())
				.load(img_url)
				.error(R.drawable.avtar_images)
				.transform(transformation)
				.centerCrop()
				.resize(mDeviceWidth, (int) (mDeviceWidth))
				.into(mSignatureImg);
	
				Logger.e(TAG, "Sign Path--" + img_url + "");
			}
            
            signatureDialog.dismiss();
			break;
			
		}
	}
	
	public boolean fieldValidation() {
		boolean flag = true;
		if (!Utils.validateString(mAmount.getText().toString())) {
			flag = false;
			mAmount.setError("Please Enter Amount");
			mAmount.requestFocus();
		} else if(!Utils.validateString(mYourName.getText().toString())) {
			flag = false;
			mYourName.setError("Please Enter Your Name");
			mYourName.requestFocus();
		}
		return flag;
	}
	
	
	private void openSignatureDialog()
	{
		signatureDialog = new Dialog(getActivity());
		signatureDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		signatureDialog.setContentView(R.layout.signature_dialog);
		
		tempDir = Environment.getExternalStorageDirectory() + "/" + "GetSignature" + "/";
	    ContextWrapper cw = new ContextWrapper(getActivity());
	    File directory = cw.getDir("GetSignature", Context.MODE_PRIVATE);
	    
	    prepareDirectory();
	    uniqueId = getTodaysDate() + "_" + getCurrentTime() + "_" + Math.random();
	    current = uniqueId + ".png";
	    mypath= new File(directory,current);

	    mContent = (LinearLayout) signatureDialog.findViewById(R.id.linearLayout3);
	    mSignature = new signature(getActivity(), null);
	    mSignature.setBackgroundColor(Color.WHITE);
	    mContent.addView(mSignature, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		
	    signatureDialog.findViewById(R.id.cancel).setOnClickListener(this);
		signatureDialog.findViewById(R.id.clear).setOnClickListener(this);
		
		mSaveSignatureBtn = (TextView) signatureDialog.findViewById(R.id.getsign);
		mSaveSignatureBtn.setEnabled(false);
		mSaveSignatureBtn.setBackgroundResource(R.drawable.hover_btn_xxxhdpi);
		mSaveSignatureBtn.setOnClickListener(this);		
		mView = mContent;
		
//		yourName = (EditText) signatureDialog.findViewById(R.id.yourName);
		
		signatureDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
		signatureDialog.show();
	}
	
	
	 
	 /*private boolean captureSignature() {

	        boolean error = false;
	        String errorMessage = "";


	        if(yourName.getText().toString().equalsIgnoreCase("")){
	            errorMessage = errorMessage + "Please enter your Name\n";
	            error = true;
	        }   

	        if(error){
	            yourName.setError(errorMessage);
	            yourName.requestFocus();
	        }

	        return error;
	    }*/
	 
	 private String getTodaysDate() { 

	        final Calendar c = Calendar.getInstance();
	        int todaysDate =     (c.get(Calendar.YEAR) * 10000) + 
	        ((c.get(Calendar.MONTH) + 1) * 100) + 
	        (c.get(Calendar.DAY_OF_MONTH));
	        Logger.w("DATE:",String.valueOf(todaysDate));
	        return(String.valueOf(todaysDate));

	    }	

	    private String getCurrentTime() {

	        final Calendar c = Calendar.getInstance();
	        int currentTime =     (c.get(Calendar.HOUR_OF_DAY) * 10000) + 
	        (c.get(Calendar.MINUTE) * 100) + 
	        (c.get(Calendar.SECOND));
	        Logger.w("TIME:",String.valueOf(currentTime));
	        return(String.valueOf(currentTime));

	    }

	 private boolean prepareDirectory() 
	    {
	        try 
	        {
	            if (makedirs()) 
	            {
	                return true;
	            } else {
	                return false;
	            }
	        } catch (Exception e) 
	        {
	            e.printStackTrace();
	            Toast.makeText(getActivity(), "Could not initiate File System.. Is Sdcard mounted properly?", 1000).show();
	            return false;
	        }
	    }

	    private boolean makedirs() 
	    {
	        File tempdir = new File(tempDir);
	        if (!tempdir.exists())
	            tempdir.mkdirs();

	        if (tempdir.isDirectory()) 
	        {
	            File[] files = tempdir.listFiles();
	            for (File file : files) 
	            {
	                if (!file.delete()) 
	                {
	                    System.out.println("Failed to delete " + file);
	                }
	            }
	        }
	        return (tempdir.isDirectory());
	    }
	    

	public class signature extends View 
	    {
	        private static final float STROKE_WIDTH = 5f;
	        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
	        private Paint paint = new Paint();
	        private Path path = new Path();

	        private float lastTouchX;
	        private float lastTouchY;
	        private final RectF dirtyRect = new RectF();

	        public signature(Context context, AttributeSet attrs) 
	        {
	            super(context, attrs);
	            paint.setAntiAlias(true);
	            paint.setColor(Color.BLACK);
	            paint.setStyle(Paint.Style.STROKE);
	            paint.setStrokeJoin(Paint.Join.ROUND);
	            paint.setStrokeWidth(STROKE_WIDTH);
	        }

	        public void save(View v) 
	        {
	            Logger.v("log_tag", "Width: " + v.getWidth());
	            Logger.v("log_tag", "Height: " + v.getHeight());
	            if(mBitmap == null)
	            {
	                mBitmap =  Bitmap.createBitmap (mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);;
	            }
	            Canvas canvas = new Canvas(mBitmap);
	            try 
	            {
	                FileOutputStream mFileOutStream = new FileOutputStream(mypath);

	                v.draw(canvas); 
	                mBitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream); 
	                mFileOutStream.flush();
	                mFileOutStream.close();
	                img_url = Images.Media.insertImage(getActivity().getContentResolver(), mBitmap, "title", null);
	                Logger.v("log_tag","img_url: " + img_url);
	                //In case you want to delete the file
	                //boolean deleted = mypath.delete();
	                //Log.v("log_tag","deleted: " + mypath.toString() + deleted);
	                //If you want to convert the image to string use base64 converter

	            }
	            catch(Exception e) 
	            { 
	                Logger.v("log_tag", e.toString()); 
	            } 
	        }

	        public void clear() 
	        {
	            path.reset();
	            invalidate();
	        }

	        @Override
	        protected void onDraw(Canvas canvas) 
	        {
	            canvas.drawPath(path, paint);
	        }

	        @Override
	        public boolean onTouchEvent(MotionEvent event) 
	        {
	            float eventX = event.getX();
	            float eventY = event.getY();
	            mSaveSignatureBtn.setEnabled(true);
	            mSaveSignatureBtn.setBackgroundResource(R.drawable.btn_xxxhdpi);

	            switch (event.getAction()) 
	            {
	            case MotionEvent.ACTION_DOWN:
	                path.moveTo(eventX, eventY);
	                lastTouchX = eventX;
	                lastTouchY = eventY;
	                return true;

	            case MotionEvent.ACTION_MOVE:

	            case MotionEvent.ACTION_UP:

	                resetDirtyRect(eventX, eventY);
	                int historySize = event.getHistorySize();
	                for (int i = 0; i < historySize; i++) 
	                {
	                    float historicalX = event.getHistoricalX(i);
	                    float historicalY = event.getHistoricalY(i);
	                    expandDirtyRect(historicalX, historicalY);
	                    path.lineTo(historicalX, historicalY);
	                }
	                path.lineTo(eventX, eventY);
	                break;

	            default:
	                debug("Ignored touch event: " + event.toString());
	                return false;
	            }

	            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
	                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
	                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
	                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

	            lastTouchX = eventX;
	            lastTouchY = eventY;

	            return true;
	        }

	        private void debug(String string){
	        }

	        private void expandDirtyRect(float historicalX, float historicalY) 
	        {
	            if (historicalX < dirtyRect.left) 
	            {
	                dirtyRect.left = historicalX;
	            } 
	            else if (historicalX > dirtyRect.right) 
	            {
	                dirtyRect.right = historicalX;
	            }

	            if (historicalY < dirtyRect.top) 
	            {
	                dirtyRect.top = historicalY;
	            } 
	            else if (historicalY > dirtyRect.bottom) 
	            {
	                dirtyRect.bottom = historicalY;
	            }
	        }

	        private void resetDirtyRect(float eventX, float eventY) 
	        {
	            dirtyRect.left = Math.min(lastTouchX, eventX);
	            dirtyRect.right = Math.max(lastTouchX, eventX);
	            dirtyRect.top = Math.min(lastTouchY, eventY);
	            dirtyRect.bottom = Math.max(lastTouchY, eventY);
	        }
	    }
	
}
