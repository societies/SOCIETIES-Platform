package org.societies.comm.xmpp.client.impl;

import static android.content.Context.BIND_AUTO_CREATE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.comm.xmpp.datatypes.Stanza;
import org.societies.comm.xmpp.exceptions.CommunicationException;
import org.societies.comm.xmpp.interfaces.CommCallback;
import org.societies.interfaces.XMPPAgent;
import org.societies.ipc.Stub;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Message;
import org.xmpp.packet.Message.Type;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;

public class ClientCommunicationMgr {
	
	private static final Logger log = LoggerFactory.getLogger(ClientCommunicationMgr.class);
	
	private static final ComponentName serviceCN = new ComponentName("org.societies", "org.societies.AgentService"); // TODO
	
	private SAXReader reader = new SAXReader();
	private Context androidContext;
	private Map<String, CommCallback> callbacks = new HashMap<String, CommCallback>();
	
	public ClientCommunicationMgr(Context androidContext) {
		this.androidContext = androidContext;
	}
	
	public void sendMessage(Stanza stanza, Type type, Object payload)
			throws CommunicationException {
		
		if (payload == null) {
			throw new InvalidParameterException("Payload cannot be null");
		}
		try {
			JAXBContext jc = JAXBContext.newInstance(payload.getClass().getPackage().getName()); // TODO Need to register all packages?		
			Marshaller m = jc.createMarshaller();			
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			m.marshal(payload, os);					
			ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());		
			Document document = reader.read(is);
			Message message = new Message();
//			if(type != null) TODO throws CloneNotSupportedException when uncommented
//				message.setType(type);
			message.setTo(stanza.getTo().getJid());
			message.getElement().add(document.getRootElement());
			String xml = message.toXML();
			log.debug(xml); // TODO remove debug
			
			sendMessage(xml);
			
		} catch (Exception e) {
			throw new CommunicationException("Error sending message", e);
		}				
	}
	
	public void sendMessage(Stanza stanza, Object payload)
			throws CommunicationException {
		sendMessage(stanza, null, payload);
	}	
	
	public void sendIQ(Stanza stanza, IQ.Type type, Object payload,
			CommCallback callback) throws CommunicationException {
		try {
			JAXBContext jc = JAXBContext.newInstance(payload.getClass().getPackage().getName()); // TODO Need to register all packages?		
			Marshaller m = jc.createMarshaller();	
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			m.marshal(payload, os);
			ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());			
			Document document = reader.read(is);
			IQ iq = new IQ();
			iq.setTo(stanza.getTo().getJid());
			iq.setType(type);
			iq.getElement().add(document.getRootElement());
			String xml = iq.toXML();
			
//			callbacks.put(iq.getID(), callback); TODO
			
			sendIQ(iq.getID(), xml, callback);
			
		} catch (Exception e) {
			throw new CommunicationException("Error sending IQ message", e);
		}
	}
	
	private void sendMessage(final String xml) {
		ServiceConnection connection = new ServiceConnection() {

			@Override
			public void onServiceConnected(ComponentName cn, IBinder binder) {
				XMPPAgent agent = (XMPPAgent)Stub.newInstance(new Class<?>[]{XMPPAgent.class}, "0", new Messenger(binder));
				agent.sendMessage(xml);		
				androidContext.unbindService(this);
			}

			@Override
			public void onServiceDisconnected(ComponentName cn) {				
			}
			
		};
		
		bindService(connection);
	}
	
	private void sendIQ(final String id, final String xml, final CommCallback callback) {
		ServiceConnection connection = new ServiceConnection() {

			@Override
			public void onServiceConnected(ComponentName cn, IBinder binder) {
				XMPPAgent agent = (XMPPAgent)Stub.newInstance(new Class<?>[]{XMPPAgent.class}, "0", new Messenger(binder));
				agent.sendIQ(id, xml, new CallbackAdapter(callback, androidContext, this));				
			}

			@Override
			public void onServiceDisconnected(ComponentName cn) {				
			}
			
		};
		
		bindService(connection);
	}
	
	private void bindService(ServiceConnection connection) {
		Intent intent = new Intent();
        intent.setComponent(serviceCN);
        androidContext.bindService(intent, connection, BIND_AUTO_CREATE);
	}
}
