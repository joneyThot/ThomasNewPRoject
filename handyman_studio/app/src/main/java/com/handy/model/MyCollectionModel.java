package com.handy.model;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class MyCollectionModel implements Serializable {

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
	
	@SerializedName("contact_person")
	public String contact_person = "";
	
	@SerializedName("contact_no")
	public String contact_no = "";
	
	@SerializedName("comment")
	public String comment = "";

	@SerializedName("hire_status")
	public String hire_status = "";
	
	@SerializedName("service_updated_by")
	public String service_updated_by = "";
	
	@SerializedName("is_outdated")
	public String is_outdated = "";
	
	@SerializedName("address")
	public String address = "";
	
	@SerializedName("floor")
	public String floor = "";

	@SerializedName("apartment")
	public String apartment = "";
	
//	@SerializedName("street")
//	public String street = "";
//	
//	@SerializedName("landmark")
//	public String landmark = "";
//	
//	@SerializedName("city")
//	public String city = "";
//	
//	@SerializedName("pincode")
//	public String pincode = "";
//	
//	@SerializedName("state")
//	public String state = "";
	
	@SerializedName("lat")
	public String lat = "";	
	
	@SerializedName("lng")
	public String lng = "";	
	
	@SerializedName("receiver_name")
	public String receiver_name = "";	
	
	@SerializedName("amount")
	public String amount = "";
	
	@SerializedName("completed_date")
	public String completed_date = "";
	
	@SerializedName("discount")
	public String discount = "";	
	
	@SerializedName("credit")
	public String credit = "";	
	
	@SerializedName("total")
	public String total = "";	
	
	@SerializedName("is_deleted")
	public String is_deleted = "";	
	
	@SerializedName("status")
	public String status = "";	
	
	@SerializedName("created_date")
	public String created_date = "";	
	
	@SerializedName("modified_date")
	public String modified_date = "";	
	
	@SerializedName("client_name")
	public String client_name = "";	
	
	@SerializedName("user_img")
	public String user_img = "";	
	
	@SerializedName("user_img_path")
	public String user_img_path = "";	
	
	@SerializedName("category_name")
	public String category_name = "";	
	
	@SerializedName("subcategory_name")
	public String subcategory_name = "";
	
	@SerializedName("city_name")
	public String city_name = "";
	
	@SerializedName("date_format")
	public String date_format = "";
	
	public String dateMain = "";
	
	@SerializedName("date")
	public String date = "";
	
	public int count = 0; 
	
	public String getDateMain() {
		return dateMain;
	}

	public void setDateMain(String dateMain) {
		this.dateMain = dateMain;
	}

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

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
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

	public String getService_updated_by() {
		return service_updated_by;
	}

	public void setService_updated_by(String service_updated_by) {
		this.service_updated_by = service_updated_by;
	}

	public String getIs_outdated() {
		return is_outdated;
	}

	public void setIs_outdated(String is_outdated) {
		this.is_outdated = is_outdated;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

//	public String getStreet() {
//		return street;
//	}
//
//	public void setStreet(String street) {
//		this.street = street;
//	}
//
//	public String getLandmark() {
//		return landmark;
//	}
//
//	public void setLandmark(String landmark) {
//		this.landmark = landmark;
//	}
//
//	public String getCity() {
//		return city;
//	}
//
//	public void setCity(String city) {
//		this.city = city;
//	}
//
//	public String getPincode() {
//		return pincode;
//	}
//
//	public void setPincode(String pincode) {
//		this.pincode = pincode;
//	}
//
//	public String getState() {
//		return state;
//	}
//
//	public void setState(String state) {
//		this.state = state;
//	}
	
	

	public String getLat() {
		return lat;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
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

	public String getReceiver_name() {
		return receiver_name;
	}

	public void setReceiver_name(String receiver_name) {
		this.receiver_name = receiver_name;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}


	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public String getCredit() {
		return credit;
	}

	public void setCredit(String credit) {
		this.credit = credit;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
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

	public String getClient_name() {
		return client_name;
	}

	public void setClient_name(String client_name) {
		this.client_name = client_name;
	}

	public String getUser_img() {
		return user_img;
	}

	public void setUser_img(String user_img) {
		this.user_img = user_img;
	}

	public String getUser_img_path() {
		return user_img_path;
	}

	public void setUser_img_path(String user_img_path) {
		this.user_img_path = user_img_path;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public String getSubcategory_name() {
		return subcategory_name;
	}

	public void setSubcategory_name(String subcategory_name) {
		this.subcategory_name = subcategory_name;
	}

	public String getCity_name() {
		return city_name;
	}

	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}

	public String getCompleted_date() {
		return completed_date;
	}

	public void setCompleted_date(String completed_date) {
		this.completed_date = completed_date;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getDate_format() {
		return date_format;
	}

	public void setDate_format(String date_format) {
		this.date_format = date_format;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getApartment() {
		return apartment;
	}

	public void setApartment(String apartment) {
		this.apartment = apartment;
	}
}
