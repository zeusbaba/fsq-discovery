package models;

import play.db.jpa.Model;

/***
 * 	Copyright (c) 2011 WareNinja.com
 *  http://www.WareNinja.com - https://github.com/WareNinja
 *  	
 *  Author: yg@wareninja.com
 */

public class ResponseModel extends Model {
    public ResponseMeta meta;
    public Object data;
}
