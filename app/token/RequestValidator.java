package token;


import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import models.ApiRequestLog;
import models.ResponseMeta;
import models.ResponseModel;
import play.Logger;

import play.Play;
import play.modules.morphia.Model.MorphiaQuery;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.With;
import play.mvc.Http.Header;
import play.cache.Cache;
import play.i18n.Lang;
import play.i18n.Messages;
import utils.LocoUtils;

/***
 * 	Copyright (c) 2011-2012 WareNinja.com
 *  http://www.WareNinja.com - https://github.com/WareNinja
 *  	
 *  Author: yg@wareninja.com
 */

//@With(NoCookieFilter.class)
public class RequestValidator extends Controller
{
	
	public static final String PARAM_APPID = Play.configuration.getProperty("fsqdiscovery.apiparam.appid", "appid");
	
	static final String CACHE_KEYPREFIX_APPID = Play.configuration.getProperty("fsqdiscovery.cache.appid.keyprefix");
	static final String CACHE_TTL_APPID = Play.configuration.getProperty("fsqdiscovery.cache.appid.ttl");
	static List<String> appIds = null;
	
	@Before
	static void checkRequest() {
		
		String appIdParamValue = params._contains(PARAM_APPID)?params.get(PARAM_APPID):"empty-appid";
		
		Header acceptHeader = request.headers.containsKey("accept")?request.headers.get("accept"):null;
		
		Logger.info("Request headers : %s", request.headers);
		
		 //Request headers : 
		 //{cache-control=[max-age=0], connection=[keep-alive], host=[localhost:9000]
		 //, accept-language=[en-US,en;q=0.8], accept=[text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8]
		// , user-agent=[Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.45 Safari/535.19]
		//, accept-encoding=[gzip,deflate,sdch], accept-charset=[ISO-8859-1,utf-8;q=0.7,*;q=0.3]}

		ApiRequestLog apiRequestLog = new ApiRequestLog();
		try {
			
			if (appIds==null) {
				//appIds = new LinkedList<String>();
				String appIdsConfig = Play.configuration.getProperty("fsqdiscovery.appids", null);
				if (appIdsConfig!=null) appIds = Arrays.asList(appIdsConfig.split(","));
			}
			
			apiRequestLog.oid = ""+System.currentTimeMillis();//+"_"+ UUID.randomUUID();
			//apiRequestLog.requestHeaders.putAll( request.headers );
			apiRequestLog.appId = appIdParamValue;
			apiRequestLog.requestTime = LocoUtils.getFormattedDate();
			apiRequestLog.clientIp = request.remoteAddress;
			apiRequestLog.userAgent = request.headers.containsKey("user-agent")?request.headers.get("user-agent").value():"<empty>";
			apiRequestLog.requestUrl = request.url;
			
			Logger.info("apiRequestLog : %s", apiRequestLog.toJsonString());
			
			apiRequestLog.save();// save to mongo
		}
		catch (Exception ex) {
			Logger.warn("exception while tracking ApiRequestLog : %s", ex.toString());
		}
		
		ResponseModel responseModel = new ResponseModel();
		ResponseMeta responseMeta = new ResponseMeta();
		
		if (acceptHeader!=null
				&& (!acceptHeader.value().contains("application/json") 
						&& !acceptHeader.value().contains("*/*")
						&& !acceptHeader.value().contains("*.*")
						)
				) {
			response.status = Http.StatusCode.BAD_REQUEST;
			
			responseMeta.code = Http.StatusCode.BAD_REQUEST;
            responseMeta.errorType = Messages.get("http.statuscode."+response.status);//"BAD_REQUEST";
            responseMeta.errorDetail = "application/json support is required!!!";//Messages.get("Http.StatusCode.BAD_REQUEST");
            responseModel.meta = responseMeta;

            //-renderJSON(responseModel);
            renderJSON( LocoUtils.getGson().toJson(responseModel) );
		}
		
		Logger.info("request appId: %s , pre-defined appIds: %s", apiRequestLog.appId, appIds);
		if (appIds!=null && !appIds.contains(apiRequestLog.appId)) {
			
			response.status = Http.StatusCode.BAD_REQUEST;
			
			responseMeta.code = Http.StatusCode.BAD_REQUEST;
            responseMeta.errorType = Messages.get("http.statuscode."+response.status);//"BAD_REQUEST";
            responseMeta.errorDetail = "appid parameter is required in every call!!!";
            responseModel.meta = responseMeta;
            
            renderJSON( LocoUtils.getGson().toJson(responseModel) );
		}
		
	}
}
