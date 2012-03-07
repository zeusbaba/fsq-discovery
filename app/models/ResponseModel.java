package models;

import com.google.gson.annotations.Expose;

import play.db.jpa.Model;

/***
 * 	Copyright (c) 2011-2012 WareNinja.com
 *  http://www.WareNinja.com - https://github.com/WareNinja
 *  	
 *  Author: yg@wareninja.com / twitter: @WareNinja
 */

public class ResponseModel extends Model {
	
	@Expose
    public ResponseMeta meta;
	@Expose
    public Object data;
}
