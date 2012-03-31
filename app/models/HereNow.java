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
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/***
 * 	Copyright (c) 2011-2012 WareNinja.com
 *  http://www.WareNinja.com - https://github.com/WareNinja
 *  Author: yg@wareninja.com / twitter: @WareNinja
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

@Entity(value="fsq_herenow", noClassnameStored=true)
@AutoTimestamp
public class HereNow extends BaseLocations implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id @SerializedName("id") @Expose
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
    
    public String poiId;
	
	@SerializedName("createdAt") @Expose
	public Long createdAt;
	@SerializedName("createdAtStr") @Expose
	public String createdAtStr;
	
	@SerializedName("type") @Expose
	public String type;
	@SerializedName("timeZone") @Expose
	public String timeZone;
	
	@SerializedName("user_id") @Expose
	public String user_id;
	@SerializedName("user_firstName") @Expose
	public String user_firstName;
	@SerializedName("user_lastName") @Expose
	public String user_lastName;
	@SerializedName("user_photo") @Expose
	public String user_photo;
	
	@SerializedName("user_photo_hres") @Expose
	public String user_photo_hres;
	
	@SerializedName("user_gender") @Expose
	public String user_gender;
	@SerializedName("user_homeCity") @Expose
	public String user_homeCity;
	@SerializedName("user_canonicalUrl") @Expose
	public String user_canonicalUrl;
	
	public void setCreatedAt(Long createdAt) {
		try {
			this.createdAt = createdAt;
			if (createdAt!=null && createdAt!=0L) this.createdAtStr = LocoUtils.getFormattedDate(createdAt*1000L);
		} catch (Exception ex) {}
	}
	public void setCreatedAt(Long createdAt, String timeZone) {
		try {
			this.createdAt = createdAt;
			if (createdAt!=null && createdAt!=0L) this.createdAtStr = LocoUtils.getFormattedDate(createdAt*1000L, timeZone);
		} catch (Exception ex) {}
	}

	@Override
	public String toString() {
		return "HereNow [" +
				"id=" + oid //+", poiId=" + poiId  
				+ ", locType="+locType
				+ ", createdAt="+ createdAt + ", createdAtStr="+ createdAtStr
				+ ", type=" + type + ", timeZone=" + timeZone
				+ ", user_id=" + user_id + ", user_firstName=" + user_firstName
				+ ", user_photo=" + user_photo + ", user_gender=" + user_gender
				+ ", user_homeCity=" + user_homeCity + ", user_canonicalUrl="
				+ user_canonicalUrl + ", user_photo_hres=" + user_photo_hres
				+ "]";
	}
}

