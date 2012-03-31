package jobs;

import play.Logger;
import play.jobs.Job;


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

public class BaseJob extends Job<Object> {

	/*// TODO: 
	@Override
	public void onException(Throwable e) {
		super.onException(e);
		Logger.error("onException : %s", e.toString());
	}

	@Override
	public void _finally() {
		//super._finally();
		// TODO:
		Logger.info("---> job done..!");
	}
    */
}
