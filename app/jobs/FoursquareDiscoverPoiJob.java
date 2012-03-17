package jobs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import models.BaseLocations;
import models.PoiModelFoursquare;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import play.Logger;
import play.Play;
import play.cache.Cache;
import play.jobs.Job;
import play.libs.WS;
import play.libs.F.Promise;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;
import play.mvc.Http;
import utils.LocoUtils;


/***
 * 	Copyright (c) 2011-2012 WareNinja.com
 *  http://www.WareNinja.com - https://github.com/WareNinja
 *  	
 *  Author: yg@wareninja.com / twitter: @WareNinja
 */

public class FoursquareDiscoverPoiJob extends BaseJob {

	static final String CACHE_KEYPREFIX_SINGLEPOI = Play.configuration.getProperty("fsqdiscovery.cache.single-poi.keyprefix", "single_poi_");
	static final String CACHE_TTL = Play.configuration.getProperty("fsqdiscovery.cache.single-poi.ttl", "2h");
	
	private String baseUrl = Play.configuration.getProperty("fsqdiscovery.discovery.API_FOURSQUARE_BASE_URL");
	private String poiSearch = Play.configuration.getProperty("fsqdiscovery.discovery.API_FOURSQUARE_POI_SEARCH");
	private String singlePoiSearch = Play.configuration.getProperty("fsqdiscovery.discovery.API_FOURSQUARE_SINGLE_POI_SEARCH");
	private HashMap params = new HashMap();
	private LinkedList<String> idsList = new LinkedList<String>();
	public FoursquareDiscoverPoiJob() {
		baseInit();
	}
	public void setReqParams(HashMap params) {
		this.params.putAll( params );
	}
	private void baseInit() {
		// with initial params
		params.put( "client_id", Play.configuration.getProperty("fsqdiscovery.discovery.FSQ_APP_KEY") );
		params.put( "client_secret", Play.configuration.getProperty("fsqdiscovery.discovery.FSQ_APP_SECRET") );
		params.put("v", Play.configuration.getProperty("fsqdiscovery.discovery.FSQ_APP_VERSION") );
		params.put("intent", "browse" );
	}
	
	public void setIdsList(List<String> reqIdsList) {
		idsList.addAll(reqIdsList);
	}
	public void setIds(String ids) {
		// e.g. ids=4a737bf8f964a52091dc1fe3,4b8aa3e1f964a520e77632e3
		// TODO: parse into list
		List<String> reqIds = Arrays.asList(ids.split(","));
		idsList.addAll(reqIds);
	}
	
	@Override
	public Object doJobWithResult() throws Exception {
		
		WSRequest req = null;
		HttpResponse resp = null;
		JsonObject jsonResp = null;
		Gson gson = new GsonBuilder().create();
		
		LinkedList<Object> dataList = new LinkedList<Object>();
		PoiModelFoursquare fsqPoi = null;
		
		if (idsList.size() > 0) {// check & load each fsq-POI
			
			for (String fsqId:idsList) {
				
				try {
					
					//fsqPoi = (PoiModelFoursquare)Cache.get(CACHE_KEYPREFIX_SINGLEPOI+fsqId);
					fsqPoi = Cache.get(CACHE_KEYPREFIX_SINGLEPOI+fsqId, PoiModelFoursquare.class);
					if (fsqPoi!=null) {
						
						Logger.info("Found in CACHE! : %s", fsqPoi);
						dataList.add(fsqPoi);
						continue;
					}
		        	
		        	// https://developer.foursquare.com/docs/venues/search
					// https://api.foursquare.com/v2/venues/search
					req = WS.url(
							baseUrl + singlePoiSearch.replace("/%s", "/"+fsqId )
							+ "?" + LocoUtils.buildUrlParams(params)
							);
			        Logger.info("req.url : %s", req.url);
			    	
			        resp = req.get();
		        	
			        jsonResp = resp.getJson().getAsJsonObject();
			        Logger.info("jsonResp : %s", jsonResp);
		
			        JsonObject respPart = jsonResp.getAsJsonObject("response");
			        JsonObject venue = respPart.getAsJsonObject("venue");
			        
			        
		        	Logger.info("venue : %s", venue);
		        	
		        	fsqPoi = gson.fromJson(venue, PoiModelFoursquare.class);
		        	if (fsqPoi!=null) fsqPoi.updateCategoryIcons();
		        	Logger.info("fsqPoi : %s", fsqPoi);
		        	
		        	if (venue.has("hereNow")) {
		        		fsqPoi.stats.herenowCount = venue.get("hereNow").getAsJsonObject().get("count").getAsInt();
		        	}
		        	
		        	fsqPoi.locType = BaseLocations.LocType.FSQ_POI;
		        	
		        	if (fsqPoi!=null && !StringUtils.isEmpty(fsqPoi.oid)) {
				        try {
				        	Cache.set(CACHE_KEYPREFIX_SINGLEPOI+fsqPoi.oid, fsqPoi, CACHE_TTL);
				        	
				        	// TODO: using mongoDB is temporary, we should make this parametric enable/disable
				        	//if (fsqPoi!=null && fsqPoi.location!=null) {
				        	if (fsqPoi.location!=null) {
				        		fsqPoi.lat = fsqPoi.location.lat;
				        		fsqPoi.lng = fsqPoi.location.lng;
				        		fsqPoi.updateLatlng();
				        	}
				        	fsqPoi.save();
				        }
			        	catch (Exception ex) {
			        		Logger.warn("Exception while persisting %s | %s", fsqPoi, ex.toString());
			        	}
			        	
			        	dataList.add(fsqPoi);
		        	}
			        
		    	}
		    	catch (Exception ex) {
		    		
		    		Logger.error("exception : %s", ex.toString());
		    	}
			}
			// TODO: calculate distance and sort!
	        //-dataList = LocoUtils.calculateDistance(lat, lng, dataList);
		}
		else {// load complete set from 4sq
		    
	        try {
	        	
	        	// https://developer.foursquare.com/docs/venues/search
				// https://api.foursquare.com/v2/venues/search
				req = WS.url(
						baseUrl + poiSearch
						+ "?" + LocoUtils.buildUrlParams(params)
						);
		        Logger.info("req.url : %s", req.url);
		    	
		        resp = req.get();
	        	
		    	//Gson gson = new GsonBuilder().create();
		        
		        jsonResp = resp.getJson().getAsJsonObject();
		        Logger.info("jsonResp : %s", jsonResp);
	
		        JsonObject respPart = jsonResp.getAsJsonObject("response");
		        JsonArray venues = respPart.getAsJsonArray("venues");
		        JsonObject venue;
		        //PoiModelFoursquare fsqPoi = null;
		        for (int i=0; i<venues.size(); i++) {
		        	venue = venues.get(i).getAsJsonObject();
		        	Logger.info("venue #%s : %s", i, venue);
		        	
		        	fsqPoi = gson.fromJson(venue, PoiModelFoursquare.class);
		        	if (fsqPoi!=null) fsqPoi.updateCategoryIcons();
		        	Logger.info("fsqPoi #%s : %s", i, fsqPoi);
		        	
		        	if (venue.has("hereNow")) {
		        		fsqPoi.stats.herenowCount = venue.get("hereNow").getAsJsonObject().get("count").getAsInt();
		        	}
		        	
		        	fsqPoi.locType = BaseLocations.LocType.FSQ_POI;
		        	
		        	if (fsqPoi!=null && !StringUtils.isEmpty(fsqPoi.oid)) {
				        try {	
				        	Cache.set(CACHE_KEYPREFIX_SINGLEPOI+fsqPoi.oid, fsqPoi, CACHE_TTL);
				        	
				        	// TODO: using mongoDB is temporary, we should make this parametric enable/disable
				        	//if (fsqPoi!=null && fsqPoi.location!=null) {
				        	if (fsqPoi.location!=null) {
				        		fsqPoi.lat = fsqPoi.location.lat;
				        		fsqPoi.lng = fsqPoi.location.lng;
				        		fsqPoi.updateLatlng();
				        	}
				        	fsqPoi.save();
				        }
			        	catch (Exception ex) {
			        		Logger.warn("Exception while persisting %s | %s", fsqPoi, ex.toString());
			        	}
			        	
			        	dataList.add(fsqPoi);
		        	}
		        }  
		        
		        // TODO: calculate distance and sort!
		        //-dataList = LocoUtils.calculateDistance(lat, lng, dataList);
	    	}
	    	catch (Exception ex) {
	    		
	    		Logger.error("exception : %s", ex.toString());
	    	}
		}
		
        return dataList;
	}	
}
