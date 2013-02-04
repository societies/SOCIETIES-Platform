package org.societies.simple.converters;

import javax.xml.parsers.ParserConfigurationException;

import org.jabber.protocol.pubsub.Item;
import org.jabber.protocol.pubsub.Items;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class PubsubItemsConverter implements Converter<Items> {
	
	private ItemConverterHelper ich;
	
	public PubsubItemsConverter(Serializer serializer) throws ParserConfigurationException {
		ich = new ItemConverterHelper(serializer);
	}

	@Override
	public Items read(InputNode node) throws Exception {
		Items i = new Items();
		i.setNode(node.getAttribute("node").getValue());
		InputNode n = node.getNext();
		while (n!=null) {
			i.getItem().add(ich.readToElement(n));
			n = node.getNext();
		}
		return i;
	}

	@Override
	public void write(OutputNode node, Items value) throws Exception {
		node.setAttribute("node", value.getNode());
		for (Item i : value.getItem())
			ich.write(node.getChild("item"), i);
	}

}
