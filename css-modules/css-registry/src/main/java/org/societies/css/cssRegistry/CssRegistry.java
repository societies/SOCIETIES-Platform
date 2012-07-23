/**
Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 

(SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp (IBM),
INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.css.cssRegistry;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.internal.css.cssRegistry.ICssRegistry;
import org.societies.api.internal.css.cssRegistry.exception.CssRegistrationException;

import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.api.schema.cssmanagement.CssNode;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.societies.api.schema.cssmanagement.CssRequest;
import org.societies.css.cssRegistry.model.CssFriendEntry;
import org.societies.css.cssRegistry.model.CssNodeEntry;
import org.societies.css.cssRegistry.model.CssRegistryEntry;
import org.societies.css.cssRegistry.model.CssRequestEntry;
import org.societies.api.schema.cssmanagement.CssRequestStatusType;

public class CssRegistry implements ICssRegistry {
	private SessionFactory sessionFactory;
	private static Logger log = LoggerFactory.getLogger(CssRegistry.class);

	/**
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @param sessionFactory
	 *            the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public CssRegistry() {
		log.info("CSS registry bundle instantiated.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.internal.css.cssRegistry.ICssRegistry#registerCss(org
	 * .societies.api.internal.css.management.CSSRecord)
	 */
	@Override
	public CssInterfaceResult registerCss(CssRecord cssRecord)
			throws CssRegistrationException {
		// TODO Auto-generated method stub
		
		log.info("??????????????? CSSRegistry registerCss Called : ");
		log.info("??????????????? CSSRegistry cssRecord node Size passed in is : " +cssRecord.getCssNodes().size());

		CssInterfaceResult result = new CssInterfaceResult();

		Session session = sessionFactory.openSession();
		CssRegistryEntry tmpRegistryEntry = null;
		CssNodeEntry tmpNodeEntry = null;

		Transaction t = session.beginTransaction();
		try {

			if (cssRecord.getCssNodes() != null) {
				log.info("??????????????? CSSRegistry Node Size is : " +cssRecord.getCssNodes().size());
				for (CssNode cssNode : cssRecord.getCssNodes()) {
					tmpNodeEntry = new CssNodeEntry(cssNode.getIdentity(),
						cssNode.getStatus(), cssNode.getType(), cssNode.getCssNodeMAC(), cssNode.getInteractable(), false);
					log.info("??????????????? CSSRegistry nodeId is : " +cssNode.getIdentity());
					log.info("??????????????? CSSRegistry status is : " +cssNode.getStatus());
					log.info("??????????????? CSSRegistry type is : " +cssNode.getType());
					log.info("??????????????? CSSRegistry nodeMAC is : " +cssNode.getCssNodeMAC());
					//log.info("??????????????? CSSRegistry interactable is : " +cssNode.isInteractable());
					session.save(tmpNodeEntry);
				}
			}
			if (cssRecord.getArchiveCSSNodes() != null) {
				for (CssNode cssNode : cssRecord.getArchiveCSSNodes()) {
					tmpNodeEntry = new CssNodeEntry(cssNode.getIdentity(),
						cssNode.getStatus(), cssNode.getType(), cssNode.getCssNodeMAC(), cssNode.getInteractable(), true);
					session.save(tmpNodeEntry);
				}
			}
			tmpRegistryEntry = new CssRegistryEntry(
					cssRecord.getCssHostingLocation(),
					cssRecord.getCssIdentity(), cssRecord.getCssInactivation(),
					cssRecord.getCssRegistration(), cssRecord.getCssUpTime(),
					cssRecord.getDomainServer(), cssRecord.getEmailID(),
					cssRecord.getEntity(), cssRecord.getForeName(),
					cssRecord.getHomeLocation(), cssRecord.getIdentityName(),
					cssRecord.getImID(), cssRecord.getName(),
					cssRecord.getPassword(), cssRecord.getPresence(),
					cssRecord.getSex(), cssRecord.getSocialURI(),
					cssRecord.getStatus());

			session.save(tmpRegistryEntry);

			t.commit();
			log.debug("Css Details saved.");
			log.info("Css Details saved.");

		} catch (Exception e) {
			t.rollback();
			e.printStackTrace();
			throw new CssRegistrationException(e);
		} finally {
			if (session != null) {
				session.close();
			}
		}

		result.setResultStatus(true);
		result.setProfile(cssRecord);
		log.info("??????????????? CSSRegistry cssRecord node Size passed in is : " +cssRecord.getCssNodes().size());
		for (int index = 0; index < cssRecord.getCssNodes().size(); index++) {
			log.info("@@@@@@@@@@@@@ CSSRegistry cssRecord node identity is : " +cssRecord.getCssNodes().get(index).getIdentity());
		}
			//

		return result;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.internal.css.cssRegistry.ICssRegistry#unregisterCss
	 * (org.societies.api.internal.css.management.CSSRecord)
	 */
	@Override
	public void unregisterCss(CssRecord cssRecord)
			throws CssRegistrationException {
		// TODO Auto-generated method stub
		log.info("%%%%%%%%%%%%%% CSSRegistry unregisterCss Called");
		
		Session session = sessionFactory.openSession();
		CssRegistryEntry tmpRegistryEntry = null;
		CssNodeEntry tmpNodeEntry = null;

		Transaction t = session.beginTransaction();
		try {

			if (cssRecord.getCssNodes() != null) {
				for (CssNode cssNode : cssRecord.getCssNodes()) {
					tmpNodeEntry = new CssNodeEntry(cssNode.getIdentity(),
						cssNode.getStatus(), cssNode.getType(), cssNode.getCssNodeMAC(), cssNode.getInteractable(), false);
					log.info("!!!!!!!!!!!!!!!!!!! cssNode identity: " +cssNode.getIdentity() + " cssNode Status : " +cssNode.getStatus() + " cssNode Type : " +cssNode.getType() + "cssNodeMAC : " +cssNode.getCssNodeMAC());
					session.delete(tmpNodeEntry);
				}
			}
			
			if (cssRecord.getArchiveCSSNodes() != null) {
				for (CssNode cssNode : cssRecord.getArchiveCSSNodes()) {
					tmpNodeEntry = new CssNodeEntry(cssNode.getIdentity(),
						cssNode.getStatus(), cssNode.getType(), cssNode.getCssNodeMAC(), cssNode.getInteractable(), true);
					session.delete(tmpNodeEntry);
				}
			}
			
			tmpRegistryEntry = new CssRegistryEntry(
					cssRecord.getCssHostingLocation(),
					cssRecord.getCssIdentity(), cssRecord.getCssInactivation(),
					cssRecord.getCssRegistration(), cssRecord.getCssUpTime(),
					cssRecord.getDomainServer(), cssRecord.getEmailID(),
					cssRecord.getEntity(), cssRecord.getForeName(),
					cssRecord.getHomeLocation(), cssRecord.getIdentityName(),
					cssRecord.getImID(), cssRecord.getName(),
					cssRecord.getPassword(), cssRecord.getPresence(),
					cssRecord.getSex(), cssRecord.getSocialURI(),
					cssRecord.getStatus());

			session.delete(tmpRegistryEntry);

			t.commit();
			log.debug("Css Details unregistered.");
			log.info("Css Details unregistered.");

		} catch (Exception e) {
			t.rollback();
			e.printStackTrace();
			throw new CssRegistrationException(e);
		} finally {
			if (session != null) {
				session.close();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.internal.css.cssRegistry.ICssRegistry#registerCss(org
	 * .societies.api.internal.css.management.CSSRecord)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CssRecord getCssRecord() throws CssRegistrationException {
		Session session = sessionFactory.openSession();
		CssRecord cssDetails = new CssRecord();
		CssNode cssNodeDetails = null;
		
		log.info("$$$$$$$$$$$$$$$$$$$ CSSRegistry getCssRecord Called : ");

		Transaction t = session.beginTransaction();
		try {

			List<CssRegistryEntry> tmpRegistryEntryList = session
					.createCriteria(CssRegistryEntry.class).list();

			cssDetails.setDomainServer(tmpRegistryEntryList.get(0)
					.getDomainServer());
			cssDetails.setCssHostingLocation(tmpRegistryEntryList.get(0)
					.getCssHostingLocation());
			cssDetails.setEntity(tmpRegistryEntryList.get(0).getEntity());
			cssDetails.setForeName(tmpRegistryEntryList.get(0).getForeName());
			cssDetails.setName(tmpRegistryEntryList.get(0).getName());
			cssDetails.setIdentityName(tmpRegistryEntryList.get(0)
					.getIdentityName());
			cssDetails.setPassword(tmpRegistryEntryList.get(0).getPassword());
			cssDetails.setEmailID(tmpRegistryEntryList.get(0).getEmailID());
			cssDetails.setImID(tmpRegistryEntryList.get(0).getImID());
			cssDetails.setSocialURI(tmpRegistryEntryList.get(0).getSocialURI());
			cssDetails.setSex(tmpRegistryEntryList.get(0).getSex());
			cssDetails.setHomeLocation(tmpRegistryEntryList.get(0)
					.getHomeLocation());
			cssDetails.setCssIdentity(tmpRegistryEntryList.get(0)
					.getCssIdentity());
			cssDetails.setStatus(tmpRegistryEntryList.get(0).getStatus());
			cssDetails.setCssRegistration(tmpRegistryEntryList.get(0)
					.getCssRegistration());
			cssDetails.setCssInactivation(tmpRegistryEntryList.get(0)
					.getCssInactivation());
			cssDetails.setCssUpTime(tmpRegistryEntryList.get(0).getCssUpTime());
			cssDetails.setPresence(tmpRegistryEntryList.get(0).getPresence());

			List<CssNodeEntry> tmpNodeRegistryEntryList = session
					.createCriteria(CssNodeEntry.class).list();

			
			log.info("$$$$$$$$$$$$$$$$$$$ CSSRegistry tmpNodeRegistryEntryList size : " +tmpNodeRegistryEntryList.size());
			
			if (tmpNodeRegistryEntryList != null) {
				for (CssNodeEntry savedNode : tmpNodeRegistryEntryList) {
					cssNodeDetails = new CssNode();
					log.info("$$$$$$$$$$$$$$$$$$$ CSSRegistry tmpNodeRegistryEntryList identity : " +savedNode.getNodeIdentity());
					cssNodeDetails.setIdentity(savedNode.getNodeIdentity());
					cssNodeDetails.setType(savedNode.getType());
					cssNodeDetails.setStatus(savedNode.getStatus());
					cssNodeDetails.setCssNodeMAC(savedNode.getcssNodeMAC());
					log.info("$$$$$$$$$$$$$$$$$$$ CSSRegistry tmpNodeRegistryEntryList MAC : " +savedNode.getcssNodeMAC());
					log.info("$$$$$$$$$$$$$$$$$$$ CSSRegistry tmpNodeRegistryEntryList Interactable : " +savedNode.getInteractable());
					cssNodeDetails.setInteractable(savedNode.getInteractable());

					if (savedNode.getArchived() == true) 
						cssDetails.getArchiveCSSNodes().add(cssNodeDetails);
					else
						cssDetails.getCssNodes().add(cssNodeDetails);
				}
			}

		} catch (Exception e) {
			t.rollback();
			e.printStackTrace();
			throw new CssRegistrationException(e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return cssDetails;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.internal.css.cssRegistry.ICssRegistry#registerCss(org
	 * .societies.api.internal.css.management.CSSRecord)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void updateCssRecord(CssRecord cssDetails)
			throws CssRegistrationException {

		log.info("??????????????? CSSRegistry updateCssRecord Called : ");
		
		Session session = sessionFactory.openSession();
		CssNodeEntry tmpNodeEntry = null;

		Transaction t = session.beginTransaction();
		try {

			// Celar the nodeEntry table, we will repopulate it
			List<CssNodeEntry> tmpNodeRegistryEntryList = session
					.createCriteria(CssNodeEntry.class).list();
			session.delete(tmpNodeRegistryEntryList);

			if (cssDetails.getCssNodes() != null) {
				for (CssNode cssNode : cssDetails.getCssNodes()) {
					tmpNodeEntry = new CssNodeEntry(cssNode.getIdentity(),
						cssNode.getStatus(), cssNode.getType(), cssNode.getCssNodeMAC(), cssNode.getInteractable(), false);
					session.save(tmpNodeEntry);
				}
			}

			if (cssDetails.getArchiveCSSNodes() != null) {
				for (CssNode cssNode : cssDetails.getArchiveCSSNodes()) {
					tmpNodeEntry = new CssNodeEntry(cssNode.getIdentity(),
						cssNode.getStatus(), cssNode.getType(), cssNode.getCssNodeMAC(), cssNode.getInteractable(), true);
					log.info("??????????????? CSSRegistry nodeId is : " +cssNode.getIdentity());
					log.info("??????????????? CSSRegistry status is : " +cssNode.getStatus());
					log.info("??????????????? CSSRegistry type is : " +cssNode.getType());
					log.info("??????????????? CSSRegistry nodeMAC is : " +cssNode.getCssNodeMAC());
					log.info("??????????????? CSSRegistry interactable is : " +cssNode.getInteractable());
					session.save(tmpNodeEntry);
				}
			}

			List<CssRegistryEntry> tmpCssRegistryEntryList = session
					.createCriteria(CssRegistryEntry.class).list();

			// Now to update the main table
			tmpCssRegistryEntryList.get(0).setDomainServer(
					cssDetails.getDomainServer());
			tmpCssRegistryEntryList.get(0).setCssHostingLocation(
					cssDetails.getCssHostingLocation());
			tmpCssRegistryEntryList.get(0).setEntity(cssDetails.getEntity());
			tmpCssRegistryEntryList.get(0)
					.setForeName(cssDetails.getForeName());
			tmpCssRegistryEntryList.get(0).setName(cssDetails.getName());
			tmpCssRegistryEntryList.get(0).setIdentityName(
					cssDetails.getIdentityName());
			tmpCssRegistryEntryList.get(0)
					.setPassword(cssDetails.getPassword());
			tmpCssRegistryEntryList.get(0).setEmailID(cssDetails.getEmailID());
			tmpCssRegistryEntryList.get(0).setImID(cssDetails.getImID());
			tmpCssRegistryEntryList.get(0).setSocialURI(
					cssDetails.getSocialURI());
			tmpCssRegistryEntryList.get(0).setSex(cssDetails.getSex());
			tmpCssRegistryEntryList.get(0).setHomeLocation(
					cssDetails.getHomeLocation());
			tmpCssRegistryEntryList.get(0).setCssIdentity(
					cssDetails.getCssIdentity());
			tmpCssRegistryEntryList.get(0).setStatus(cssDetails.getStatus());
			tmpCssRegistryEntryList.get(0).setCssRegistration(
					cssDetails.getCssRegistration());
			tmpCssRegistryEntryList.get(0).setCssInactivation(
					cssDetails.getCssInactivation());
			tmpCssRegistryEntryList.get(0).setCssUpTime(
					cssDetails.getCssUpTime());
			tmpCssRegistryEntryList.get(0)
					.setPresence(cssDetails.getPresence());

			session.update(tmpCssRegistryEntryList);

			t.commit();
		} catch (Exception e) {
			t.rollback();
			e.printStackTrace();
			throw new CssRegistrationException(e);
		} finally {
			if (session != null) {
				session.close();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.internal.css.cssRegistry.ICssRegistry#registerCss(org
	 * .societies.api.internal.css.management.CSSRecord)
	 */

	@SuppressWarnings("unchecked")
	@Override
	public void updateCssRequestRecord(CssRequest cssRequest)
			throws CssRegistrationException {

		Session session = sessionFactory.openSession();
		CssRequestEntry filterRegistryEntry = new CssRequestEntry();

		Transaction t = session.beginTransaction();
		try {

			filterRegistryEntry.setCssIdentity(cssRequest.getCssIdentity());

			List<CssRequestEntry> tmpRegistryEntryList = session.createCriteria(CssRequestEntry.class)
					.add(Restrictions.eq("cssIdentity",
							cssRequest.getCssIdentity()).ignoreCase()).list();
			if (tmpRegistryEntryList != null) {
				for (CssRequestEntry tmpEn : tmpRegistryEntryList) {
					session.delete(tmpEn);
				}
			}

			if (cssRequest.getRequestStatus() != CssRequestStatusType.CANCELLED) {
				filterRegistryEntry.setRequestStatus(cssRequest
						.getRequestStatus().value());

				session.save(filterRegistryEntry);
			}
			t.commit();
		} catch (Exception e) {
			t.rollback();
			e.printStackTrace();
			throw new CssRegistrationException(e);
		} finally {
			if (session != null) {
				session.close();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.internal.css.cssRegistry.ICssRegistry#registerCss(org
	 * .societies.api.internal.css.management.CSSRecord)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<CssRequest> getCssRequests() throws CssRegistrationException {

		Session session = sessionFactory.openSession();
		CssRequest registryEntry = null;
		List<CssRequest> requestList = new ArrayList<CssRequest>();

		try {

			List<CssRequestEntry> tmpRegistryEntryList = session.createCriteria(CssRequestEntry.class).list();
			if (tmpRegistryEntryList != null) {
				for (CssRequestEntry tmpEn : tmpRegistryEntryList) {
					registryEntry = new CssRequest();

					registryEntry.setCssIdentity(tmpEn.getCssIdentity());

					registryEntry.setRequestStatus(CssRequestStatusType
							.valueOf(tmpEn.getRequestStatus().toUpperCase()));
					requestList.add(registryEntry);

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
			throw new CssRegistrationException(e);
		} finally {
			if (session != null) {
				session.close();
			}
		}

		return requestList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.internal.css.cssRegistry.ICssRegistry#registerCss(org
	 * .societies.api.internal.css.management.CSSRecord)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void updateCssFriendRequestRecord(CssRequest cssRequest)
			throws CssRegistrationException {

		Session session = sessionFactory.openSession();
		CssFriendEntry filterRegistryEntry = new CssFriendEntry();

		Transaction t = session.beginTransaction();
		try {

			filterRegistryEntry.setFriendIdentity(cssRequest.getCssIdentity());

			List<CssFriendEntry> tmpRegistryEntryList = session
					.createCriteria(CssFriendEntry.class)
					.add(Restrictions.eq("friendIdentity",
							cssRequest.getCssIdentity()).ignoreCase()).list();

			if (tmpRegistryEntryList != null) {
				for (CssFriendEntry tmpEn : tmpRegistryEntryList) {
					session.delete(tmpEn);
				}
			}

			if (cssRequest.getRequestStatus() != CssRequestStatusType.CANCELLED) {

				filterRegistryEntry.setRequestStatus(cssRequest
						.getRequestStatus().value());

				session.save(filterRegistryEntry);
			}
			t.commit();
		} catch (Exception e) {
			t.rollback();
			e.printStackTrace();
			throw new CssRegistrationException(e);
		} finally {
			if (session != null) {
				session.close();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.internal.css.cssRegistry.ICssRegistry#registerCss(org
	 * .societies.api.internal.css.management.CSSRecord)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<CssRequest> getCssFriendRequests()
			throws CssRegistrationException {

		Session session = sessionFactory.openSession();
		CssRequest registryEntry = null;
		List<CssRequest> requestList = new ArrayList<CssRequest>();

		try {

			List<CssFriendEntry> tmpRegistryEntryList = session.createCriteria(CssFriendEntry.class).list();
			if (tmpRegistryEntryList != null) {
				for (CssFriendEntry tmpEn : tmpRegistryEntryList) {
					registryEntry = new CssRequest();

					registryEntry.setCssIdentity(tmpEn.getFriendIdentity());

					registryEntry.setRequestStatus(CssRequestStatusType
							.valueOf(tmpEn.getRequestStatus().toUpperCase()));
					requestList.add(registryEntry);

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
			throw new CssRegistrationException(e);
		} finally {
			if (session != null) {
				session.close();
			}
		}

		return requestList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.internal.css.cssRegistry.ICssRegistry#registerCss(org
	 * .societies.api.internal.css.management.CSSRecord)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public CssRequest getCssFriendRequest(String cssFriendId)
			throws CssRegistrationException {

		Session session = sessionFactory.openSession();
		CssRequest registryEntry = new CssRequest();
		CssFriendEntry filterRegistryEntry = new CssFriendEntry();
		registryEntry.setCssIdentity(cssFriendId);
		registryEntry.setRequestStatus(CssRequestStatusType.NOTREQUESTED); // default
																			// value

		try {

			List<CssFriendEntry> tmpRegistryEntryList = session
					.createCriteria(CssFriendEntry.class)
					.add(Restrictions.eq("friendIdentity", cssFriendId).ignoreCase()).list();

			if (tmpRegistryEntryList != null && tmpRegistryEntryList.size() > 0) {
				registryEntry.setRequestStatus(CssRequestStatusType
						.valueOf(tmpRegistryEntryList.get(0).getRequestStatus()
								.toUpperCase()));

			}

		} catch (Exception e) {

			e.printStackTrace();
			throw new CssRegistrationException(e);
		} finally {
			if (session != null) {
				session.close();
			}
		}

		return registryEntry;

	}

}
