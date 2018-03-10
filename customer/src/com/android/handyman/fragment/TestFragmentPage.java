package com.android.handyman.fragment;

public class TestFragmentPage extends BaseFragment{
	 private boolean isFirstSelected = false;

	    public void onPageSelected(boolean isInitial) {

	        // If it is the time just after the parent activity resumed,
	        // Setup of the focus is put off until onResume method executed.
	        if(isInitial) {
	            isFirstSelected = true;
	        } else {
	            setupFocus();
	        }
	    }

	    public void onResume() {
	        super.onResume();

	        // If setup of the focus is put off, do it here.
	        // because we want to make sure that onCreateView method has already been executed.
	        if(isFirstSelected) {
	            setupFocus();
	            isFirstSelected = false;
	        }
	    }

	    private void setupFocus() {
	        // Here is the same implementation as old onPageSelected method
	    }
}
