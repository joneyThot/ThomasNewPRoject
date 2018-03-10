package com.handy.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CoinAmountModel implements Serializable {

    public static final long serialVersionUID = 1L;

    @SerializedName("success")
    public String success = "";

    @SerializedName("message")
    public String message = "";

    @SerializedName("option_value")
    public String option_value = "";


    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOption_value() {
        return option_value;
    }

    public void setOption_value(String option_value) {
        this.option_value = option_value;
    }

}
