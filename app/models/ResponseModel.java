package models;

import play.db.jpa.Model;

/***
 * 	Copyright (c) 2011-2012 WareNinja.com
 *  http://www.WareNinja.com - https://github.com/WareNinja
 *  	
 *  Author: yg@wareninja.com / twitter: @WareNinja
 */

public class ResponseModel extends Model {
    public ResponseMeta meta;
    public Object data;
}
