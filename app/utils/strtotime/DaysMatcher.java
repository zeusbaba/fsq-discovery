/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.strtotime;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

class DaysMatcher implements strtotime.Matcher {

    private final Pattern days = Pattern.compile("[\\-\\+]?\\d+ days");

    @Override
    public Date tryConvert(String input, String refDateStr) {

    	Calendar calendar = Calendar.getInstance();
    	if (!StringUtils.isEmpty(refDateStr)) {
    		try {
    			calendar.setTime( DateUtils.parseDate(refDateStr, new String[] {"yyyy-MM-dd"}) );
    		}
    		catch (Exception ex) {
    		}
    	}
    	
        if (days.matcher(input).matches()) {
            int d = Integer.parseInt(input.split(" ")[0]);
            //Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, d);
            return calendar.getTime();
        }

        return null;
    }
}
