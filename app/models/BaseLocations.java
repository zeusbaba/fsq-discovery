package models;


import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.utils.IndexDirection;

import play.modules.morphia.Model;
import play.modules.morphia.Model.AutoTimestamp;
import utils.LocoUtils;

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
public class BaseLocations extends BaseModel {

	public enum LocType {FSQ_POI, FSQ_TRENDING, FSQ_HERENOW};
	
	public Double lat;
	public Double lng;
	public long distance;
	
	@Indexed(IndexDirection.GEO2D)
	public Double[] latlng;
	public void updateLatlng() {
		this.latlng = new Double[] { lat, lng};
	}
	
	public BaseLocations() {
	}
	
}

