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

import jobs.FoursquareDiscoverPoiJob;
import jobs.FoursquareDiscoverHereNowJob;
import jobs.FoursquareTrendingPoiJob;

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
	static final String CACHE_KEYPREFIX_NEARBY = Play.configuration.getProperty("fsqdiscovery.cache.nearby-poi.keyprefix", "nearby_poi_");
	static final String CACHE_TTL_NEARBY = Play.configuration.getProperty("fsqdiscovery.cache.nearby-poi.ttl", "30mn");
	
	static final String CACHE_KEYPREFIX_TRENDING = Play.configuration.getProperty("fsqdiscovery.cache.trending-poi.keyprefix", "trending_poi_");
	static final String CACHE_TTL_TRENDING = Play.configuration.getProperty("fsqdiscovery.cache.trending-poi.ttl", "2h");
	
	
	/*
	 * get&prepare FSQ locations  - Discover
	 * 
	 * GET 	/api/locations/discover/geo:{lat},{lng}/?limit=&appid=&radius=
	 */
	public static void discoverFsqLocations(
			String lat, String lng
			) {
		
		String appid = params._contains(PARAM_APPID) ? params.get(PARAM_APPID) : "";
		String limit = params._contains(PARAM_LIMIT) ? params.get(PARAM_LIMIT) : "";
		limit = verifyRecordLimit(limit);
		String radius = params._contains(PARAM_RADIUS) ? params.get(PARAM_RADIUS) : "";
		radius = verifyRadius(radius);
		String herenow = params._contains(PARAM_HERENOW) ? params.get(PARAM_HERENOW) : "true";// by default : true
		String query = params._contains(PARAM_QUERY) ? params.get(PARAM_QUERY) : "";
		
		Logger.info("PARAMS -> appid:%s ; lat,lng:%s,%s ; radius:%s ; limit:%s ; herenow:%s ; query:%s", appid, lat, lng, radius, limit, herenow, query);

		ResponseModel responseModel = new ResponseModel();
		ResponseMeta responseMeta = new ResponseMeta();
		LinkedList<Object> dataList = new LinkedList<Object>();
		
		HashMap params = new HashMap(); 
		String cacheKey = CACHE_KEYPREFIX_NEARBY+"geo:"+lat+","+lng;
		if (!StringUtils.isEmpty(limit)) cacheKey+="|"+limit;
		if (!StringUtils.isEmpty(query)) cacheKey+="|"+query;
		
		try { 
			
			//dataList = (LinkedList<Object>) Cache.get(cacheKey);
			dataList = Cache.get(cacheKey, LinkedList.class);
			if (dataList==null) {
			
				dataList = new LinkedList<Object>();
				
				params.clear();
				if (!StringUtils.isEmpty(lat)&&!StringUtils.isEmpty(lng)) params.put("ll", lat+","+lng);
				if (!StringUtils.isEmpty(limit)) params.put(PARAM_LIMIT, limit);
				
				if (!StringUtils.isEmpty(radius)) params.put(PARAM_RADIUS, radius);
				/*params.put(PARAM_RADIUS,
						!StringUtils.isEmpty(radius)?
								radius
								:Play.configuration.getProperty("fsqdiscovery.discovery.API_LOCO_SEARCHDISTANCE")
						);*/
				if (!StringUtils.isEmpty(query)) params.put(PARAM_QUERY, query);
				
				FoursquareDiscoverPoiJob mFoursquarePoiJob = new FoursquareDiscoverPoiJob();
				mFoursquarePoiJob.setReqParams(params);
				dataList.addAll( (LinkedList<Object>)mFoursquarePoiJob.doJobWithResult() );
				
				if (dataList.size()>0) {
					Logger.info("adding to cache!!! %s", dataList.size());
					Cache.set(cacheKey, dataList, CACHE_TTL_NEARBY);	
				}
				else {
					Logger.info("NO NEED to cache, dataList.size(): 0");
					
					response.status = Http.StatusCode.OK;
			        responseMeta.code = response.status;
			        responseModel.meta = responseMeta;
			        responseModel.data = dataList;
			        
			        renderJSON( LocoUtils.getGson().toJson(responseModel) );
				}
			}
			else {
				Logger.info("Found in CACHE!!! %s", dataList.size());
			}
			
			if ("true".equalsIgnoreCase(herenow)) {
				// HereNow part
				params.clear();
				FoursquareDiscoverHereNowJob mFoursquareDiscoverHereNowJob = new FoursquareDiscoverHereNowJob();
				mFoursquareDiscoverHereNowJob.setReqParams(params);
				mFoursquareDiscoverHereNowJob.setPoiList(dataList);
				dataList = new LinkedList<Object>();//dataList.clear();
				dataList.addAll( (LinkedList<Object>)mFoursquareDiscoverHereNowJob.doJobWithResult() );
				
				// remove items which doesn't have any hereNow in it!!!
				try {
					PoiModelFoursquare fsqPoi = null;
					LinkedList<Object> dataListFiltered = new LinkedList<Object>();
					for (Object obj:dataList) {
						fsqPoi = (PoiModelFoursquare)obj;
						
						//if (fsqPoi.herenow==null || fsqPoi.herenow.size()==0) dataList.remove(obj);
						if (fsqPoi.herenow!=null && fsqPoi.herenow.size()>0) dataListFiltered.add(obj);
					}
					Logger.info("dataList.size(): %s | dataListFiltered.size(): %s", dataList.size(), dataListFiltered.size());
					dataList = new LinkedList<Object>();//dataList.clear();
					dataList.addAll(dataListFiltered);
				} catch (Exception ex) {
					Logger.warn("exception while filtering out hereNow : %s", ex.toString());
				}
			}
			else {
				Logger.info("herenow param is NOT set true, skip loading hereNow!!! herenow: %s", herenow);
			}
			
			response.status = Http.StatusCode.OK;
	        responseMeta.code = response.status;
	        responseModel.meta = responseMeta;
	        responseModel.data = dataList;
	        
	        renderJSON( LocoUtils.getGson().toJson(responseModel) );
		}
    	catch (Exception ex) {
    	
    		responseMeta.code = Http.StatusCode.INTERNAL_ERROR;
    		gotError(responseMeta, ex);
	        //renderJSON(responseModel);
    	}
	}
	
	/*
	 * get&prepare FSQ locations  - Disocver
	 * 
	 * GET 	/api/locations/discover/geo:{lat},{lng}/?limit=&appid=&radius=
	 */
	public static void trendingFsqLocations(
			String lat, String lng
			) {
		
		String appid = params._contains(PARAM_APPID) ? params.get(PARAM_APPID) : "";
		String limit = params._contains(PARAM_LIMIT) ? params.get(PARAM_LIMIT) : "";
		limit = verifyRecordLimit(limit);
		String radius = params._contains(PARAM_RADIUS) ? params.get(PARAM_RADIUS) : "";
		radius = verifyRadius(radius);
		String herenow = params._contains(PARAM_HERENOW) ? params.get(PARAM_HERENOW) : "true";// by default, thats true!
		String query = params._contains(PARAM_QUERY) ? params.get(PARAM_QUERY) : "";
		
		Logger.info("PARAMS -> appid:%s ; lat,lng:%s,%s ; radius:%s ; limit:%s ; herenow:%s ; query:%s", appid, lat, lng, radius, limit, herenow, query);
		
		// using Async jobs
		ResponseModel responseModel = new ResponseModel();
		ResponseMeta responseMeta = new ResponseMeta();
		LinkedList<Object> dataList = new LinkedList<Object>();
		
		HashMap params = new HashMap(); 
		String cacheKey = CACHE_KEYPREFIX_TRENDING+"geo:"+lat+","+lng;
		if (!StringUtils.isEmpty(limit)) cacheKey+="|"+limit;
		if (!StringUtils.isEmpty(query)) cacheKey+="|"+query;
		
		try { 
			
			//dataList = (LinkedList<Object>) Cache.get(cacheKey);
			dataList = Cache.get(cacheKey, LinkedList.class);
			if (dataList==null) {
			
				dataList = new LinkedList<Object>();
				
				params.clear();
				if (!StringUtils.isEmpty(lat)&&!StringUtils.isEmpty(lng)) params.put("ll", lat+","+lng);
				if (!StringUtils.isEmpty(limit)) params.put(PARAM_LIMIT, limit);
				
				if (!StringUtils.isEmpty(radius)) params.put(PARAM_RADIUS, radius);
				/*params.put(PARAM_RADIUS,
						!StringUtils.isEmpty(radius)?
								radius
								:Play.configuration.getProperty("fsqdiscovery.trending.API_LOCO_SEARCHDISTANCE")
						);*/
				if (!StringUtils.isEmpty(query)) params.put(PARAM_QUERY, query);
				
				FoursquareTrendingPoiJob mFoursquareTrendingPoiJob = new FoursquareTrendingPoiJob();
				mFoursquareTrendingPoiJob.setReqParams(params);
				dataList.addAll( (LinkedList<Object>)mFoursquareTrendingPoiJob.doJobWithResult() );
				
				//Logger.info("adding to cache!!! %s", dataList.size());
				//Cache.set(cacheKey, dataList, CACHE_TTL_TRENDING);
				if (dataList.size()>0) {
					Logger.info("adding to cache!!! %s", dataList.size());
					Cache.set(cacheKey, dataList, CACHE_TTL_TRENDING);	
				}
				else {
					Logger.info("NO NEED to cache, dataList.size(): 0");
					
					response.status = Http.StatusCode.OK;
			        responseMeta.code = response.status;
			        responseModel.meta = responseMeta;
			        responseModel.data = dataList;
			        
			        renderJSON( LocoUtils.getGson().toJson(responseModel) );
				}
			}
			else {
				Logger.info("Found in CACHE!!! %s", dataList.size());
			}
			
			if ("true".equalsIgnoreCase(herenow)) {
				// HereNow part
				params.clear();
				FoursquareDiscoverHereNowJob mFoursquareDiscoverHereNowJob = new FoursquareDiscoverHereNowJob();
				mFoursquareDiscoverHereNowJob.setReqParams(params);
				mFoursquareDiscoverHereNowJob.setPoiList(dataList);
				dataList = new LinkedList<Object>();//dataList.clear();
				dataList.addAll( (LinkedList<Object>)mFoursquareDiscoverHereNowJob.doJobWithResult() );
			}
			else {
				Logger.info("herenow param is NOT set true, skip loading hereNow!!! herenow: %s", herenow);
			}
			
			response.status = Http.StatusCode.OK;
	        responseMeta.code = response.status;
	        responseModel.meta = responseMeta;
	        responseModel.data = dataList;
	        
	        renderJSON( LocoUtils.getGson().toJson(responseModel) );
		}
    	catch (Exception ex) {
    	
    		responseMeta.code = Http.StatusCode.INTERNAL_ERROR;
    		gotError(responseMeta, ex);
	        //renderJSON(responseModel);
    	}
	}
	
	/*
	 * get&prepare HereNow using provided FSQ location IDS  - Discover HereNow
	 * 
# # REQUIRED params ?ids=<comma separated ids> / ids  are the id's of foursquare locations
# # optional params / example: limit=12
# example: http://localhost:9000/api/locations/herenow/?limit=10&ids=4a737bf8f964a52091dc1fe3,4b8aa3e1f964a520e77632e3
	 * GET 	/api/locations/herenow/?ids=<id1, id2>&limit=<max_number_of_records>&appid=<appid>
	 */
	public static void discoverHereNow() {
		
		String appid = params._contains(PARAM_APPID) ? params.get(PARAM_APPID) : "";
		String limit = params._contains(PARAM_LIMIT) ? params.get(PARAM_LIMIT) : "";
		limit = verifyRecordLimit(limit);
		String ids = params._contains(PARAM_IDS) ? params.get(PARAM_IDS) : "";
		
		//Logger.info("appid, limit, ids : %s, %s, %s \n", appid, limit, ids);
		Logger.info("PARAMS -> appid:%s ; limit:%s ; ids:%s", appid, limit, ids);
		
		// using Async jobs
		ResponseModel responseModel = new ResponseModel();
		ResponseMeta responseMeta = new ResponseMeta();
		LinkedList<Object> dataList = new LinkedList<Object>();
		
		HashMap params = new HashMap(); 
		
		try { 
			
			params.clear();
			//-if (!StringUtils.isEmpty(limit)) params.put(PARAM_LIMIT, limit);
			FoursquareDiscoverPoiJob mFoursquarePoiJob = new FoursquareDiscoverPoiJob();
			mFoursquarePoiJob.setIds(ids);
			mFoursquarePoiJob.setReqParams(params);
			dataList.addAll( (LinkedList<Object>)mFoursquarePoiJob.doJobWithResult() );
			
			
			// HereNow part
			params.clear();
			if (!StringUtils.isEmpty(limit)) params.put(PARAM_LIMIT, limit);
			FoursquareDiscoverHereNowJob mFoursquareDiscoverHereNowJob = new FoursquareDiscoverHereNowJob();
			mFoursquareDiscoverHereNowJob.setReqParams(params);
			mFoursquareDiscoverHereNowJob.setPoiList(dataList);
			dataList = new LinkedList<Object>();//dataList.clear();
			dataList.addAll( (LinkedList<Object>)mFoursquareDiscoverHereNowJob.doJobWithResult() );
			
			
			response.status = Http.StatusCode.OK;
	        responseMeta.code = response.status;
	        responseModel.meta = responseMeta;
	        responseModel.data = dataList;
	        
	        renderJSON( LocoUtils.getGson().toJson(responseModel) );
		}
    	catch (Exception ex) {
    	
    		responseMeta.code = Http.StatusCode.INTERNAL_ERROR;
    		gotError(responseMeta, ex);
	        //renderJSON(responseModel);
    	}
	}
}