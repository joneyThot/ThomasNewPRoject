package com.android.handy.service;

public interface AsyncCallListener {

	public void onResponseReceived(Object response);

	public void onErrorReceived(String error);
}
