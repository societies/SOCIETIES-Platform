package org.societies.simple.converters;

import javax.xml.parsers.ParserConfigurationException;

import org.jabber.protocol.pubsub.Item;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class PubsubItemConverter implements Converter<Item> {
	
	private ItemConverterHelper ich;
	
	public PubsubItemConverter(Serializer serializer) throws ParserConfigurationException {
		ich = new ItemConverterHelper(serializer);
	}

	@Override
	public Item read(InputNode node) throws Exception {
		return ich.readToElement(node);
	}

	@Override
	public void write(OutputNode node, Item value) throws Exception {
		ich.write(node, value);
	}

}
