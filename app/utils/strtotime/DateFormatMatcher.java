package utils.strtotime;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

class DateFormatMatcher implements strtotime.Matcher {

    private final DateFormat dateFormat;

    public DateFormatMatcher(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public Date tryConvert(String input, String refDateStr) {
        try {
            return dateFormat.parse(input);
        } catch (ParseException ex) {
            return null;
        }
    }
}
