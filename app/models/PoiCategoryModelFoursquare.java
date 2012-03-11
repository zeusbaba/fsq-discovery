package models;


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
public class PoiCategoryModelFoursquare {
	
	@SerializedName("id") @Expose
	public String oid;
	@SerializedName("name") @Expose
	public String name;
	@SerializedName("pluralName") @Expose
	public String pluralName;
	@SerializedName("shortName") @Expose
	public String shortName;
	@SerializedName("primary") @Expose
	public Boolean isPrimary;
	
	@SerializedName("icon") //@Expose
	public PoiCategoryIconModelFoursquare catIcon;
	
	@SerializedName("icons") @Expose
	public LinkedList<String> icons = new LinkedList<String>();
	public void updateCategoryIcons() {
		
		String iconUrl = "";
		if (catIcon!=null) {
			
			for (int i=0; i<catIcon.sizes.length; i++) {
				iconUrl = catIcon.prefix + catIcon.sizes[i] + catIcon.name;
				icons.add(iconUrl);
			}
		}
	}
	
	public String getImgUrl() {
		String imgUrl = "";
		
		if (catIcon!=null) {
			imgUrl = catIcon.prefix + "64" + catIcon.name;
		}
		
		return imgUrl;
	}

	@Override
	public String toString() {
		return "PoiCategoryModelFoursquare [oid=" + oid + ", name=" + name
				+ ", pluralName=" + pluralName + ", shortName=" + shortName
				+ ", isPrimary=" + isPrimary + ", catIcon=" + catIcon
				+ ", getImgUrl()="+getImgUrl()
				+ "]";
	}
	
/*
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
]
 * 
 * 
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

