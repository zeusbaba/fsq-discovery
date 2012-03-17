package jobs;

import java.util.HashMap;
import java.util.LinkedList;

import models.HereNow;
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
 *  Author: yg@wareninja.com / twitter: @Wareninja
 */

public class FoursquareDiscoverHereNowJob extends BaseJob {

	static final String CACHE_KEYPREFIX_HERENOW = Play.configuration.getProperty("fsqdiscovery.cache.herenow.keyprefix", "single_poi_herenow_");
	static final String CACHE_TTL_HERENOW = Play.configuration.getProperty("fsqdiscovery.cache.herenow.ttl", "30mn");
	
	private String baseUrl = Play.configuration.getProperty("fsqdiscovery.discovery.API_FOURSQUARE_BASE_URL");
	//private String poiSearch = Play.configuration.getProperty("fsqdiscovery.discovery.API_FOURSQUARE_POI_SEARCH");
	private String herenowSearch = Play.configuration.getProperty("fsqdiscovery.discovery.API_FOURSQUARE_POI_HERENOW");
	private HashMap params = new HashMap();
	private LinkedList<PoiModelFoursquare> poiList = new LinkedList<PoiModelFoursquare>();
	public FoursquareDiscoverHereNowJob() {
		baseInit();
	}
	public void setReqParams(HashMap params) {
		this.params.putAll( params );
	}
	public void setPoiList(LinkedList<Object> dataList) {
		
		for (Object obj : dataList) {
			poiList.add( (PoiModelFoursquare)obj );
		}
	}
	public void setPoi(Object obj) {
		poiList.add( (PoiModelFoursquare)obj );
	}
	
	private void baseInit() {
		// with initial params
		params.put( "client_id", Play.configuration.getProperty("fsqdiscovery.discovery.FSQ_APP_KEY") );
		params.put( "client_secret", Play.configuration.getProperty("fsqdiscovery.discovery.FSQ_APP_SECRET") );
		params.put("v", Play.configuration.getProperty("fsqdiscovery.discovery.FSQ_APP_VERSION") );
		
		params.put("oauth_token", Play.configuration.getProperty("fsqdiscovery.discovery.FSQ_TOKENS") );
	}
	
	@Override
	public Object doJobWithResult() throws Exception {
		
        LinkedList<Object> dataList = new LinkedList<Object>();
        HereNow hereNow = new HereNow();
        HttpResponse resp = null;
        WSRequest req = null;
        for (PoiModelFoursquare poi : poiList) {
        	
	        try {
	        	
	        	//LinkedList<HereNow> herenow = (LinkedList<HereNow>)Cache.get(CACHE_KEYPREFIX_HERENOW+poi.oid);
	        	LinkedList<HereNow> herenow = Cache.get(CACHE_KEYPREFIX_HERENOW+poi.oid, LinkedList.class);
				if (herenow!=null) {
					poi.herenow = new LinkedList<HereNow>();
					poi.herenow.addAll( herenow );
					//-poiList.set(p, poi);
					Logger.info("Found in CACHE! # %s", herenow.size());
					
					dataList.add(poi);
					continue;
				}
	        	
	        	// https://developer.foursquare.com/docs/venues/herenow
				// https://api.foursquare.com/v2/venues/VENUE_ID/herenow
	        	req = WS.url(
	    				baseUrl + herenowSearch.replace("/%s/", "/"+poi.oid+"/" )
	    				+ "?" + LocoUtils.buildUrlParams(params)
	    				);
	        	Logger.info("req.url : %s", req.url);
	        	
	        	resp = req.get();
	        	
		    	Gson gson = new GsonBuilder().create();
		        
		        JsonObject jsonResp = resp.getJson().getAsJsonObject();
		        Logger.info("jsonResp : %s", jsonResp);
	
		        poi.herenow = new LinkedList<HereNow>();
		        
		        JsonObject respPart = jsonResp.getAsJsonObject("response");
		        respPart = respPart.getAsJsonObject("hereNow");
		        JsonArray items = respPart.getAsJsonArray("items");
		        JsonObject item;
		        for (int i=0; i<items.size(); i++) {
		        	item = items.get(i).getAsJsonObject();
		        	Logger.info("item #%s : %s", i, item);
		        	
		        	hereNow = new HereNow();
		        	//-hereNow = gson.fromJson(item, PoiHerenowModelFoursquare.class);
		        	hereNow.oid = item.has("id")?item.get("id").getAsString():"";
		        	
		        	hereNow.type = item.has("type")?item.get("type").getAsString():"";
		        	hereNow.timeZone = item.has("timeZone")?item.get("timeZone").getAsString():"";
		        	//-hereNow.createdAt = item.has("createdAt")?item.get("createdAt").getAsLong():0L;
		        	//-if (item.has("createdAt")) hereNow.setCreatedAt(item.get("createdAt").getAsLong());
		        	if (item.has("createdAt")) hereNow.setCreatedAt(item.get("createdAt").getAsLong(), hereNow.timeZone);
		        	 
		        	if (item.has("user")) {
		        		item = item.getAsJsonObject("user");
		        		hereNow.user_id = item.has("id")?item.get("id").getAsString():"";
		        		hereNow.user_firstName = item.has("firstName")?item.get("firstName").getAsString():"";
		        		hereNow.user_photo = item.has("photo")?item.get("photo").getAsString():"";
		        		hereNow.user_gender = item.has("gender")?item.get("gender").getAsString():"";
		        		hereNow.user_homeCity = item.has("homeCity")?item.get("homeCity").getAsString():"";
		        		hereNow.user_canonicalUrl = item.has("canonicalUrl")?item.get("canonicalUrl").getAsString():"";
		        		
		        		if (!StringUtils.isEmpty(hereNow.user_photo)) {
		        			hereNow.user_photo_hres = hereNow.user_photo.replace("/userpix_thumbs/", "/userpix/");
		        		}
		        	}
		        	
		        	Logger.info("hereNow #%s : %s", i, hereNow);
		        	
		        	poi.herenow.add(hereNow);
		        	
		        	try {
		        		// TODO: store only if it doesnt exists!
			        	//hereNow.oid = poi.oid + "_" + hereNow.oid;
			        	hereNow.poiId = poi.oid;
			        	hereNow.lat = poi.lat;
			        	hereNow.lng = poi.lng;
			        	hereNow.updateLatlng();
			        	hereNow.save();
		        	}
		        	catch (Exception ex) {
		        		Logger.warn("Exception while persisting %s | %s", hereNow, ex.toString());
		        	}
		        }
		        
		        if (poi.herenow.size()>0) {
			        Cache.set(CACHE_KEYPREFIX_HERENOW+poi.oid, poi.herenow, CACHE_TTL_HERENOW);
			        Logger.info("CACHEd hereNow.size: %s", poi.herenow.size());
		        }
		        if (poi.stats!=null) poi.stats.herenowCount = poi.herenow.size();
		        
		        dataList.add(poi);
	    	}
	    	catch (Exception ex) {
	    		
	    		Logger.error("exception : %s", ex.toString());
	    	}
	        
	        
        }
		
        return dataList;
	}	
}
