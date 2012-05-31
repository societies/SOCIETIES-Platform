package org.societies.personalisation.CRISTUserIntentPrediction.test;

import java.net.URI;
import java.net.URISyntaxException;

import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

public class MockIdentity implements IIdentity{
	
	IdentityType type;
	String identifier;
	String domainIdentifier;
	
	
	public static ServiceResourceIdentifier serviceId_music;
	public static ServiceResourceIdentifier serviceId_checkin;

	public MockIdentity(IdentityType type, String identifier,
			String domainIdentifier) {
		this.type = type;
		this.identifier = identifier;
		this.domainIdentifier = domainIdentifier;
	}

	@Override
	public String getJid() {
		return null;
	}

	@Override
	public String getBareJid() {
		return null;
	}

	@Override
	public String getDomain() {
		return domainIdentifier;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public IdentityType getType() {
		return type;
	}
	

	public static ServiceResourceIdentifier getServiceId_music()
	{
		if (serviceId_music != null)
		{
			return serviceId_music;
		}
		serviceId_music = new ServiceResourceIdentifier();
		try {
			serviceId_music.setIdentifier(new URI("http://testService_music"));
			return serviceId_music;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ServiceResourceIdentifier getServiceId_checkin()
	{
		if (serviceId_checkin != null)
		{
			return serviceId_checkin;
		}
		serviceId_checkin = new ServiceResourceIdentifier();
		try {
			serviceId_checkin.setIdentifier(new URI("http://testService_checkin"));
			return serviceId_checkin;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

}
