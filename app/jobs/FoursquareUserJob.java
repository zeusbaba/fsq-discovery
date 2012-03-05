package jobs;

import java.util.HashMap;
import java.util.LinkedList;

import models.PoiHerenowModelFoursquare;
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

public class FoursquareUserJob extends BaseJob {

	private String baseUrl = Play.configuration.getProperty("fsqdiscovery.discovery.API_FOURSQUARE_BASE_URL");
	//private String poiSearch = Play.configuration.getProperty("fsqdiscovery.discovery.API_FOURSQUARE_POI_SEARCH");
	private String herenowSearch = Play.configuration.getProperty("fsqdiscovery.discovery.API_FOURSQUARE_POI_HERENOW");
	private HashMap params = new HashMap();
	private LinkedList<PoiModelFoursquare> poiList = new LinkedList<PoiModelFoursquare>();
	public FoursquareUserJob() {
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
        PoiHerenowModelFoursquare hereNow = new PoiHerenowModelFoursquare();
        HttpResponse resp = null;
        WSRequest req = null;
        for (PoiModelFoursquare poi : poiList) {
        	
	        try {
	        	req = WS.url(
	    				baseUrl + herenowSearch.replace("/%s/", "/"+poi.oid+"/" )
	    				+ "?" + LocoUtils.buildUrlParams(params)
	    				);
	        	Logger.info("req.url : %s", req.url);
	        	
	        	resp = req.get();
	        	
		    	Gson gson = new GsonBuilder().create();
		        
		        JsonObject jsonResp = resp.getJson().getAsJsonObject();
		        Logger.info("jsonResp : %s", jsonResp);
	
		        poi.herenow = new LinkedList<PoiHerenowModelFoursquare>();
		        
		        JsonObject respPart = jsonResp.getAsJsonObject("response");
		        respPart = respPart.getAsJsonObject("hereNow");
		        JsonArray items = respPart.getAsJsonArray("items");
		        JsonObject item;
		        for (int i=0; i<items.size(); i++) {
		        	item = items.get(i).getAsJsonObject();
		        	Logger.info("item #%s : %s", i, item);
		        	
		        	hereNow = new PoiHerenowModelFoursquare();
		        	//-hereNow = gson.fromJson(item, PoiHerenowModelFoursquare.class);
		        	hereNow.id = item.has("id")?item.get("id").getAsString():"";
		        	hereNow.createdAt = item.has("createdAt")?item.get("createdAt").getAsLong():0L;
		        	hereNow.type = item.has("type")?item.get("type").getAsString():"";
		        	hereNow.timeZone = item.has("timeZone")?item.get("timeZone").getAsString():"";
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
		        }   
	    	}
	    	catch (Exception ex) {
	    		
	    		Logger.error("exception : %s", ex.toString());
	    	}
	        
	        dataList.add(poi);
        }
		
        return dataList;
	}	
}
