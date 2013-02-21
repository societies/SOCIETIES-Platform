package org.societies.simple.converters;

import javax.xml.parsers.ParserConfigurationException;

import org.jabber.protocol.pubsub.event.Item;
import org.jabber.protocol.pubsub.event.Items;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class EventItemsConverter implements Converter<Items> {
	
	private ItemConverterHelper ich;
	
	public EventItemsConverter(Serializer serializer) throws ParserConfigurationException {
		ich = new ItemConverterHelper(serializer);
	}

	public Items read(InputNode node) throws Exception {
		Items i = new Items();
		i.setNode(node.getAttribute("node").getValue());
		InputNode n = node.getNext();
		while (n!=null) {
			i.getItem().add(ich.readEventItemAnyToElement(n));
			n = node.getNext();
		}
		return i;
	}

	public void write(OutputNode node, Items value) throws Exception {
		node.setAttribute("node", value.getNode());
		for (Item i : value.getItem())
			ich.write(node.getChild("item"), i);
	}

}
