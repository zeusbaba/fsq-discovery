package utils.strtotime;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class YesterdayMatcher implements strtotime.Matcher {

    private final Pattern yesterday = Pattern.compile("yesterday");

    @Override
    public Date tryConvert(String input, String refDateStr) {
        if (yesterday.matcher(input).matches()) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            return calendar.getTime();
        } else {
            return null;
        }
    }
}
