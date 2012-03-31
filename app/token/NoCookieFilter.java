package token;

import play.*;
import play.cache.Cache;
import play.libs.F.Promise;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;
import play.modules.morphia.Model.MorphiaQuery;
import play.mvc.*;
import utils.LocoUtils;
import play.i18n.Messages;

import java.io.InputStreamReader;
import java.util.*;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
//import org.codehaus.jackson.map.ObjectMapper;

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

/** 
 * Removes cookies from all responses. 
 * 
 * This is because cookies are not required in stateless webservice and 
 * we don't want to send any unnecessary information to the client. 
 *  
 */ 
public class NoCookieFilter extends Controller { 
    /** An empty cookie map to replace any cookies in the response. */ 
    private static final Map<String, Http.Cookie> cookies = new HashMap<String, Http.Cookie>(0); 
    /** 
     * When the configuration property 'cookies.enabled' equals false, 
     * this Finally filter will replace the cookies in the response with an empty Map. 
     */
    /*
    @Before
    protected static void removeCookiesBefore() { 
    	
    	Logger.info("removeCookiesBefore");
    	removeAllCookies (); 
    }
    */
    
    @Finally
    protected static void removeCookiesFinally() { 
    	
    	Logger.info("removeCookiesFinally");
    	removeAllCookies (); 
    }
    
    protected static void removeAllCookies () {
    	String cookiesEnabled = Play.configuration.getProperty("fsqdiscovery.cookiefilter.enabled"); 
    
	    if ("true".equalsIgnoreCase(cookiesEnabled)) { 
	        response.cookies = cookies;
	        Logger.info("RESET cookies!!!");
	    } 
    }
} 