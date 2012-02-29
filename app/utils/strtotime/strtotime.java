package utils.strtotime;

/* 
 * strtotime ${pom.version} (${changeSet} on ${changeSetDate})
 * (c) 2009 Davide Angelocola <davide.angelocola@gmail.com>
 * http://bitbucket.org/dfa/strtotime
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public final class strtotime {

/*
 * from: http://stackoverflow.com/questions/1268174/phps-strtotime-in-java
 * 
 * example usage:
 * Date now = strtotime("now");
 * Date tomorrow = strtotime("tomorrow");
 * Date bla1 = strtotime("3 days");
 * Date bla2 = strtotime("-3 days");
 * 
 * this is the format from facebook
 * "updated_time": "2011-01-03T15:55:47+0000" -> using "UTC" by default
 * yyyy-MM-dd'T'HH:mm:ssZ
 * 
   					yyyy-MM-dd 1969-12-31
                     yyyy-MM-dd 1970-01-01
               yyyy-MM-dd HH:mm 1969-12-31 16:00
               yyyy-MM-dd HH:mm 1970-01-01 00:00
              yyyy-MM-dd HH:mmZ 1969-12-31 16:00-0800
              yyyy-MM-dd HH:mmZ 1970-01-01 00:00+0000
       yyyy-MM-dd HH:mm:ss.SSSZ 1969-12-31 16:00:00.000-0800
       yyyy-MM-dd HH:mm:ss.SSSZ 1970-01-01 00:00:00.000+0000
     yyyy-MM-dd'T'HH:mm:ss.SSSZ 1969-12-31T16:00:00.000-0800
     yyyy-MM-dd'T'HH:mm:ss.SSSZ 1970-01-01T00:00:00.000+0000
 */
	
    private static final List<Matcher> matchers;

    static {
        matchers = new LinkedList<Matcher>();
        matchers.add(new NowMatcher());
        matchers.add(new TomorrowMatcher());
        matchers.add(new YesterdayMatcher());
        matchers.add(new DaysMatcher());
        matchers.add(new WeeksMatcher());
        
        matchers.add(new DateFormatMatcher(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")));//format used by facebook
        matchers.add(new DateFormatMatcher(new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z")));
        matchers.add(new DateFormatMatcher(new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z")));
        matchers.add(new DateFormatMatcher(new SimpleDateFormat("yyyy MM dd")));
    }

    public static void registerMatcher(Matcher matcher) {
        matchers.add(0, matcher);
    }

    public static interface Matcher {

        //public Date tryConvert(String input);
    	public Date tryConvert(String input, String refDateStr);
    }

    public static Date strtotime(String input) {
    	return strtotime(input, "");
        /*
    	for (Matcher matcher : matchers) {
            Date date = matcher.tryConvert(input);

            if (date != null) {
                return date;
            }
        }
        return null;
        */
    }
    public static Date strtotime(String input, String refDateStr) {
        for (Matcher matcher : matchers) {
            Date date = matcher.tryConvert(input, refDateStr);

            if (date != null) {
                return date;
            }
        }

        return null;
    }

    private strtotime() {
        throw new UnsupportedOperationException("cannot instantiate");
    }
}
