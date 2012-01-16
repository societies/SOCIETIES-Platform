package org.societies.comm.xmpp.xc.impl;

import java.io.OutputStream;
import java.io.Writer;
import java.util.Stack;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;

public class InlineNamespaceXMLStreamWriter implements XMLStreamWriter {
	
	private final static XMLOutputFactory xof = XMLOutputFactory.newFactory();
	
	private XMLStreamWriter xsw = null;
	private Stack<String> namespaceStack = new Stack<String>();
	private Stack<Integer> namespaceChangeHeight = new Stack<Integer>();
	private Integer currentHeight = 0;

	public InlineNamespaceXMLStreamWriter(OutputStream out) throws XMLStreamException {
		xsw = xof.createXMLStreamWriter(out);
	}
	
	public InlineNamespaceXMLStreamWriter(Writer writer) throws XMLStreamException {
		xsw = xof.createXMLStreamWriter(writer);
	}
	
	public InlineNamespaceXMLStreamWriter(Result result) throws XMLStreamException {
		xsw = xof.createXMLStreamWriter(result);
	}
	
	@Override
	public void close() throws XMLStreamException {
        xsw.close();
    }

	@Override
    public void flush() throws XMLStreamException {
        xsw.flush();
    }

	@Override
    public NamespaceContext getNamespaceContext() {
        return xsw.getNamespaceContext();
    }

	@Override
    public String getPrefix(String arg0) throws XMLStreamException {
        return xsw.getPrefix(arg0);
    }

	@Override
    public Object getProperty(String arg0) throws IllegalArgumentException {
        return xsw.getProperty(arg0);
    }

	@Override
    public void setDefaultNamespace(String arg0) throws XMLStreamException {
        xsw.setDefaultNamespace(arg0);
    }

	@Override
    public void setPrefix(String arg0, String arg1) throws XMLStreamException {
        xsw.setPrefix(arg0, arg1);
    }

	@Override
    public void writeAttribute(String arg0, String arg1) throws XMLStreamException {
        xsw.writeAttribute(arg0, arg1);
    }

	@Override
    public void writeAttribute(String arg0, String arg1, String arg2) throws XMLStreamException {
        xsw.writeAttribute(arg0, arg1, arg2);
    }

	@Override
    public void writeAttribute(String arg0, String arg1, String arg2, String arg3) throws XMLStreamException {
        xsw.writeAttribute(arg0, arg1, arg2, arg3);
    }

	@Override
    public void writeCData(String arg0) throws XMLStreamException {
        xsw.writeCData(arg0);
    }

	@Override
    public void writeCharacters(String arg0) throws XMLStreamException {
        xsw.writeCharacters(arg0);
    }

	@Override
    public void writeCharacters(char[] arg0, int arg1, int arg2) throws XMLStreamException {
        xsw.writeCharacters(arg0, arg1, arg2);
    }

	@Override
    public void writeComment(String arg0) throws XMLStreamException {
        xsw.writeComment(arg0);
    }

	@Override
    public void writeDTD(String arg0) throws XMLStreamException {
        xsw.writeDTD(arg0);
    }

	@Override
    public void writeDefaultNamespace(String arg0) throws XMLStreamException {
        xsw.writeDefaultNamespace(arg0);
    }

	@Override
    public void writeEmptyElement(String arg0) throws XMLStreamException {
        xsw.writeEmptyElement(arg0);
    }

	@Override
    public void writeEmptyElement(String arg0, String arg1) throws XMLStreamException {
        xsw.writeEmptyElement(arg0, arg1);
    }

	@Override
    public void writeEndDocument() throws XMLStreamException {
        xsw.writeEndDocument();
    }

	@Override
    public void writeEntityRef(String arg0) throws XMLStreamException {
        xsw.writeEntityRef(arg0);
    }

	@Override
    public void writeProcessingInstruction(String arg0) throws XMLStreamException {
        xsw.writeProcessingInstruction(arg0);
    }

	@Override
    public void writeProcessingInstruction(String arg0, String arg1) throws XMLStreamException {
        xsw.writeProcessingInstruction(arg0, arg1);
    }

	@Override
    public void writeStartDocument() throws XMLStreamException {
        xsw.writeStartDocument();
    }

	@Override
    public void writeStartDocument(String arg0) throws XMLStreamException {
        xsw.writeStartDocument(arg0);
    }

	@Override
    public void writeStartDocument(String arg0, String arg1) throws XMLStreamException {
        xsw.writeStartDocument(arg0, arg1);
    }

	@Override
    public void writeStartElement(String arg0) throws XMLStreamException {
		currentHeight++;
        xsw.writeStartElement(arg0);
    }

	@Override
    public void writeStartElement(String arg0, String arg1) throws XMLStreamException {
		currentHeight++;
        xsw.writeStartElement(arg0, arg1);
    }

	@Override
    public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
		currentHeight++;
		xsw.writeStartElement("", localName, namespaceURI);
        if(namespaceURI!=null && namespaceURI.length()>0) {
        	if(namespaceStack.size()==0 || !namespaceURI.equals(namespaceStack.peek())) {
                writeDefaultNamespace(namespaceURI);
                // nsStackUp
                namespaceChangeHeight.push(currentHeight);
                namespaceStack.push(namespaceURI);
            }
        }
     }
	
	@Override
    public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
		xsw.writeStartElement("", localName, namespaceURI);
        if(namespaceURI!=null && namespaceURI.length()>0) {
            if(namespaceStack.size()==0 || !namespaceURI.equals(namespaceStack.peek())) {
                writeDefaultNamespace(namespaceURI);
            }
        }
    }
	
	@Override
    public void writeEndElement() throws XMLStreamException {
		currentHeight--;
		if (namespaceChangeHeight.size()>0 && namespaceChangeHeight.peek()>currentHeight) {
			// nsStackDown
			namespaceChangeHeight.pop();
            namespaceStack.pop();
		}
        xsw.writeEndElement();
    }
	
	@Override
    public void writeNamespace(String arg0, String arg1) throws XMLStreamException {
		// TODO
    }
	
	@Override
    public void setNamespaceContext(NamespaceContext arg0) throws XMLStreamException {
		//xsw.setNamespaceContext(arg0); TODO
    }

}
