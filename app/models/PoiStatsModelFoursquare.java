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
public class PoiStatsModelFoursquare {

	@SerializedName("checkinsCount")
	public Integer checkinsCount;
	@SerializedName("usersCount")
	public Integer usersCount;
	@SerializedName("tipCount")
	public Integer tipCount;
	
	@Override
	public String toString() {
		return "PoiStatsModelFoursquare [checkinsCount=" + checkinsCount
				+ ", usersCount=" + usersCount + ", tipCount=" + tipCount + "]";
	}
	
/*
"stats": {
"checkinsCount": 259,
"usersCount": 151,
"tipCount": 11
},
"hereNow": {
"count": 0
}
 * 

 */
}

