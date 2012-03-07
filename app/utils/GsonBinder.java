package utils;

import java.lang.annotation.*;
import java.lang.reflect.Type;

import com.google.gson.*;

import play.Logger;
import play.data.binding.*;

/***
 * 	Copyright (c) 2011-2012 WareNinja.com
 *  http://www.WareNinja.com - https://github.com/WareNinja
 *  	
 *  Author: yg@wareninja.com / twitter: @WareNinja
 */

@Global
public class GsonBinder implements TypeBinder<JsonObject> {

	@Override
	public Object bind(String name, Annotation[] annotations, String value,
			Class actualClass, Type genericType) throws Exception {
		//Logger.info("value : %s", value);
		return new JsonParser().parse(value);
	}
}