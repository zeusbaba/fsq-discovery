package jobs;

import java.util.HashMap;
import java.util.LinkedList;

import models.PoiModelFoursquare;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import play.Logger;
import play.Play;
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
 *  Author: yg@wareninja.com
 */

public class FoursquarePoiJob extends BaseJob {

	private String baseUrl = Play.configuration.getProperty("fsqdiscovery.discovery.API_FOURSQUARE_BASE_URL");
	private String poiSearch = Play.configuration.getProperty("fsqdiscovery.discovery.API_FOURSQUARE_POI_SEARCH");
	private HashMap params = new HashMap();
	public FoursquarePoiJob() {
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
	}
	
	@Override
	public Object doJobWithResult() throws Exception {
		
		WSRequest req = WS.url(
				baseUrl + poiSearch
				+ "?" + LocoUtils.buildUrlParams(params)
				);
		// testing external API
        /*WSRequest req = WS.url(
        		//"https://graph.facebook.com/100001789213579"
        		baseUrl + poiSearch
        		+ "?ll="+lat+","+lng//51.2116316666667,6.801265"
        		+ "&limit="+limit
        		+ "&client_id="+Play.configuration.getProperty("fsqdiscovery.discovery.FSQ_APP_KEY")
        		+ "&client_secret="+Play.configuration.getProperty("fsqdiscovery.discovery.FSQ_APP_SECRET")
        		+ "&v=20111113"
        		);*/
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
	        	Logger.info("fsqPoi #%s : %s", i, fsqPoi);
	        	
	        	if (venue.has("hereNow")) {
	        		fsqPoi.stats.herenowCount = venue.get("hereNow").getAsJsonObject().get("count").getAsInt();
	        	}
	        	
	        	dataList.add(fsqPoi);
	        }   
    	}
    	catch (Exception ex) {
    		
    		Logger.error("exception : %s", ex.toString());
    	}
		
        return dataList;
	}	
}
