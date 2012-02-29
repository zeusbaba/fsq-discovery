package controllers;

import play.*;
import play.i18n.Messages;
import play.mvc.*;
import token.NoCookieFilter;
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
 *  Author: yg@wareninja.com
 */

@With(NoCookieFilter.class)
//@With({RequestValidator.class,NoCookieFilter.class})
public class ApplicationBaseController extends Controller {

    
    @Finally
    static void logResponse() {
        //Logger.info("Response contentType : " + response.contentType);
        //Logger.info("Response contains : " + response.out);
    }
 
    
    public static final String RECORDLIMIT_DEFAULT = Play.configuration.getProperty("fsqdiscovery.discovery.API_LOADITEMS_LIMIT_DEFAULT");
    public static final String RECORDLIMIT_MAX = Play.configuration.getProperty("fsqdiscovery.discovery.API_LOADITEMS_LIMIT_MAX");
    protected static String verifyRecordLimit(String limit) {
    	try {
			if (StringUtils.isEmpty(limit) || !StringUtils.isNumeric(limit)) {
				limit = RECORDLIMIT_DEFAULT;
			}
			else {
				//if (Integer.parseInt(limit) > Integer.parseInt(RECORDLIMIT_MAX)) limit = RECORDLIMIT_DEFAULT;
				if (LocoUtils.stringToInteger(limit) > LocoUtils.stringToInteger(RECORDLIMIT_MAX)) limit = RECORDLIMIT_DEFAULT;
			}
		}
		catch (Exception ex) {
			limit = RECORDLIMIT_DEFAULT;
		}
    	
    	return limit;
    }
    protected static void gotError(ResponseMeta responseMeta, Exception e) {
        
    	ResponseModel responseModel = new ResponseModel();
		
		if (responseMeta.code==null) responseMeta.code = Http.StatusCode.INTERNAL_ERROR;
    	response.status = responseMeta.code;
        if (StringUtils.isEmpty(responseMeta.errorType)) responseMeta.errorType = Messages.get("http.statuscode." + response.status);
        if (StringUtils.isEmpty(responseMeta.errorDetail)) responseMeta.errorDetail = Messages.get("com.wareninja.loco.errortype.other", e.getMessage());
        responseModel.meta = responseMeta;
        responseModel.data = null;//new LinkedList<BaseModel>();
        
        renderJSON(responseModel);
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
        
        renderJSON(responseModel);
	}
}