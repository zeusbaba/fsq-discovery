package models;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

/***
 * 	Copyright (c) 2011-2012 WareNinja.com
 *  http://www.WareNinja.com - https://github.com/WareNinja
 *  	
 *  Author: yg@wareninja.com / twitter: @WareNinja
 */

public class ResponseModel implements Serializable {
	
	@Expose
    public ResponseMeta meta;
	@Expose
    public Object data;
}
