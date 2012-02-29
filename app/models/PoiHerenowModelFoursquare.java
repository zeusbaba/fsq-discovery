package models;


import java.util.Arrays;
import java.util.LinkedList;

import javax.persistence.Entity;

import play.Logger;
import play.db.jpa.Model;

import com.google.gson.annotations.SerializedName;


/***
 * 	Copyright (c) 2011 WareNinja.com
 *  http://www.WareNinja.com - https://github.com/WareNinja
 *  	
 *  Author: yg@wareninja.com
 */

/*
 * represents search response model
 * for Foursquare
 */
public class PoiHerenowModelFoursquare {
	
	@SerializedName("id")
	public String id;
	@SerializedName("createdAt")
	public Long createdAt;
	@SerializedName("type")
	public String type;
	@SerializedName("timeZone")
	public String timeZone;
	
	@SerializedName("user_id")
	public String user_id;
	@SerializedName("user_firstName")
	public String user_firstName;
	@SerializedName("user_photo")
	public String user_photo;
	@SerializedName("user_gender")
	public String user_gender;
	@SerializedName("user_homeCity")
	public String user_homeCity;
	@SerializedName("user_canonicalUrl")
	public String user_canonicalUrl;
	
	
/*
hereNow: {
count: 3
items: [
	{
		id: "4f4e48b8e4b0ed369457a3ec"
		createdAt: 1330530488
		type: "checkin"
		timeZone: "America/New_York"
		user: {
				id: "1230423"
				firstName: "Roy"
				photo: "https://img-s.foursquare.com/userpix_thumbs/KPIEFYVZ4F5EOOUK.jpg"
				gender: "male"
				homeCity: "New York, NY"
				canonicalUrl: "https://foursquare.com/roychung"
			}
	},
	...
	]
}
 */
}

