package controllers;

import play.*;
import play.i18n.Messages;
import play.mvc.*;
import token.NoCookieFilter;
import token.RequestValidator;
import utils.LocoUtils;

import java.util.*;

import org.apache.commons.lang.StringUtils;

import models.BaseModel;
import models.ResponseMeta;
import models.ResponseModel;

/***
 * 	Copyright (c) 2011-2012 WareNinja.com
 *  http://www.WareNinja.com - https://github.com/WareNinja
 *  	
 *  Author: yg@wareninja.com / twitter: @WareNinja
 */

//@With(NoCookieFilter.class)
@With({RequestValidator.class,NoCookieFilter.class})
public class ApplicationBaseController extends Controller {

   /*// just for testing
    @Finally
    static void logResponse() {
        //Logger.info("Response contentType : " + response.contentType);
        //Logger.info("Response contains : " + response.out);
    }
    */
    
	// param names used for API calls
	public static final String PARAM_APPID = Play.configuration.getProperty("fsqdiscovery.apiparam.appid");
	public static final String PARAM_LIMIT = Play.configuration.getProperty("fsqdiscovery.apiparam.limit");
	public static final String PARAM_RADIUS = Play.configuration.getProperty("fsqdiscovery.apiparam.radius");
	public static final String PARAM_HERENOW = Play.configuration.getProperty("fsqdiscovery.apiparam.herenow");
	public static final String PARAM_IDS = Play.configuration.getProperty("fsqdiscovery.apiparam.ids");
	public static final String PARAM_QUERY = Play.configuration.getProperty("fsqdiscovery.apiparam.query");
	
	
    public static final String RECORDLIMIT_DEFAULT = Play.configuration.getProperty("fsqdiscovery.discovery.API_LOADITEMS_LIMIT_DEFAULT");
    public static final String RECORDLIMIT_MAX = Play.configuration.getProperty("fsqdiscovery.discovery.API_LOADITEMS_LIMIT_MAX");
    protected static String verifyRecordLimit(String limit) {
    	try {
			if (StringUtils.isEmpty(limit) || !StringUtils.isNumeric(limit)) {
				limit = RECORDLIMIT_DEFAULT;
			}
			else {
				if (LocoUtils.stringToInteger(limit) > LocoUtils.stringToInteger(RECORDLIMIT_MAX)) limit = RECORDLIMIT_DEFAULT;
			}
		}
		catch (Exception ex) {
			limit = RECORDLIMIT_DEFAULT;
		}
    	
    	return limit;
    }
    public static final String RADIUS_DEFAULT = Play.configuration.getProperty("fsqdiscovery.discovery.API_LOADITEMS_RADIUS_DEFAULT", "1000");
    public static final String RADIUS_MAX = Play.configuration.getProperty("fsqdiscovery.discovery.API_LOADITEMS_RADIUS_MAX", "5000");
    protected static String verifyRadius(String radius) {
    	try {
			if (StringUtils.isEmpty(radius) || !StringUtils.isNumeric(radius)) {
				radius = RADIUS_DEFAULT;
			}
			else {
				if (LocoUtils.stringToInteger(radius) > LocoUtils.stringToInteger(RADIUS_MAX)) radius = RADIUS_DEFAULT;
			}
		}
		catch (Exception ex) {
			radius = RADIUS_DEFAULT;
		}
    	
    	return radius;
    }
    
    
    protected static void gotError(ResponseMeta responseMeta, Exception e) {
        
    	ResponseModel responseModel = new ResponseModel();
		
		if (responseMeta.code==null) responseMeta.code = Http.StatusCode.INTERNAL_ERROR;
    	response.status = responseMeta.code;
        if (StringUtils.isEmpty(responseMeta.errorType)) responseMeta.errorType = Messages.get("http.statuscode." + response.status);
        if (StringUtils.isEmpty(responseMeta.errorDetail)) responseMeta.errorDetail = Messages.get("com.wareninja.loco.errortype.other", e.getMessage());
        responseModel.meta = responseMeta;
        responseModel.data = null;//new LinkedList<BaseModel>();
        
        //-renderJSON(responseModel);
        renderJSON( LocoUtils.getGson().toJson(responseModel) );
    }
    
    public static void invalidCall() {
		
		Logger.warn("request path: %s , args: %s , remoteAddress: %s", request.path, request.args, request.remoteAddress);
		Logger.warn("request headers: %s", request.headers);
		
		ResponseModel responseModel = new ResponseModel();
		ResponseMeta responseMeta = new ResponseMeta();
		
		response.status = Http.StatusCode.FORBIDDEN;
        responseMeta.code = response.status;
        responseMeta.errorType = Messages.get("http.statuscode."+response.status);
        responseMeta.errorDetail = Messages.get("com.wareninja.loco.errortype.endpoint_error");
        responseModel.meta = responseMeta;
        responseModel.data = null;
        
        //-renderJSON(responseModel);
        renderJSON( LocoUtils.getGson().toJson(responseModel) );
	}
}