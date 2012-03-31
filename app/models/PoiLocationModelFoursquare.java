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

/*
 * 
 */
public class PoiLocationModelFoursquare implements Serializable {
	
	
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
}

