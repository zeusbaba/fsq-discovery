package models;


import com.google.gson.annotations.Expose;

import play.db.jpa.Model;

/***
 * 	Copyright (c) 2011-2012 WareNinja.com
 *  http://www.WareNinja.com - https://github.com/WareNinja
 *  	
 *  Author: yg@wareninja.com / twitter: @WareNinja
 */

/*
 * meta part of json response
 */
public class ResponseMeta extends Model {

	@Expose
	public Integer code;
	@Expose
	public String errorType;
	@Expose
	public String errorDetail;
}

/*
{
  "meta": {
    "code": 200,
  },
  "data": {
    "id": "1234567",
    "sessionId": "123323444",
    "updated_time": "2011-11-07T13:27:26+02:00",
    "ownerDomain": "http://api.aspiro.com/domain/Comoyo",
    "uri": "http://api.aspiro.com/user/1234567"
    "externalProfiles":[
        {
		"type":	"facebook",
		"id": "100001789213579",
		"name": "Name on SN",
		"access_token": "<access_token>",
        "created_time": "2011-11-07T11:27:26+02:00",
		"updated_time": "2011-11-07T13:27:26+02:00",
		"verified": "true"
        },
        {
		"type":	"twitter",
		"id": "201122323",
		"name": "Name on SN",
		"access_token": "<access_token>",
		"access_secret": "<access_secret>",
        "created_time": "2011-11-07T15:27:26+02:00",
		"updated_time": "2011-11-07T16:27:26+02:00",
		"verified": "true"
        }
    ]
  } 
}
*/
