package utils;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import models.PoiModelFoursquare;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import play.Logger;

/***
 * 	Copyright (c) 2011-2012 WareNinja.com
 *  http://www.WareNinja.com - https://github.com/WareNinja
 *  	
 *  Author: yg@wareninja.com / twitter: @WareNinja
 */
/*
 * contains common util functions 
 */
public class LocoUtils {

	static final String TAG = "LocoUtils";
	
	public static Gson getGson() {
		return new GsonBuilder()
		    .excludeFieldsWithModifiers( new int[] { 
		    		Modifier.STATIC, Modifier.TRANSIENT//, Modifier.FINAL 
		    		} )
		    .excludeFieldsWithoutExposeAnnotation()
		    .create();
	}
	public static Gson getGsonSimple() {
		return new GsonBuilder()
		    .excludeFieldsWithModifiers( new int[] { 
		    		Modifier.STATIC, Modifier.TRANSIENT//, Modifier.FINAL 
		    		} )
		    .create();
	}
	
	
	
	public static LinkedList<PoiModelFoursquare> calculateDistance(double lat, double lng
			, LinkedList<PoiModelFoursquare> locationList) {
		LinkedList<PoiModelFoursquare> resultList = new LinkedList<PoiModelFoursquare>();
		
		for (PoiModelFoursquare location : locationList) {
			try {
				location.distance = getDistanceMeters(lat, lng, location.lat, location.lng);
			}
			catch (Exception ex) {
				Logger.warn("exception -> %s", ex.toString());
			}
			resultList.add(location);
			//Logger.info("location : %s", location);
		}
		
		Collections.sort(resultList, new LocationComparator());
		//Collections.reverse(resultList);
		
		return resultList;
	}
	static class LocationComparator implements Comparator<PoiModelFoursquare> {
	    @Override
	    public int compare(PoiModelFoursquare o1, PoiModelFoursquare o2) {
	        return (
	        		Long.valueOf(o1.distance).compareTo( Long.valueOf(o2.distance) ) 
	        		);
	    }
	}
	
	/**
	 * Calculate the distance in meters between this point and the one given.
	 * 
	 * TODO: Evaluate if this works over short distances well.  I read
	 * that it might not, but it's what a lot of geo packages seem to use.
	 * 
	 * @param other The other point to calculate the distance between.
	 * @return An int, representing the distance between the points in meters.
	 */
	public static long getDistanceMeters(double lat1, double lng1, double lat2, double lng2) {
		
		double dist = acos(sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(lng1 - lng2));
		if(dist < 0) {
			dist = dist + Math.PI;
		}
		return Math.round(dist * 6378100);
	}
	/**
	 * Checks that the internal representation is a valid Geographical Point.
	 * 
	 * @return boolean true if valid for Earth, false otherwise.
	 */
	public static boolean isLatLngValid(double lat, double lng) {
		return Math.abs(lng) <= 180 && Math.abs(lat) <= 90;
	}
	// --- GeoLoc conversions ---
	/** Multiplier for km to miles. **/
	private static final double MILES_CONVERT = 0.621371192;
	/** Multiplier for meters to feet. **/
	private static final double FEET_CONVERT = 3.2808399;
	/** Conversion factor for degrees/mdegrees. **/
	public static final double CNV = 1E6;
	/**
	 * Convert a number of kilometers to miles.
	 * @param km number of km to convert
	 * @return number of km in miles
	 */
	public static double asMiles(final double km) {
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(km * MILES_CONVERT));
	}
	//public static double asMiles(final int meters) {
	public static double asMiles(final long meters) {
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(asMiles(meters / 1000.0)));
	}
	/**
	 * Convert a number of meters to feet.
	 * @param meters Value to convert in meters.
	 * @return meters converted to feet.
	 */
	public static double asFeet(final double meters) {
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(meters * FEET_CONVERT));
	}
	public static String asFeetString(final double meters) {
		return asFeet(meters) + " ft";
	}
	
	public static String asMilesString(final double km) {
		return asMiles(km) + " mi";
	}
	public static String asMilesString(final int meters) {
		return asMiles(meters) + " mi";
	}
	public static String asKilometerString(final double km) {
		return km + " km";
	}
	
	public static String asKilometerString(final int meters) {
		return (double)(meters/1000) + " km";
	}
	public static String asMeterString(final int meters) {
		return meters + " meters";
	}
	
	/**
	 * Convert degrees to microdegrees.
	 * @param degrees
	 * @return integer microdegrees.
	 */
	public static int asMicroDegrees(final double degrees) {
		return (int) (degrees * CNV);
	}
	
	/**
	 * Convert microdegrees to degrees.
	 * @param mDegrees
	 * @return double type degrees.
	 */
	public static double asDegrees(final int mDegrees) {
		return mDegrees / CNV;
	}
	
	public static String getShortFormattedDate() {
		return getShortFormattedDate(System.currentTimeMillis());
	}
	public static String getShortFormattedDate(long millis) {
		return getShortFormattedDate(new Date(millis));
	}
	public static String getShortFormattedDate(Date date) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		return sdf.format(date);
	}
	
	/*public static String getFormattedDateEpoch(long seconds) {
		
		try {
			return getFormattedDate(new Date(
					(new java.text.SimpleDateFormat ("MM/dd/yyyy HH:mm:ss").parse("01/01/1970 00:00:00").getTime()) 
					+ (seconds*1000L)
					));
		} catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
	}
	public static String getFormattedDateEpoch(long seconds, String timeZone) {
		
		try {
			Date date = Calendar.getInstance(TimeZone.getTimeZone(timeZone)).getTime();
			date.setTime(seconds*1000L);
			
			return getFormattedDate( date.getTime() );
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}*/
	public static String getFormattedDate(long millis, String timeZone) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
		if (!StringUtils.isEmpty(timeZone)) sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
		return sdf.format( new Date(millis) );
	}
	
	public static String getFormattedDate() {
		return getFormattedDate(System.currentTimeMillis());
		// long epoch = System.currentTimeMillis()/1000; /seconds since 
	}
	public static String getFormattedDate(long millis) {
		return getFormattedDate(new Date(millis));
	}
	public static String getFormattedDate(Date date) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
		return sdf.format(date);
	}
	
	public static String buildUrlParams(HashMap<String, String> params) {
		
		String result = "";
		
		for (Map.Entry<String, String> param : params.entrySet()) {
			result += param.getKey() +"=" + param.getValue() + "&";
		}
		if (result.contains("&")) result = result.substring(0, result.lastIndexOf("&"));
		
		return result;
	}
	
	public static String readStream(InputStream in) {//throws IOException {
        StringBuilder sb = new StringBuilder();
        
        try {
	        BufferedReader r = new BufferedReader(new InputStreamReader(in));//, 1000
	        for (String line = r.readLine(); line != null; line = r.readLine()) {
	            sb.append(line);
	        }
	        in.close();
        }
        catch (Exception ex) {
        	Logger.error("exception : %s", ex.toString());
        }
        
        return sb.toString();
    }
	
	
	// -> source from CastUtils: https://github.com/apache/pig/blob/89c2e8e76c68d0d0abe6a36b4e08ddc56979796f/src/org/apache/pig/impl/util/CastUtils.java
    private static Integer mMaxInt = Integer.valueOf(Integer.MAX_VALUE);
    private static Long mMaxLong = Long.valueOf(Long.MAX_VALUE);

    public static Double stringToDouble(String str) {
	    if (str == null) {
	    	return null;
	    } else {
		    try {
		    return Double.parseDouble(str);
		    } catch (NumberFormatException e) {
		    	Logger.warn(TAG + "|Unable to interpret value "
		    		    + str
		    		    + " in field being "
		    		    + "converted to double, caught NumberFormatException <"
		    		    + e.getMessage() + "> field discarded");
		    	return null;
		    }
	    }
    }
    public static Float stringToFloat(String str) {
	    if (str == null) {
	    	return null;
	    } else {
		    try {
		    	return Float.parseFloat(str);
		    } catch (NumberFormatException e) {
		    	Logger.warn(TAG + "|Unable to interpret value "
		    		    + str
		    		    + " in field being "
		    		    + "converted to float, caught NumberFormatException <"
		    		    + e.getMessage() + "> field discarded");
		    	return null;
		    }
	    }
    }
    public static Integer stringToInteger(String str) {
	    if (str == null) {
	    	return null;
	    } else {
		    try {
		    	return Integer.parseInt(str);
		    } catch (NumberFormatException e) {
			    // It's possible that this field can be interpreted as a double.
			    // Unfortunately Java doesn't handle this in Integer.valueOf. So
			    // we need to try to convert it to a double and if that works
			    // then
			    // go to an int.
			    try {
				    Double d = Double.valueOf(str);
				    // Need to check for an overflow error
				    if (d.doubleValue() > mMaxInt.doubleValue() + 1.0) {
				    	Logger.warn(TAG + "|Value " + d
							    + " too large for integer");
				    	return null;
				    }
				    return Integer.valueOf(d.intValue());
			    } catch (NumberFormatException nfe2) {
			    	Logger.warn(TAG + "|Unable to interpret value "
						    + str
						    + " in field being "
						    + "converted to int, caught NumberFormatException <"
						    + e.getMessage()
						    + "> field discarded");
			    	return null;
			    }
		    }
	    }
    }
    public static Long stringToLong(String str) {
	    if (str == null) {
	    	return null;
	    } else {
		    try {
		    	return Long.parseLong(str);
		    } catch (NumberFormatException e) {
			    // It's possible that this field can be interpreted as a double.
			    // Unfortunately Java doesn't handle this in Long.valueOf. So
			    // we need to try to convert it to a double and if that works
			    // then
			    // go to an long.
			    try {
				    Double d = Double.valueOf(str);
				    // Need to check for an overflow error
				    if (d.doubleValue() > mMaxLong.doubleValue() + 1.0) {
				    	Logger.warn(TAG + "|Value " + d
							    + " too large for long");
				    	return null;
				    }
				    return Long.valueOf(d.longValue());
			    } catch (NumberFormatException nfe2) {
			    	Logger.warn(TAG + "|Unable to interpret value "
						    + str
						    + " in field being "
						    + "converted to long, caught NumberFormatException <"
						    + nfe2.getMessage()
						    + "> field discarded");
			    	return null;
			    }
		    }
	    }
    }
	// ---
}
