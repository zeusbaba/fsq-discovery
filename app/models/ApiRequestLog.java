package models;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import play.Logger;

import play.modules.morphia.Model;
import play.modules.morphia.Model.AutoTimestamp;
import utils.LocoUtils;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/***
 * 	Copyright (c) 2011-2012 WareNinja.com
 *  http://www.WareNinja.com - https://github.com/WareNinja
 *  	
 *  Author: yg@wareninja.com / twitter: @WareNinja
 */

@Entity(value="api_request_log", noClassnameStored=true)
@AutoTimestamp
public class ApiRequestLog extends Model implements Serializable {//{
	private static final long serialVersionUID = 1L;
	
	@Id @SerializedName("id") @Expose
	public String oid;
	@Override
    public Object getId() {
        return oid;
    }
    @Override
    protected void setId_(Object id) {
    	oid = processId_(id).toString();
    }
    protected static Object processId_(Object id) {
        return id.toString();
    }
    
    @Expose
    public String requestTime;
    @Expose
    public String requestUrl;
    @Expose
    public String clientIp;
    @Expose
    public String userAgent;
    //public Map requestHeaders = new HashMap();
    
    
	@Override
	public String toString() {
		return "ApiRequestLog [oid=" + oid + ", requestTime=" + requestTime
				+ ", requestUrl=" + requestUrl + ", clientIp=" + clientIp
				+ ", userAgent=" + userAgent + "]";
	}
	public String toJsonString() {
		return "" + LocoUtils.getGsonWithPrettyPrinting().toJson(this);
	}
}

