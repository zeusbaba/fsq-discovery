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
public class PoiCategoryModelFoursquare implements Serializable {
	
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
}

