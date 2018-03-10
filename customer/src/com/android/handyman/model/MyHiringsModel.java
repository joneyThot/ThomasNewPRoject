package com.android.handyman.model;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class MyHiringsModel implements Serializable {

	public static final long serialVersionUID = 1L;

	@SerializedName("success")
	public String success = "";

	@SerializedName("message")
	public String message = "";

	@SerializedName("id")
	public String id = "";

	@SerializedName("handyman_id")
	public String handyman_id = "";

	@SerializedName("client_id")
	public String client_id = "";
	
	@SerializedName("order_id")
	public String order_id = "";

	@SerializedName("job_description")
	public String job_description = "";

	@SerializedName("category")
	public String category = "";

	@SerializedName("sub_category")
	public String sub_category = "";

	@SerializedName("appointment_date")
	public String appointment_date = "";

	@SerializedName("appointment_time")
	public String appointment_time = "";
	
	@SerializedName("client_name")
	public String client_name = "";
	
	@SerializedName("img")
	public String img = "";
	
	@SerializedName("img_path")
	public String img_path = "";
	

	@SerializedName("contact_person")
	public String contact_person = "";

	@SerializedName("contact_no")
	public String contact_no = "";

	@SerializedName("comment")
	public String comment = "";

	@SerializedName("hire_status")
	public String hire_status = "";

	@SerializedName("address")
	public String address = "";

	@SerializedName("street")
	public String street = "";

	@SerializedName("landmark")
	public String landmark = "";

	@SerializedName("city")
	public String city = "";

	@SerializedName("pincode")
	public String pincode = "";

	@SerializedName("state")
	public String state = "";

	@SerializedName("lat")
	public String lat = "";

	@SerializedName("lng")
	public String lng = "";
	
	@SerializedName("distance")
	public String distance = "";

	@SerializedName("is_deleted")
	public String is_deleted = "";

	@SerializedName("status")
	public String status = "";

	@SerializedName("created_date")
	public String created_date = "";

	@SerializedName("modified_date")
	public String modified_date = "";
	
	@SerializedName("category_name")
	public String category_name = "";
	
	@SerializedName("sub_category_name")
	public String sub_category_name = "";
	
	@SerializedName("debit")
	public String debit = "";
	
	@SerializedName("email")
	public String email = "";
	
	@SerializedName("city_name")
	public String city_name = "";
	
	@SerializedName("state_name")
	public String state_name = "";
	
	@SerializedName("handyman_name")
	public String handyman_name = "";
	
	@SerializedName("rating")
	public String rating = "";
	
	@SerializedName("handyman_img")
	public String handyman_img = "";
	
	@SerializedName("hamdyman_mobile")
	public String hamdyman_mobile = "";
	
	@SerializedName("closing_status")
	public String closing_status = "";
	
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHandyman_id() {
		return handyman_id;
	}

	public void setHandyman_id(String handyman_id) {
		this.handyman_id = handyman_id;
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	public String getJob_description() {
		return job_description;
	}

	public void setJob_description(String job_description) {
		this.job_description = job_description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSub_category() {
		return sub_category;
	}

	public void setSub_category(String sub_category) {
		this.sub_category = sub_category;
	}

	public String getAppointment_date() {
		return appointment_date;
	}

	public void setAppointment_date(String appointment_date) {
		this.appointment_date = appointment_date;
	}

	public String getAppointment_time() {
		return appointment_time;
	}

	public void setAppointment_time(String appointment_time) {
		this.appointment_time = appointment_time;
	}

	public String getContact_person() {
		return contact_person;
	}

	public void setContact_person(String contact_person) {
		this.contact_person = contact_person;
	}

	public String getContact_no() {
		return contact_no;
	}

	public void setContact_no(String contact_no) {
		this.contact_no = contact_no;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getHire_status() {
		return hire_status;
	}

	public void setHire_status(String hire_status) {
		this.hire_status = hire_status;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getLandmark() {
		return landmark;
	}

	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(String is_deleted) {
		this.is_deleted = is_deleted;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreated_date() {
		return created_date;
	}

	public void setCreated_date(String created_date) {
		this.created_date = created_date;
	}

	public String getModified_date() {
		return modified_date;
	}

	public void setModified_date(String modified_date) {
		this.modified_date = modified_date;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}


	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getClient_name() {
		return client_name;
	}

	public void setClient_name(String client_name) {
		this.client_name = client_name;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getImg_path() {
		return img_path;
	}

	public void setImg_path(String img_path) {
		this.img_path = img_path;
	}

	public String getDebit() {
		return debit;
	}

	public void setDebit(String debit) {
		this.debit = debit;
	}

	public String getSub_category_name() {
		return sub_category_name;
	}

	public void setSub_category_name(String sub_category_name) {
		this.sub_category_name = sub_category_name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHandyman_name() {
		return handyman_name;
	}

	public void setHandyman_name(String handyman_name) {
		this.handyman_name = handyman_name;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getHandyman_img() {
		return handyman_img;
	}

	public void setHandyman_img(String handyman_img) {
		this.handyman_img = handyman_img;
	}

	public String getClosing_status() {
		return closing_status;
	}

	public void setClosing_status(String closing_status) {
		this.closing_status = closing_status;
	}

	public String getCity_name() {
		return city_name;
	}

	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}

	public String getState_name() {
		return state_name;
	}

	public void setState_name(String state_name) {
		this.state_name = state_name;
	}

	public String getHamdyman_mobile() {
		return hamdyman_mobile;
	}

	public void setHamdyman_mobile(String hamdyman_mobile) {
		this.hamdyman_mobile = hamdyman_mobile;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	
}