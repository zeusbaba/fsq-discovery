package models;


import java.io.Serializable;
import java.util.LinkedList;

import play.Logger;
import play.modules.morphia.Model.AutoTimestamp;
import utils.LocoUtils;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
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
@Entity(value="fsq_locations", noClassnameStored=true)
@AutoTimestamp
public class PoiModelFoursquare extends BaseLocations {//implements Serializable {

	public String updated;
    public String created;

    //public enum PoiType { FOURSQUARE};
    @SerializedName("loc_type")
	public LocType locType = LocType.FSQ_POI; 
	public String getCreatedTime() {
    	return LocoUtils.getFormattedDate( this._getCreated() );
    }
    public String getUpdatedTime() {
    	return LocoUtils.getFormattedDate( this._getModified() );
    }
    
	//@SerializedName("id")
	//public String oid;
	  
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
	
	
	@SerializedName("name") @Expose
	public String name;
	
	@Embedded @SerializedName("location") @Expose
	public PoiLocationModelFoursquare location;

	@Embedded @SerializedName("categories") @Expose
	public LinkedList<PoiCategoryModelFoursquare> categories;
	
	@Embedded @SerializedName("stats") @Expose
	public Stats stats;
	
	@Embedded @SerializedName("herenow") @Expose
	public LinkedList<HereNow> herenow;
	
	@Override
	public String toString() {
		return "PoiModelFoursquare [" +
				"id=" + oid + ", name=" + name
				+ ", locType="+locType
				+ ", location=" + location 
				+ ", categories="+categories
				+ ", stats="+stats
				+ ", herenow="+herenow
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
