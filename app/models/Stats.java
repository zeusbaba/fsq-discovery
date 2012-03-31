package models;


import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;

import javax.persistence.Entity;

import play.Logger;
import play.db.jpa.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


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

/*
 * 
 */
public class Stats implements Serializable {
	private static final long serialVersionUID = 1L;

	@SerializedName("checkinsCount") @Expose
	public Integer checkinsCount;
	@SerializedName("usersCount") @Expose
	public Integer usersCount;
	@SerializedName("tipCount") @Expose
	public Integer tipCount;
	
	@SerializedName("herenowCount") @Expose
	public Integer herenowCount;
	
	@Override
	public String toString() {
		return "Stats [checkinsCount=" + checkinsCount
				+ ", usersCount=" + usersCount + ", tipCount=" + tipCount
				+ ", herenowCount="+herenowCount
				+ "]";
	}
}

