package jobs;

import play.Logger;
import play.jobs.Job;


/***
 * 	Copyright (c) 2011-2012 WareNinja.com
 *  http://www.WareNinja.com - https://github.com/WareNinja
 *  	
 *  Author: yg@wareninja.com
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
