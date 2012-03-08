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
 *  	
 *  Author: yg@wareninja.com / twitter: @WareNinja
 */

/*
 * 
 */
public class BaseLocations extends BaseModel {

	public enum LocType {FSQ_POI, FSQ_TRENDING, FSQ_HERENOW};
	
	//public LocType locType;
	
	public Double lat;
	public Double lng;
	public long distance;
	
	@Indexed(IndexDirection.GEO2D)
	public Double[] latlng;
	public void updateLatlng() {
		this.latlng = new Double[] { lat, lng};
	}

	/*
	//TODO: @Indexed(value=IndexDirection.ASC, name="upc", unique=true, dropDups=false)
	public String name;
	public String description;
	public String address;
	*/
	
	public BaseLocations() {
	}
	
}

