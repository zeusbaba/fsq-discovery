package controllers;

import play.*;
import play.cache.Cache;
import play.i18n.Messages;
import play.libs.F.Promise;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;
import play.modules.morphia.Model.MorphiaQuery;
import play.mvc.*;

import utils.LocoUtils;
import utils.strtotime.strtotime;

import java.io.InputStreamReader;
import java.util.*;

import jobs.FoursquarePoiJob;
import jobs.FoursquareUserJob;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;

import models.PoiModelFoursquare;
import models.ResponseMeta;
import models.ResponseModel;

/***
 * 	Copyright (c) 2011 WareNinja.com
 *  http://www.WareNinja.com - https://github.com/WareNinja
 *  	
 *  Author: yg@wareninja.com
 */

public class SnLocationsController extends ApplicationBaseController {

	static final String KEY_PREFIX = "locations_";
	static final String CACHE_TTL = Play.configuration.getProperty("fsqdiscovery.cache.ttl");
	
	
	/*
	 * get&prepare mashup locations from different communities!!!
	 * 
	 * [prev] GET 	/api/locations/discover/{uid}/geo:{lat},{lng}/{limit}
	 * GET 	/api/locations/discover/geo:{lat},{lng}/?limit=&uid=
	 */
	public static void discoverFsqLocations(
			//String uid, 
			String lat, String lng
			) {
		
		String uid = params._contains("uid") ? params.get("uid") : "";
		String limit = params._contains("limit") ? params.get("limit") : "";
		limit = verifyRecordLimit(limit);
		
		Logger.info("uid, lat, lng, limit : %s, %s, %s, %s", uid, lat, lng, limit);
		
		// using Async jobs
		ResponseModel responseModel = new ResponseModel();
		ResponseMeta responseMeta = new ResponseMeta();
		LinkedList<Object> dataList = new LinkedList<Object>();
		
		HashMap params = new HashMap(); 
		String cacheKey = "geo:"+lat+","+lng;
		if (!StringUtils.isEmpty(limit)) cacheKey+="|"+limit;
		
		try { 
			
			dataList = (LinkedList<Object>) Cache.get(cacheKey);
			if (dataList==null) {
			
				dataList = new LinkedList<Object>();
				
				params.clear();
				if (!StringUtils.isEmpty(lat)&&!StringUtils.isEmpty(lng)) params.put("ll", lat+","+lng);
				if (!StringUtils.isEmpty(limit)) params.put("limit", limit);
				params.put("radius", Play.configuration.getProperty("fsqdiscovery.discovery.API_LOCO_SEARCHDISTANCE"));
				FoursquarePoiJob mFoursquarePoiJob = new FoursquarePoiJob();
				mFoursquarePoiJob.setReqParams(params);
				dataList.addAll( (LinkedList<Object>)mFoursquarePoiJob.doJobWithResult() );
				
				params.clear();
				FoursquareUserJob mFoursquareUserJob = new FoursquareUserJob();
				mFoursquareUserJob.setReqParams(params);
				mFoursquareUserJob.setPoiList(dataList);
				dataList.clear();
				dataList.addAll( (LinkedList<Object>)mFoursquareUserJob.doJobWithResult() );
				
				Logger.info("adding to cache!!! %s", dataList.size());
				Cache.safeAdd(cacheKey, dataList, Play.configuration.getProperty("fsqdiscovery.cache.ttl"));
			}
			else {
				Logger.info("found in cache!!! %s", dataList.size());
			}
			
			response.status = Http.StatusCode.OK;
	        responseMeta.code = response.status;
	        responseModel.meta = responseMeta;
	        responseModel.data = dataList;
	        
	        renderJSON(responseModel);
		}
    	catch (Exception ex) {
    	
    		responseMeta.code = Http.StatusCode.INTERNAL_ERROR;
    		gotError(responseMeta, ex);
	        
	        renderJSON(responseModel);
    	}
	}
	
	/*
	public static void discoverSnLocations(
			//String uid, 
			String lat, String lng
			) {
		
		String uid = params._contains("uid") ? params.get("uid") : "";
		String limit = params._contains("limit") ? params.get("limit") : "";
		limit = verifyRecordLimit(limit);
		
		Logger.info("uid, lat, lng, limit : %s, %s, %s, %s", uid, lat, lng, limit);
		
		
		// using Async jobs
		ResponseModel responseModel = new ResponseModel();
		ResponseMeta responseMeta = new ResponseMeta();
		LinkedList<Object> dataList = new LinkedList<Object>();
		
		HashMap params = new HashMap();
		LinkedList<Locations> resultList = new LinkedList<Locations>(); 
		String cacheKey = "geo:"+lat+","+lng;
		if (!StringUtils.isEmpty(limit)) cacheKey+="|"+limit;
		// TODO: improve record handling in cache! e.g. populate based on limit
		
		LocationsMashup locationsMashup = new LocationsMashup();
		locationsMashup.oid = cacheKey;
		locationsMashup.userid = uid;
		locationsMashup.lat = LocoUtils.stringToDouble(lat);
		locationsMashup.lng = LocoUtils.stringToDouble(lng);
		locationsMashup.updateLatlng();
		locationsMashup.locType = BaseLocations.LocType.MASHUP;
		
		try { 
			
			LocationsMashup locationsMashup1;
			
			locationsMashup1 = (LocationsMashup) Cache.get(locationsMashup.oid);
			if (locationsMashup1!=null) {
				
				resultList = locationsMashup1.locations;
				Logger.info("# of records found in cache -> %s", resultList.size());
				
				resultList = LocoUtils.calculateDistance(locationsMashup.lat, locationsMashup.lng, resultList);
				
				response.status = Http.StatusCode.OK;
		        responseMeta.code = response.status;
		        responseModel.meta = responseMeta;
		        responseModel.data = resultList;
		        
		        renderJSON(responseModel);
		        return;
			}
			Logger.info("NOT found in cache..!"); 
			
			MorphiaQuery q = LocationsMashup.q();
			q.or(q.criteria("_id").equal(locationsMashup.oid)
					, q.criteria("oid").equal(locationsMashup.oid));
			locationsMashup1 = q.first();
			if (locationsMashup1!=null) {
				resultList = locationsMashup1.locations;
				Logger.info("# of records found in Mongo -> %s", resultList.size());
				
				resultList = LocoUtils.calculateDistance(locationsMashup.lat, locationsMashup.lng, resultList);
				
				response.status = Http.StatusCode.OK;
		        responseMeta.code = response.status;
		        responseModel.meta = responseMeta;
		        responseModel.data = resultList;
		        
		        renderJSON(responseModel);
		        return;
			}
			Logger.info("NOT in Mongo, do load fresh data!");		
					
			
			params.clear();
			if (!StringUtils.isEmpty(lat)&&!StringUtils.isEmpty(lng)) params.put("ll", lat+","+lng);
			if (!StringUtils.isEmpty(limit)) params.put("limit", limit);
			FoursquareJob foursquareJob = new FoursquareJob();
			foursquareJob.setReqParams(params);
			dataList.addAll( (LinkedList<Object>)foursquareJob.doJobWithResult() );
			
			
			// now create mashup of locations!!!
			params.clear();
			params.put(Play.configuration.getProperty("fsqdiscovery.poitype.mashup-rawlist"), dataList);
			//params.put(Play.configuration.getProperty("elocome.poitype.mashup-meta-oid"), locationsMashup.oid);
			params.put(Play.configuration.getProperty("fsqdiscovery.poitype.mashup-meta-data"), locationsMashup);
			MashupLocationsJob mashupLocationsJob = new MashupLocationsJob();
			mashupLocationsJob.setReqParams(params);
			resultList = (LinkedList<Locations>)mashupLocationsJob.doJobWithResult();
			
			if (resultList.size() >0) {
				// --> save to MongoDB
		        //locationsMashup.locations.addAll(resultList);
				locationsMashup.addLocations(resultList);
		        locationsMashup.save();
				// --> also save to cache
		        Cache.safeAdd(locationsMashup.oid, locationsMashup, Play.configuration.getProperty("fsqdiscovery.cache.ttl"));
			} 
			
			resultList = LocoUtils.calculateDistance(locationsMashup.lat, locationsMashup.lng, resultList);
			
			
			response.status = Http.StatusCode.OK;
	        responseMeta.code = response.status;
	        responseModel.meta = responseMeta;
	        responseModel.data = resultList;
	        
	        renderJSON(responseModel);
		}
    	catch (Exception ex) {
    	
    		responseMeta.code = Http.StatusCode.INTERNAL_ERROR;
    		gotError(responseMeta, ex);
	        
	        renderJSON(responseModel);
    	}
	}
	*/
}