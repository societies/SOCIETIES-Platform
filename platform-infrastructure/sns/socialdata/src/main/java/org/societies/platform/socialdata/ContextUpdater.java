package org.societies.platform.socialdata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.shindig.social.opensocial.model.Group;
import org.apache.shindig.social.opensocial.model.Person;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.platform.socialdata.converters.FriendsConverter;
import org.societies.platform.socialdata.converters.FriendsConveterFactory;
import org.societies.platform.socialdata.converters.GroupConverter;
import org.societies.platform.socialdata.converters.GroupConveterFactory;
import org.societies.platform.socialdata.converters.PersonConverter;
import org.societies.platform.socialdata.converters.PersonConverterFactory;

public class ContextUpdater {

    /** The logging facility. */
    private static final Logger LOG = LoggerFactory
	    .getLogger(ContextUpdater.class);

    ICtxBroker internalCtxBroker = null;
    IIdentity cssId = null;
    String snName;

    CtxEntity socialNetworkEntity = null;
    List<ISocialConnector> connectors = new ArrayList<ISocialConnector>();

    public ContextUpdater(ICtxBroker internalCtxBroker, IIdentity cssId) {

	LOG.debug("updating user profile in context: broker service: "
		+ internalCtxBroker);
	this.cssId = cssId;
	this.internalCtxBroker = internalCtxBroker;

    }

    Runnable updateCtxProfileThread = new Runnable() {

	@Override
	public void run() {

	    LOG.debug(" Updating CtxBroker - Updater thread started ....");
	    for (ISocialConnector connector : connectors)
		updateCtxProfile(connector);

	    LOG.debug("CtxBroker Updated - Updater thread is over.");
	}
    };

    public void updateSocialData(List<ISocialConnector> connectors) {
	this.connectors = connectors;
	new Thread(updateCtxProfileThread).start();
    }

    public void updateCtxProfile(ISocialConnector connector) {

	LOG.debug(" Updating profile data for " + connector.getConnectorName());

	this.snName = connector.getConnectorName();
	this.socialNetworkEntity = this.getSnCtxEntity();

	// Update Simple Profile
	PersonConverter parser = PersonConverterFactory.getConverter(connector);
	Person profile = parser.load(connector.getUserProfile());

	// updateStringFieldIfExists(snName, CtxAttributeTypes.TYPE);
	updateStringFieldIfExist(profile.getAboutMe(), CtxAttributeTypes.ABOUT);
	updateStringFieldIfExist(profile.getProfileUrl(), CtxAttributeTypes.PROFILE_IMAGE_URL);
	updateStringFieldIfExist(profile.getDisplayName(), CtxAttributeTypes.NAME);

	if (profile.getName() != null) {
	    updateStringFieldIfExist(profile.getName().getGivenName(),CtxAttributeTypes.NAME_FIRST);
	    updateStringFieldIfExist(profile.getName().getFamilyName(), CtxAttributeTypes.NAME_LAST);
	}
	// updateStringFieldIfExist(profile.getPhoneNumbers(),
	// CtxAttributeTypes.PHONES);
	updateStringFieldIfExist(profile.getPoliticalViews(), CtxAttributeTypes.POLITICAL_VIEWS);
	updateStringFieldIfExist(profile.getPreferredUsername(),CtxAttributeTypes.USERNAME);
	updateStringFieldIfExist(profile.getThumbnailUrl(),CtxAttributeTypes.USERNAME);
	updateStringFieldIfExist(profile.getRelationshipStatus(),CtxAttributeTypes.STATUS);
	updateStringFieldIfExist(profile.getReligion(),CtxAttributeTypes.RELIGIOUS_VIEWS);

	if (profile.getGender() != null)
	    updateStringFieldIfExist(profile.getGender().name(),CtxAttributeTypes.SEX);
	if (profile.getBirthday() != null)
	    updateStringFieldIfExist(profile.getBirthday().toGMTString(), CtxAttributeTypes.BIRTHDAY);
	if (profile.getCurrentLocation() != null)
	    updateStringFieldIfExist(profile.getCurrentLocation().getFormatted(), CtxAttributeTypes.LOCATION_SYMBOLIC);

	updateStringFieldIfExist(profile.getBooks(), CtxAttributeTypes.BOOKS);
	updateStringFieldIfExist(profile.getMusic(), CtxAttributeTypes.MUSIC);
	updateStringFieldIfExist(profile.getInterests(),CtxAttributeTypes.INTERESTS);
	updateStringFieldIfExist(profile.getJobInterests(),CtxAttributeTypes.JOBS_INTERESTS);
	updateStringFieldIfExist(profile.getLanguagesSpoken(),CtxAttributeTypes.LANGUAGES);
	updateStringFieldIfExist(profile.getMovies(), CtxAttributeTypes.MOVIES);
	updateStringFieldIfExist(profile.getTurnOns(),CtxAttributeTypes.TURNSON);
	updateStringFieldIfExist(profile.getActivities(),CtxAttributeTypes.ACTIVITIES);
	// updateStringFieldIfExist(profile.getEmails(),
	// CtxAttributeTypes.EMAIL);

	LOG.debug(" Updating Friends List ...");
	// Add Friends List

	FriendsConverter friendsParser = FriendsConveterFactory.getConverter(connector);
	List<Person> friends = friendsParser.load(connector.getUserFriends());
	storeFriendsIntoContextBroker(friends);

	LOG.debug(" Updating Groups list ...");
	// Add Group List
	GroupConverter groupConverter = GroupConveterFactory.getConverter(connector);
	List<Group> groups = groupConverter.load(connector.getUserGroups());
	storeGroupsIntoContextBroker(groups);
    }

    private void updateStringFieldIfExist(String value, String type) {
	try {
	    if (value != null) {
		LOG.info("update " + type + " data" + value);
		storeSocialDataIntoContextBroker(type, value);
		LOG.info(snName + " entity updated with " + type + " data "
			+ socialNetworkEntity.getId());
	    } else
		LOG.debug(type + " value is NULL");
	} catch (Exception ex) {
	    LOG.error("Unable to store :" + type + " -> " + value + " because "
		    + ex, ex);
	}

    }

    private void updateStringFieldIfExist(List<String> listOfvalues, String type) {

	if (listOfvalues != null) {
	    String value = updateListData(listOfvalues);
	    updateStringFieldIfExist(value, type);
	} else
	    LOG.debug(type + " value is NULL");
    }

    private CtxEntity getSnCtxEntity() {

	IndividualCtxEntity individualEntity;
	CtxAssociation snsAssoc = null;
	CtxEntity socialNetwork = null;

	try {

	    individualEntity = this.internalCtxBroker.retrieveIndividualEntity(
		    this.cssId).get();

	    Set<CtxAssociationIdentifier> snsAssocSet = individualEntity
		    .getAssociations(CtxAssociationTypes.IS_CONNECTED_TO_SNS);
	    LOG.debug("There are " + snsAssocSet.size()
		    + " associations with SocialNetworks");

	    if (snsAssocSet.size() > 0) {

		List<CtxAssociationIdentifier> snsAssocList = new ArrayList<CtxAssociationIdentifier>(
			snsAssocSet);
		for (CtxAssociationIdentifier assocID : snsAssocList) {

		    snsAssoc = (CtxAssociation) this.internalCtxBroker
			    .retrieve(assocID).get();
		    Set<CtxEntityIdentifier> snsEntitiesSet = snsAssoc
			    .getChildEntities(CtxEntityTypes.SOCIAL_NETWORK);
		    List<CtxEntityIdentifier> snsEntitiesList = new ArrayList<CtxEntityIdentifier>(
			    snsEntitiesSet);

		    LOG.debug("lookup SN association" + snName);

		    // List<CtxEntityIdentifier> snEntList =
		    // this.internalCtxBroker.lookupEntities(snsEntitiesList,
		    // CtxAttributeTypes.NAME, snName).get();

		    List<CtxEntityIdentifier> snEntList = this.internalCtxBroker
			    .lookupEntities(snsEntitiesList,
				    CtxAttributeTypes.TYPE, snName).get();
		    // List<CtxEntityIdentifier> snEntList =
		    // this.internalCtxBroker.lookupEntities(snsEntitiesList,
		    // CtxAttributeTypes.NAME, snName).get();

		    if (snEntList.size() > 0) {
			socialNetwork = (CtxEntity) this.internalCtxBroker
				.retrieve(snEntList.get(0)).get();
			return socialNetwork;
		    }
		}

	    }

	    LOG.info("NO Asscotiation found  the User Entity");
	    snsAssoc = this.internalCtxBroker.createAssociation(
		    CtxAssociationTypes.IS_CONNECTED_TO_SNS).get();
	    LOG.info("---> snsAssoc created ");

	    List<CtxEntityIdentifier> snEntitiesList = this.internalCtxBroker
		    .lookupEntities(CtxEntityTypes.SOCIAL_NETWORK,
			    CtxAttributeTypes.TYPE, snName, snName).get();

	    if (snEntitiesList.size() == 0) {

		LOG.info("No SocialNetwork entity for this SN:" + snName);
		socialNetwork = this.internalCtxBroker.createEntity(
			CtxEntityTypes.SOCIAL_NETWORK).get();

		LOG.info("SOCIAL_NETWORK entity created"
			+ socialNetwork.getId());
		CtxAttribute snsNameAttr = this.internalCtxBroker
			.createAttribute(socialNetwork.getId(),
				CtxAttributeTypes.TYPE).get();
		snsNameAttr.setStringValue(snName);
		this.internalCtxBroker.update(snsNameAttr);

	    }

	    else {
		socialNetwork = (CtxEntity) this.internalCtxBroker.retrieve(
			snEntitiesList.get(0)).get();
	    }

	    LOG.debug("Add Association ...");
	    snsAssoc.addChildEntity(socialNetwork.getId());
	    snsAssoc.addChildEntity(individualEntity.getId());
	    snsAssoc.setParentEntity(individualEntity.getId());
	    snsAssoc = (CtxAssociation) this.internalCtxBroker.update(snsAssoc)
		    .get();

	    this.internalCtxBroker.update(individualEntity);

	} catch (InterruptedException e) {
	    LOG.error("Error:" + e, e);
	    e.printStackTrace();
	} catch (ExecutionException e) {
	    LOG.error("Error:" + e, e);
	    e.printStackTrace();
	} catch (CtxException e) {
	    LOG.error("Error:" + e, e);
	    e.printStackTrace();
	}

	return socialNetwork;
    }

    private CtxEntity storeSocialDataIntoContextBroker(String type,
	    Serializable value) {

	CtxAttribute attribute = null;

	try {

	    if (socialNetworkEntity != null) {

		LOG.info(">>>>> Updating user profile in context " + type
			+ " values" + value);
		List<CtxIdentifier> attributeIdentifiers = this.internalCtxBroker
			.lookup(socialNetworkEntity.getId(),
				CtxModelType.ATTRIBUTE, type).get();

		if (attributeIdentifiers.size() > 0) {

		    LOG.info("updating " + snName + " profile in context 1");
		    CtxIdentifier attrId = attributeIdentifiers.get(0);
		    attribute = (CtxAttribute) this.internalCtxBroker.retrieve(
			    attrId).get();
		    attribute = setAttrValueType(attribute, type, value);
		    attribute = (CtxAttribute) this.internalCtxBroker.update(
			    attribute).get();
		} else {
		    LOG.info("Insert attribute in user profile in context 2");
		    attribute = this.internalCtxBroker.createAttribute(
			    socialNetworkEntity.getId(), type).get();
		    attribute = setAttrValueType(attribute, type, value);
		    attribute = (CtxAttribute) this.internalCtxBroker.update(
			    attribute).get();
		}

		socialNetworkEntity = (CtxEntity) this.internalCtxBroker
			.update(socialNetworkEntity).get();
	    } else
		LOG.warn("Social Network Entity is NULL");
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (ExecutionException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (CtxException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return socialNetworkEntity;
    }

    private CtxEntity storeFriendsIntoContextBroker(List<Person> friends) {

	CtxAttribute attribute = null;

	try {

	    if (socialNetworkEntity != null) {

		LOG.info(">>>>> Check if FRIENDS fiels is already stored ");
		List<CtxIdentifier> attributeFriendsIdentifiers = this.internalCtxBroker
			.lookup(socialNetworkEntity.getId(),
				CtxModelType.ATTRIBUTE,
				CtxAttributeTypes.FRIENDS).get();

		for (CtxIdentifier ctxID : attributeFriendsIdentifiers) {
		    this.internalCtxBroker.remove(ctxID);
		}

		attribute = this.internalCtxBroker.createAttribute(
			socialNetworkEntity.getId(), CtxAttributeTypes.FRIENDS)
			.get();

		attribute = setAttrValueType(attribute,
			CtxAttributeTypes.FRIENDS, friends.size() + " "
				+ snName + " friends");

		attribute = (CtxAttribute) this.internalCtxBroker.update(
			attribute).get();

		for (Person friend : friends) {

		    String name = "";

		    if (friend.getAccounts() != null) {
			if (friend.getAccounts().size() > 0) {
			    name += friend.getAccounts().get(0).getDomain()
				    + ",";
			}
		    }
		    name += friend.getId() + ",";

		    // First just the name
		    name = "";
		    try {
			if (friend.getName() != null) {
			    if (friend.getName().getFormatted() != null)
				name += friend.getName().getFormatted();
			    else {
				if (friend.getName().getFamilyName() != null)
				    name += friend.getName().getFamilyName();
				if (friend.getName().getGivenName() != null) {
				    if (name.length() > 0)
					name += " ";
				    name += friend.getName().getGivenName();
				}
			    }
			}
			if (name.length() == 0)
			    name = friend.getId();

			LOG.info("Add friend [attribute]:" + name);

			attribute = this.internalCtxBroker.createAttribute(
				socialNetworkEntity.getId(),
				CtxAttributeTypes.FRIENDS).get();
			attribute = setAttrValueType(attribute,
				CtxAttributeTypes.FRIENDS, name);
			attribute = (CtxAttribute) this.internalCtxBroker
				.update(attribute).get();

		    }

		    catch (Exception ex) {
			LOG.error("Unable to find a name for this friend", ex);
		    }

		}

		socialNetworkEntity = (CtxEntity) this.internalCtxBroker
			.update(socialNetworkEntity).get();
	    } else
		LOG.warn("Social Network Entity is NULL");
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (ExecutionException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (CtxException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return socialNetworkEntity;

    }

    private CtxEntity storeGroupsIntoContextBroker(List<Group> groups) {

	CtxAttribute attribute = null;

	try {

	    if (socialNetworkEntity != null) {

		LOG.info(">>>>> Check if GROUPS fiels is already stored ");
		List<CtxIdentifier> attributeGroupssIdentifiers = this.internalCtxBroker
			.lookup(socialNetworkEntity.getId(),
				CtxModelType.ATTRIBUTE, CtxAttributeTypes.GROUP)
			.get();
		for (CtxIdentifier ctxID : attributeGroupssIdentifiers) {
		    this.internalCtxBroker.remove(ctxID);
		}

		for (Group group : groups) {

		    String value = group.getDescription();
		    LOG.info("Insert Group:" + value + "+  to user profile");
		    attribute = this.internalCtxBroker.createAttribute(
			    socialNetworkEntity.getId(),
			    CtxAttributeTypes.GROUP).get();
		    attribute = setAttrValueType(attribute,
			    CtxAttributeTypes.GROUP, value);
		    attribute = (CtxAttribute) this.internalCtxBroker.update(
			    attribute).get();
		}

		socialNetworkEntity = (CtxEntity) this.internalCtxBroker
			.update(socialNetworkEntity).get();
	    } else
		LOG.warn("Social Network Entity is NULL");
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (ExecutionException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (CtxException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return socialNetworkEntity;

    }

    private String updateListData(List<String> data) {

	String valueString = "";
	try {
	    for (int i = 0; i < data.size(); i++) {
		JSONObject jsonResponse = new JSONObject(data.get(i));
		String value = jsonResponse.get("value").toString();
		if (valueString.length() > 0)
		    valueString += ",";
		valueString += value;
	    }
	} catch (JSONException e) {
	    e.printStackTrace();
	}
	return valueString;
    }

    private CtxAttribute setAttrValueType(CtxAttribute attribute, String type,
	    Serializable value) {

	CtxAttribute updatedAttr = attribute;

	if (value == null) {
	    updatedAttr.setValueType(CtxAttributeValueType.EMPTY);
	    return updatedAttr;
	} else if (value instanceof String) {
	    updatedAttr.setValueType(CtxAttributeValueType.STRING);
	    updatedAttr.setStringValue(value.toString());
	    return updatedAttr;
	} else if (value instanceof Integer) {
	    updatedAttr.setValueType(CtxAttributeValueType.INTEGER);
	    updatedAttr.setIntegerValue((Integer) value);

	    return updatedAttr;
	} else if (value instanceof Double) {
	    updatedAttr.setValueType(CtxAttributeValueType.DOUBLE);
	    updatedAttr.setDoubleValue((Double) value);

	    return updatedAttr;
	} else if (value instanceof byte[]) {
	    updatedAttr.setValueType(CtxAttributeValueType.BINARY);
	    updatedAttr.setStringValue("_BLOB_");

	    return updatedAttr;
	} else
	    throw new IllegalArgumentException(value + ": Invalid value type");

    }

    public void removeConnectorData(ISocialConnector connector) {
	IndividualCtxEntity individualEntity;
	CtxAssociation snsAssoc = null;
	CtxEntity socialNetwork = null;

	LOG.debug("Removing data for" + connector.getConnectorName()
		+ " social Network...");
	try {
	    individualEntity = this.internalCtxBroker.retrieveIndividualEntity(
		    this.cssId).get();
	    Set<CtxAssociationIdentifier> snsAssocSet = individualEntity
		    .getAssociations(CtxAssociationTypes.IS_CONNECTED_TO_SNS);
	    LOG.debug("There are " + snsAssocSet.size()
		    + " associations with SocialNetworks");

	    if (snsAssocSet.size() > 0) {

		List<CtxAssociationIdentifier> snsAssocList = new ArrayList<CtxAssociationIdentifier>(
			snsAssocSet);
		for (CtxAssociationIdentifier assocID : snsAssocList) {
		    snsAssoc = (CtxAssociation) this.internalCtxBroker
			    .retrieve(assocID).get();
		    Set<CtxEntityIdentifier> snsEntitiesSet = snsAssoc
			    .getChildEntities(CtxEntityTypes.SOCIAL_NETWORK);
		    List<CtxEntityIdentifier> snsEntitiesList = new ArrayList<CtxEntityIdentifier>(
			    snsEntitiesSet);

		    LOG.debug("lookup SN association" + snName);
		    List<CtxEntityIdentifier> snEntList = this.internalCtxBroker
			    .lookupEntities(snsEntitiesList,
				    CtxAttributeTypes.TYPE,
				    connector.getConnectorName()).get();

		    // List<CtxEntityIdentifier> snEntList =
		    // this.internalCtxBroker
		    // .lookupEntities(snsEntitiesList,
		    // CtxAttributeTypes.NAME,
		    // connector.getConnectorName()).get();

		    if (snEntList.size() > 0) {
			socialNetwork = (CtxEntity) this.internalCtxBroker
				.retrieve(snEntList.get(0)).get();
			this.internalCtxBroker.remove(socialNetwork.getId());
			this.internalCtxBroker.remove(assocID);
			LOG.debug("All data about "
				+ connector.getConnectorName()
				+ " has been removed.");
		    }
		}

	    }
	    return;
	} catch (InterruptedException e) {
	    LOG.error("Error " + e, e);
	    e.printStackTrace();
	} catch (ExecutionException e) {
	    LOG.error("Error " + e, e);
	    e.printStackTrace();
	} catch (CtxException e) {
	    LOG.error("Error " + e, e);
	    e.printStackTrace();
	}

	LOG.error("An unexpected has broken cleaning data of "
		+ connector.getConnectorName());
    }

}