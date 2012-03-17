package models;


import java.io.Serializable;
import java.util.LinkedList;

import javax.persistence.Entity;

import play.Logger;
import play.db.jpa.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/***
 * 	Copyright (c) 2011-2012 WareNinja.com
 *  http://www.WareNinja.com - https://github.com/WareNinja
 *  	
 *  Author: yg@wareninja.com / twitter: @WareNinja
 */

/*
 * represents search response model
 * for Foursquare
 */
public class PoiLocationModelFoursquare implements Serializable {//{//extends BaseModel {

	//public enum Type { VENUES, USERS };
	//public Type type;
	
	@SerializedName("lat") @Expose
	public Double lat;
	@SerializedName("lng") @Expose
	public Double lng;
	@SerializedName("distance") @Expose
	public Integer distance;
	@SerializedName("address") @Expose
	public String address;
	@SerializedName("city") @Expose
	public String city;
	@SerializedName("state") @Expose
	public String state;
	@SerializedName("postalCode") @Expose
	public String postalCode;
	
	@Override
	public String toString() {
		return "PoiLocationModelFoursquare [lat=" + lat + ", lng=" + lng
				+ ", distance=" + distance + ", address=" + address + ", city="
				+ city + ", state=" + state + ", postalCode=" + postalCode
				+ "]";
	}
	
/*
{
"meta": {
"code": 200
},

"response": {

"venues": [
{
"id": "4b0588b3f964a52095d422e3",
"name": "Mucho Mas",
"contact": {},
"location": {
"address": "Thorvald Meyers Gate 36",
"lat": 59.9241756,
"lng": 10.7592222,
"distance": 2,
"postalCode": "0555",
"city": "Oslo",
"state": "Norway"
},
"categories": [
{
"id": "4bf58dd8d48988d1c1941735",
"name": "Mexican Restaurant",
"pluralName": "Mexican Restaurants",
"shortName": "Mexican",
"icon": {
"prefix": "https://foursquare.com/img/categories/food/mexican_",
"sizes": [
32,
44,
64,
88,
256
],
"name": ".png"
},
"primary": true
}
],
"verified": false,
"stats": {
"checkinsCount": 259,
"usersCount": 151,
"tipCount": 11
},
"hereNow": {
"count": 0
}
},
 */
}

