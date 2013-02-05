/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.simple.basic;

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
        if( mat.find() ) {
	        String tzCode = "GMT"+mat.group(1)+mat.group(2); // eg "GMT+0100"
	        tz = TimeZone.getTimeZone(tzCode);
        }
        DateFormat df = new SimpleDateFormat(DATE_TIME_FORMAT);
        if( tz != null ) {
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
