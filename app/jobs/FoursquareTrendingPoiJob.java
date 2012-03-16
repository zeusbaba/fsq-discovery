package jobs;

import java.util.HashMap;
import java.util.LinkedList;

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

public class FoursquareTrendingPoiJob extends BaseJob {

	static final String CACHE_KEYPREFIX_SINGLEPOI = Play.configuration.getProperty("fsqdiscovery.cache.single-poi.keyprefix");
	static final String CACHE_TTL = Play.configuration.getProperty("fsqdiscovery.cache.nearby-poi.ttl");
	
	private String baseUrl = Play.configuration.getProperty("fsqdiscovery.discovery.API_FOURSQUARE_BASE_URL");
	private String trendingSearch = Play.configuration.getProperty("fsqdiscovery.discovery.API_FOURSQUARE_POI_TRENDING");
	private HashMap params = new HashMap();
	public FoursquareTrendingPoiJob() {
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
		//params.put("intent", "browse" );
	}
	
	@Override
	public Object doJobWithResult() throws Exception {
		
		// https://developer.foursquare.com/docs/venues/trending
		// https://api.foursquare.com/v2/venues/trending
		WSRequest req = WS.url(
				baseUrl + trendingSearch
				+ "?" + LocoUtils.buildUrlParams(params)
				);
        Logger.info("req.url : %s", req.url);
    	
        HttpResponse resp = req.get();
        LinkedList<Object> dataList = new LinkedList<Object>();
        
        try {
	    	Gson gson = new GsonBuilder().create();//new GsonBuilder().setPrettyPrinting().create();
	        
	        
	        JsonObject jsonResp = resp.getJson().getAsJsonObject();
	        Logger.info("jsonResp : %s", jsonResp);

	        JsonObject respPart = jsonResp.getAsJsonObject("response");
	        JsonArray venues = respPart.getAsJsonArray("venues");
	        JsonObject venue;
	        PoiModelFoursquare fsqPoi = null;
	        for (int i=0; i<venues.size(); i++) {
	        	venue = venues.get(i).getAsJsonObject();
	        	Logger.info("venue #%s : %s", i, venue);
	        	
	        	fsqPoi = gson.fromJson(venue, PoiModelFoursquare.class);
	        	if (fsqPoi!=null) fsqPoi.updateCategoryIcons();
	        	Logger.info("fsqPoi #%s : %s", i, fsqPoi);
	        	
	        	if (venue.has("hereNow")) {
	        		fsqPoi.stats.herenowCount = venue.get("hereNow").getAsJsonObject().get("count").getAsInt();
	        	}
	        	
	        	fsqPoi.locType = BaseLocations.LocType.FSQ_TRENDING;
	        	
	        	if (fsqPoi!=null && !StringUtils.isEmpty(fsqPoi.oid)) {
			        try {	
			        	// TODO: store only if it doesnt exists!
			        	if (fsqPoi!=null && fsqPoi.location!=null) {
			        		fsqPoi.lat = fsqPoi.location.lat;
			        		fsqPoi.lng = fsqPoi.location.lng;
			        		fsqPoi.updateLatlng();
			        	}
			        	fsqPoi.save();
			        	
			        	Cache.set(CACHE_KEYPREFIX_SINGLEPOI+fsqPoi.oid, fsqPoi, CACHE_TTL);
			        }
		        	catch (Exception ex) {
		        		Logger.warn("Exception while persisting %s | %s", fsqPoi, ex.toString());
		        	}
		        	
		        	dataList.add(fsqPoi);
	        	}
	        }   
    	}
    	catch (Exception ex) {
    		
    		Logger.error("exception : %s", ex.toString());
    	}
		
        return dataList;
	}	
}
