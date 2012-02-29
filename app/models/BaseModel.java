package models;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

import play.modules.morphia.Model;
import play.modules.morphia.Model.AutoTimestamp;
import utils.LocoUtils;

/***
 * 	Copyright (c) 2011 WareNinja.com
 *  http://www.WareNinja.com - https://github.com/WareNinja
 *  	
 *  Author: yg@wareninja.com
 */
public class BaseModel extends Model {
	
	public String createdTime;
    public String updatedTime;
    
    public String oid;
   /*
    @Id  
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
    */
}
