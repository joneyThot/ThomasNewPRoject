package com.android.handyman.service;

public interface AsyncCallListener {

	public void onResponseReceived(Object response);

	public void onErrorReceived(String error);
}
