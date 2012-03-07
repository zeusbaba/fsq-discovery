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
import jobs.FoursquareDiscoveryJob;

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
 * 	Copyright (c) 2011-2012 WareNinja.com
 *  http://www.WareNinja.com - https://github.com/WareNinja
 *  	
 *  Author: yg@wareninja.com / twitter: @WareNinja
 */

public class SnLocationsController extends ApplicationBaseController {

	//static final String KEY_PREFIX = "locations_";
	//static final String CACHE_TTL = Play.configuration.getProperty("fsqdiscovery.cache.ttl");
	static final String CACHE_KEYPREFIX = Play.configuration.getProperty("fsqdiscovery.cache.nearby-poi.keyprefix");
	static final String CACHE_TTL = Play.configuration.getProperty("fsqdiscovery.cache.nearby-poi.ttl");
	
	
	/*
	 * get&prepare mashup locations from different communities!!!
	 * 
	 * [prev] GET 	/api/locations/discover/{uid}/geo:{lat},{lng}/{limit}
	 * GET 	/api/locations/discover/geo:{lat},{lng}/?limit=&uid=
	 */
	public static void discoverFsqLocations(
			String lat, String lng
			) {
		
		String uid = params._contains("uid") ? params.get("uid") : "";
		String limit = params._contains("limit") ? params.get("limit") : "";
		limit = verifyRecordLimit(limit);
		String radius = params._contains("radius") ? params.get("radius") : "";
		
		Logger.info("uid, lat, lng, limit : %s, %s, %s, %s", uid, lat, lng, limit);
		
		// using Async jobs
		ResponseModel responseModel = new ResponseModel();
		ResponseMeta responseMeta = new ResponseMeta();
		LinkedList<Object> dataList = new LinkedList<Object>();
		
		HashMap params = new HashMap(); 
		String cacheKey = CACHE_KEYPREFIX+"geo:"+lat+","+lng;
		if (!StringUtils.isEmpty(limit)) cacheKey+="|"+limit;
		
		try { 
			
			dataList = (LinkedList<Object>) Cache.get(cacheKey);
			if (dataList==null) {
			
				dataList = new LinkedList<Object>();
				
				params.clear();
				if (!StringUtils.isEmpty(lat)&&!StringUtils.isEmpty(lng)) params.put("ll", lat+","+lng);
				if (!StringUtils.isEmpty(limit)) params.put("limit", limit);
				params.put("radius",
						!StringUtils.isEmpty(radius)?
								radius
								:Play.configuration.getProperty("fsqdiscovery.discovery.API_LOCO_SEARCHDISTANCE")
						);
				FoursquarePoiJob mFoursquarePoiJob = new FoursquarePoiJob();
				mFoursquarePoiJob.setReqParams(params);
				dataList.addAll( (LinkedList<Object>)mFoursquarePoiJob.doJobWithResult() );
				
				Logger.info("adding to cache!!! %s", dataList.size());
				Cache.set(cacheKey, dataList, CACHE_TTL);
			}
			else {
				Logger.info("Found in CACHE!!! %s", dataList.size());
			}
			
			// HereNow part
			params.clear();
			FoursquareDiscoveryJob mFoursquareUserJob = new FoursquareDiscoveryJob();
			mFoursquareUserJob.setReqParams(params);
			mFoursquareUserJob.setPoiList(dataList);
			dataList.clear();
			dataList.addAll( (LinkedList<Object>)mFoursquareUserJob.doJobWithResult() );
			
			
			response.status = Http.StatusCode.OK;
	        responseMeta.code = response.status;
	        responseModel.meta = responseMeta;
	        responseModel.data = dataList;
	        
	        //-renderJSON(responseModel);
	        renderJSON( LocoUtils.getGson().toJson(responseModel) );
		}
    	catch (Exception ex) {
    	
    		responseMeta.code = Http.StatusCode.INTERNAL_ERROR;
    		gotError(responseMeta, ex);
	        
	        renderJSON(responseModel);
    	}
	}
}