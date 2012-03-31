package controllers;

import play.*;
import play.cache.Cache;
import play.libs.F.Promise;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;
import play.modules.morphia.Model.MorphiaQuery;
import play.mvc.*;
import token.NoCookieFilter;
import utils.LocoUtils;
import play.i18n.Messages;

import java.io.InputStreamReader;
import java.util.*;

import models.ResponseMeta;
import models.ResponseModel;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.mongodb.Mongo;

/***
 * 	Copyright (c) 2011-2012 WareNinja.com
 *  http://www.WareNinja.com - https://github.com/WareNinja
 *  Author: yg@wareninja.com / twitter: @WareNinja
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

@With(NoCookieFilter.class)
public class TokenController  extends Controller {

	static final String KEY_PREFIX = "tokens_";
    static final String CACHE_TTL = Play.configuration.getProperty("fsqdiscovery.tokencache.ttl");
    
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