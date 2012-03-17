package models;


import java.io.Serializable;

import com.google.gson.annotations.Expose;


/***
 * 	Copyright (c) 2011-2012 WareNinja.com
 *  http://www.WareNinja.com - https://github.com/WareNinja
 *  	
 *  Author: yg@wareninja.com / twitter: @WareNinja
 */

/*
 * meta part of json response
 */
public class ResponseMeta implements Serializable {

	@Expose
	public Integer code;
	@Expose
	public String errorType;
	@Expose
	public String errorDetail;
}

