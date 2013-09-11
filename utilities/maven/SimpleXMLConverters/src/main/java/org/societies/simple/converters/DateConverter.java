package org.societies.simple.converters;

import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateConverter implements Converter<Date> {
    private final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final Pattern TZ_REGEX = Pattern.compile("([+-][0-9][0-9]):?([0-9][0-9])$");

    @Override
    public Date read(InputNode node) throws Exception {
        String dateString = node.getValue();
        //DateFormat df = new SimpleDateFormat(DATE_TIME_FORMAT);
        //return df.parse(dateString);

        Matcher mat = TZ_REGEX.matcher(dateString);
        TimeZone tz = null;
        if (mat.find()) {
            String tzCode = "GMT" + mat.group(1) + mat.group(2); // eg "GMT+0100"
            tz = TimeZone.getTimeZone(tzCode);
        }
        DateFormat df = new SimpleDateFormat(DATE_TIME_FORMAT);
        if (tz != null) {
            df.setTimeZone(tz);
        }
        try {
            return df.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public void write(OutputNode node, Date date) {
        //http://weblogs.java.net/blog/felipegaucho/archive/2009/12/06/jaxb-customization-xsddatetime
        //new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ") produces	2009-12-06T15:59:34+0100
        //the expected format for the Schema xsd:dateTime type is	2009-12-06T15:59:34+01:00

        DateFormat df = new SimpleDateFormat(DATE_TIME_FORMAT);
        DateFormat tzDf = new SimpleDateFormat("Z");
        String timezone = tzDf.format(date);
        String val = df.format(date) + timezone.substring(0, 3) + ":" + timezone.substring(3);
        node.setValue(val);
    }
}
