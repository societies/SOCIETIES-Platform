package org.societies.comm.simplexml;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class XMLGregorianCalendarConverter implements Converter<XMLGregorianCalendar> {

	DatatypeFactory df;
	
	public XMLGregorianCalendarConverter() throws DatatypeConfigurationException {
		df = DatatypeFactory.newInstance();
	}
	
	@Override
	public XMLGregorianCalendar read(InputNode node) throws Exception {
		XMLGregorianCalendar xmlDate = df.newXMLGregorianCalendar(node.getValue());
		return xmlDate;
	}

	@Override
	public void write(OutputNode node, XMLGregorianCalendar value)
			throws Exception {
		node.setValue(value.toXMLFormat());
	}
}
