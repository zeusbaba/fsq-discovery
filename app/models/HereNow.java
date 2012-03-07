package models;


import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;

import models.BaseLocations.LocType;

import play.Logger;

import play.modules.morphia.Model.AutoTimestamp;
import utils.LocoUtils;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.gson.annotations.SerializedName;


/***
 * 	Copyright (c) 2011-2012 WareNinja.com
 *  http://www.WareNinja.com - https://github.com/WareNinja
 *  	
 *  Author: yg@wareninja.com / twitter: @WareNinja
 */

@Entity(value="fsq_herenow", noClassnameStored=true)
@AutoTimestamp
public class HereNow extends BaseLocations {//implements Serializable {
	
	@Id @SerializedName("id")
	public String oid;
	@Override
    public Object getId() {
        return oid;
    }
    @Override
    protected void setId_(Object id) {
    	oid = processId_(id).toString();
    }
    protected static Object processId_(Object id) {
        return id.toString();
    }
	
    @SerializedName("loc_type")
    public LocType locType = LocType.FSQ_HERENOW;
    public String getCreatedTime() {
    	return LocoUtils.getFormattedDate( this._getCreated() );
    }
    public String getUpdatedTime() {
    	return LocoUtils.getFormattedDate( this._getModified() );
    }
    
	/*@Id @SerializedName("poiId")
	public String poiId;
    @Override
    public Object getId() {
        return poiId;
    }
    @Override
    protected void setId_(Object id) {
    	poiId = processId_(id).toString();
    }
    protected static Object processId_(Object id) {
        return id.toString();
    }*/
	
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
	
	@SerializedName("user_photo_hres")
	public String user_photo_hres;
	
	@SerializedName("user_gender")
	public String user_gender;
	@SerializedName("user_homeCity")
	public String user_homeCity;
	@SerializedName("user_canonicalUrl")
	public String user_canonicalUrl;
	
	

	@Override
	public String toString() {
		return "HereNow [" +
				"id=" + oid //+", poiId=" + poiId  
				+ ", locType="+locType
				+ ", createdAt="+ createdAt + ", type=" + type + ", timeZone=" + timeZone
				+ ", user_id=" + user_id + ", user_firstName=" + user_firstName
				+ ", user_photo=" + user_photo + ", user_gender=" + user_gender
				+ ", user_homeCity=" + user_homeCity + ", user_canonicalUrl="
				+ user_canonicalUrl + ", user_photo_hres=" + user_photo_hres
				+ "]";
	}
	
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

