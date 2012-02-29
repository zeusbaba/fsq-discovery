package utils.strtotime;

import java.util.Date;
import java.util.regex.Pattern;

class NowMatcher implements strtotime.Matcher {

    private static final Pattern now = Pattern.compile("\\W*now\\W*");
    private static final Pattern today = Pattern.compile("\\W*today\\W*");

    @Override
    public Date tryConvert(String input, String refDateStr) {
        if (now.matcher(input).matches() || today.matcher(input).matches()) {
            return new Date();
        } else {
            return null;
        }
    }
}
