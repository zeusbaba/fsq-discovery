package models;


import java.io.Serializable;
import java.util.Arrays;
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
public class Stats implements Serializable {

	@SerializedName("checkinsCount") @Expose
	public Integer checkinsCount;
	@SerializedName("usersCount") @Expose
	public Integer usersCount;
	@SerializedName("tipCount") @Expose
	public Integer tipCount;
	
	@SerializedName("herenowCount") @Expose
	public Integer herenowCount;
	
	@Override
	public String toString() {
		return "Stats [checkinsCount=" + checkinsCount
				+ ", usersCount=" + usersCount + ", tipCount=" + tipCount
				+ ", herenowCount="+herenowCount
				+ "]";
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

