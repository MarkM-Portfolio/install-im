/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2002, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.websphere.product.xml;

/*
 * Calendar utilities for XML data.
 *
 * History 1.2, 9/26/03
 *
 * 29-Jul-2002 Initial Version
 */

import java.util.*;

import java.text.ParseException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class CalendarUtil
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // Calendar/XML Utility Methods:
    //
    //    public static Calendar getTimeStamp();
    //    public static String getTimeStampAsString();
    //
    //    public static int[] splitMillisec(int);
    //    public static void adjustByMillisec(Calendar, int);
    //
    //    public static String formatXMLZone(int);
    //    public static int[] parseXMLZone(String);
    //
    //    public static String formatXMLTimeStamp(Calendar);
    //    public static Calendar recoverCalendar(String);
    //
    //    public static String fileFormat(Calendar);
    //    public static String fileFormat(String);
    //
    //    public static String formatSimpleCalendar(Calendar);
    //    public static Calendar recoverSimpleCalendar(String);


    // Answer the current time stamp as a calendar object.

    public static Calendar getTimeStamp()
    {
        return Calendar.getInstance();
    }

    // Answer the current time stamp as string.  See
    // 'formatXMLTimeStamp' for the formatting details.

    public static String getTimeStampAsString()
    {
        return formatXMLTimeStamp(getTimeStamp());
    }

    // Split the millisec value into hours, minutes, and seconds.
    // The millisec value must be non-negative.
    //
    // See constants for the offsets to the result values.

    public static final int HOURS_OFFSET = 0 ;
    // Split the millisec value into hours, minutes, and seconds.
    // The millisec value must be non-negative.
    //
    // See constants for the offsets to the result values.

    public static final int MINUTES_OFFSET = 1 ;
    // Split the millisec value into hours, minutes, and seconds.
    // The millisec value must be non-negative.
    //
    // See constants for the offsets to the result values.

    public static final int SECONDS_OFFSET = 2 ;

    public static int[] splitMillisec(int millisec)
    {
        int seconds = millisec / 1000;
        millisec -= (seconds * 1000);
        if ( millisec >= 500 ) // round up
            seconds++;

        int hours = seconds / (60 * 60);
        seconds -= (hours * (60 * 60));

        int minutes = seconds / 60;
        seconds -= (minutes * 60);

        return new int[] { hours, minutes, seconds };
    }

    // Adjust the time-stamp by the specified count of
    // millseconds.  Negative counts are allowed.
    //
    // This method modifies the argument calendar.  Be
    // careful to use the 'clone()' method to avoid side
    // effects.

    public static void adjustByMillisec(Calendar calendar, int deltaMillisec)
    {
        if ( deltaMillisec == 0 )
            return;

        boolean didNegate;

        if ( didNegate = (deltaMillisec < 0) )
            deltaMillisec *= -1;

        int[] deltaValues = splitMillisec(deltaMillisec);

        int deltaHours   = deltaValues[HOURS_OFFSET],
            deltaMinutes = deltaValues[MINUTES_OFFSET],
            deltaSeconds = deltaValues[SECONDS_OFFSET];

        if ( didNegate ) {
            deltaMillisec *= -1;

            deltaSeconds *= -1;
            deltaMinutes *= -1;
            deltaHours *= -1;
        }

        calendar.add(Calendar.SECOND, deltaSeconds);
        calendar.add(Calendar.MINUTE, deltaMinutes);
        calendar.add(Calendar.HOUR,   deltaHours);
    }

    // Answer the count of milliseconds as an XML format
    // time zone value:
    // 
    //     A 'Z' denotes a time zone of 00:00.
    //
    // The time zone value is interpreted by the formula:
    //
    //     Local Time = UCT Time + Zone Offset
    //
    // For example:
    //
    //     10:00-01:00 is UCT 11:00.
    //
    // Except when returning a "Z", this method always
    // returns two digits of hours plus a colon plus
    // two digits of minutes.
    //
    // An extra colon plus two digits of seconds may be
    // added, although, the standard does not seem to
    // support this case.
    //
    // Millisecond values are rounded to the nearest
    // second.  '500' milliseconds is rounded up.

    public static String formatXMLZone(int millisec)
    {
        if ( millisec == 0 )
            return "Z";

        String xmlZone;

        if ( millisec < 0 ) {
            xmlZone = "-";
            millisec *= -1;
        } else {
            xmlZone = "+";
        }

        int[] millisecValues = splitMillisec(millisec);

        int hours   = millisecValues[HOURS_OFFSET],
            minutes = millisecValues[MINUTES_OFFSET],
            seconds = millisecValues[SECONDS_OFFSET];

        String hoursText = Integer.toString(hours);
        if ( hours < 10 ) 
            hoursText = "0" + hoursText;

        xmlZone += hoursText;

        // Always add on the zone minutes, even if 0.

        String minutesText = Integer.toString(minutes);
        if ( minutes < 10 )
            minutesText = "0" + minutesText;

        xmlZone += ':' + minutesText;

        // Only add on the zone seconds if non-zero.
        // Don't know if the standard allows this, or
        // if the seconds will ever be non-zero.

        if ( seconds > 0 ) {
            String secondsText = Integer.toString(seconds);
            if ( seconds < 10 )
                secondsText = "0" + secondsText;

            xmlZone += ':' + secondsText;
        }

        return xmlZone;
    }

    // Parse the XML zone text.
    //
    // See the method 'formatXMLZone' for format details.
    //
    // This method parses integers.  If invalid integer values
    // are provided, the parse values will be zero.
    //
    // The result values are unpredictable when the zone text
    // is invalid.

    public static int[] parseXMLZone(String xmlZoneText)
    {
        String hoursText   = null;
        String minutesText = null;
        String secondsText = null;

        if ( !xmlZoneText.startsWith("Z") ) {
            int firstColonOffset = xmlZoneText.indexOf(':');

            if ( firstColonOffset == -1 ) {
                hoursText = xmlZoneText;

            } else {
                hoursText = xmlZoneText.substring(0, firstColonOffset);
                xmlZoneText = xmlZoneText.substring(firstColonOffset, xmlZoneText.length());

                int secondColonOffset = xmlZoneText.indexOf(':');

                if ( secondColonOffset == -1 ) {
                    minutesText = xmlZoneText;

                } else {
                    minutesText = xmlZoneText.substring(0, secondColonOffset);
                    secondsText = xmlZoneText.substring(secondColonOffset, xmlZoneText.length());
                }
            }
        }

        int zoneHours;

        if ( hoursText != null ) {
            try {
                zoneHours = Integer.parseInt(hoursText);
            } catch ( NumberFormatException e ) {
                zoneHours = 0;
            }
        } else {
            zoneHours = 0;
        }

        int zoneMinutes;

        if ( minutesText != null ) {
            try {
                zoneMinutes = Integer.parseInt(minutesText);
            } catch ( NumberFormatException e ) {
                zoneMinutes = 0;
            }
        } else {
            zoneMinutes = 0;
        }

        int zoneSeconds;

        if ( secondsText != null ) {
            try {
                zoneSeconds = Integer.parseInt(secondsText);
            } catch ( NumberFormatException e ) {
                zoneSeconds = 0;
            }
        } else {
            zoneSeconds = 0;
        }

        return new int[] { zoneHours, zoneMinutes, zoneSeconds };
    }

    // Format the calendar value as an XML time stamp.
    //
    // The XML format time stamp provides a complete representation
    // of the argument time stamp, includig a time zone value.
    //
    // The XML format time stamp is accurrate to the nearest second.
    // Milliseconds values are rounded by this method to the nearest
    // second.  '500' milliseconds is rounded up.

    public static final String
        XML_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    public static final int
        XML_DATE_FORMAT_LENGTH = 19;

    public static final DateFormat
        XML_DATE_FORMATTER = new SimpleDateFormat(XML_DATE_FORMAT);

    public static String formatXMLTimeStamp(Calendar calendar)
    {
        Date dateTime = calendar.getTime();
        String xmlDate = XML_DATE_FORMATTER.format(dateTime);

        int zoneMillisec =
            calendar.get(Calendar.ZONE_OFFSET) +
            calendar.get(Calendar.DST_OFFSET);  // milliseconds

        String xmlZone = formatXMLZone(zoneMillisec);

        xmlDate += xmlZone;

        return xmlDate;
    }

    // RecoveredTime - (CurrentZone - RecoveredZone)
    //
    // 4pm-1:00 ==> 5pm UCT
    //
    // Current zone offset: -2:00
    // 3pm-2:00 ==> 5pm UCT
    //
    // 4 - (-1 - (-2))
    // 4 - (-1 + 2)
    // 4 - 1
    // 3

    public static Calendar recoverCalendar(String xmlTimeStamp)
    {
        // System.out.println(">>>> Recoving calendar from time: " + xmlTimeStamp + " <<<<");

	if ( xmlTimeStamp == null )
	    return null;

        int zoneOffset = XML_DATE_FORMAT_LENGTH;

        String baseText = xmlTimeStamp.substring(0, zoneOffset);
        String zoneText = xmlTimeStamp.substring(zoneOffset, xmlTimeStamp.length());

        Date recoveredDate;

        try {
            recoveredDate = XML_DATE_FORMATTER.parse(baseText);
        } catch ( ParseException e ) {
            return null;
        }

        Calendar recoveredCalendar = Calendar.getInstance();

        recoveredCalendar.setTime(recoveredDate);

        // Don't know if setting the time changes the time zone.
        // Could be a problem.

        int currentZone = 
            recoveredCalendar.get(Calendar.ZONE_OFFSET) +
            recoveredCalendar.get(Calendar.DST_OFFSET);  // milliseconds

        int[] zoneValues = parseXMLZone(zoneText);

        int zoneHours   = zoneValues[HOURS_OFFSET],
            zoneMinutes = zoneValues[MINUTES_OFFSET],
            zoneSeconds = zoneValues[SECONDS_OFFSET];

        int recoveredZone = ((((zoneHours * 60) + zoneMinutes) * 60) + zoneSeconds) * 1000;

        int deltaZone = currentZone - recoveredZone;

        adjustByMillisec(recoveredCalendar, deltaZone);

        return recoveredCalendar;
    }

    // Format the calendar value as a text fragment which
    // may be used in a file name.
    //
    // The format is given by 'FILE_DATE_FORMAT', below.
    //
    // The value is according to the calendar value as a
    // UCT value.

    public static final String
        FILE_DATE_FORMAT = "yyyyMMdd_HHmmss";

    public static final DateFormat
        FILE_DATE_FORMATTER = new SimpleDateFormat(FILE_DATE_FORMAT);

    public static String fileFormat(Calendar calendar)
    {
        Calendar scratchCalendar = (Calendar) calendar.clone();
        
        int currentZone = 
            scratchCalendar.get(Calendar.ZONE_OFFSET) +
            scratchCalendar.get(Calendar.DST_OFFSET);  // milliseconds

        // The zone offset is added to UCT to get local time.
        // Subtract from the local time to get UCT.

        adjustByMillisec(scratchCalendar, -1 * currentZone);

        Date scratchDate = scratchCalendar.getTime();
        
        String fileFormat = FILE_DATE_FORMATTER.format(scratchDate);

        return fileFormat;
    }

    // Format the XML time stamp as a file name fragment.
    // This means converting the time stamp to a calendar,
    // adjusting out the time zone value, and formatting
    // the resulting UCT time stamp.
    //
    // Answer null if the argument time stamp is not valid.

    public static String fileFormat(String xmlTimeStamp)
    {
        Calendar recoveredCalendar = recoverCalendar(xmlTimeStamp);

        String fileName;

        if ( recoveredCalendar == null )
            fileName = null;
        else
            fileName = fileFormat(recoveredCalendar);

        return fileName;
    }

    // Answer tht time stamp formatted as a simple date.

    public static final String
        SIMPLE_DATE_FORMAT = "yyyy-MM-dd";
    
    public static final SimpleDateFormat
        SIMPLE_DATE_FORMATTER = new SimpleDateFormat(SIMPLE_DATE_FORMAT);

    public static String formatSimple(Calendar timeStamp)
    {
        return SIMPLE_DATE_FORMATTER.format(timeStamp);
    }

    // Convert the simple time stamp to a calendar object.
    // Answer null if a parse error occurs.

    public static Calendar recoverSimple(String timeStampText)
    {
        Date recoveredDate;

        try {
            recoveredDate = SIMPLE_DATE_FORMATTER.parse(timeStampText);

        } catch ( ParseException e ) {
            return null;
        }

        Calendar recoveredCalendar = Calendar.getInstance();
        recoveredCalendar.setTime(recoveredDate);

        return recoveredCalendar; 
    }
}
