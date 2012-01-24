/**
 * This Manager creates functions to use the XEP-SOC1
 * http://github.com/societies/Societies-Seed-Documentation/xeps/html/xep-SOC1.html
 * Notice: all functions are not yet implemented
 */
var CommunityManager = {
	connection: null,
	NS_DISCO_ITEMS: 'http://jabber.org/protocol/disco#items',
	NS_DISCO_INFO: 'http://jabber.org/protocol/disco#info',
	NS_PUBSUB: 'http://jabber.org/protocol/pubsub',
	NS_DEFAULT_COMMUNITY: 'http://socialblend.org/community',
	NS_DEFAULT_MANAGER: 'http://socialblend.org/manager',
	
	init: function(connectionXMPP) {
		console.log("connexion is", connectionXMPP);
		CommunityManager.connection = connectionXMPP;
	},
	
	// -- Actions
	// JOIN
	join: function(communitiesEndpoint, communityJid, on_success, on_error) {
		var iq = $iq({type: 'set', to: communityJid}).c('community', {xmlns: communitiesEndpoint}).c('join');
		CommunityManager.connection.sendIQ(iq, on_success, on_error);
	},
	
	// LEAVE
	leave: function(communitiesEndpoint, communityJid, on_success, on_error) {
		var iq = $iq({type: 'set', to: communityJid}).c('community', {xmlns: communitiesEndpoint}).c('leave');
		CommunityManager.connection.sendIQ(iq, on_success, on_error);
	},
	
	// DISCOVER COMMUNITIES AND ENDPOINTS
	discoverCommunityManagers: function(managerJid, on_success, on_error) {
		var iq = $iq({type: 'get', to: managerJid}).c('query', {xmlns: CommunityManager.NS_DISCO_INFO});
		CommunityManager.connection.sendIQ(iq, on_success, on_error);
	},
	discoverCommunities: function(managerJid, on_success, on_error) {
		var iq = $iq({type: 'get', to: managerJid}).c('query', {xmlns: CommunityManager.NS_DISCO_ITEMS});
		CommunityManager.connection.sendIQ(iq, on_success, on_error);
	},
	discoverCommunityInfos: function(communityJid, on_success, on_error) {
		var iq = $iq({type: 'get', to: communityJid}).c('query', {xmlns: CommunityManager.NS_DISCO_INFO});
		CommunityManager.connection.sendIQ(iq, on_success, on_error);
	},
	
	// DISCOVER NODE
	discoverNodes: function(pubsubManagerJid, on_success, on_error) {
		var iq = $iq({type: 'get', to: pubsubManagerJid}).c('query', {xmlns: CommunityManager.NS_DISCO_ITEMS});
		CommunityManager.connection.sendIQ(iq, on_success, on_error);
	},
	discoverNodeInfos: function(pubsubManagerJid, nodeName, on_success, on_error) {
		var iq = $iq({type: 'get', to: pubsubManagerJid}).c('query', {xmlns: CommunityManager.NS_DISCO_INFO, node: nodeName});
		CommunityManager.connection.sendIQ(iq, on_success, on_error);
	},
	requestAllNodeItems: function(pubsubManagerJid, nodeName, on_success, on_error) {
		var iq = $iq({type: 'get', to: pubsubManagerJid}).c('pubsub', {xmlns: CommunityManager.NS_DISCO_ITEMS}).c('items', {node: nodeName});
		CommunityManager.connection.sendIQ(iq, on_success, on_error);
	},
	requestRecentNodeItems: function(pubsubManagerJid, nodeName, nbOfItems, on_success, on_error) {
		var iq = $iq({type: 'get', to: pubsubManagerJid});
		iq.c('pubsub', {xmlns: CommunityManager.NS_DISCO_ITEMS}).c('items', {node: nodeName, max_items: nbOfItems});
		CommunityManager.connection.sendIQ(iq, on_success, on_error);
	},
	requestNodeItem: function(pubsubManagerJid, nodeName, itemId, on_success, on_error) {
		var iq = $iq({type: 'get', to: pubsubManagerJid});
		iq.c('pubsub', {xmlns: CommunityManager.NS_DISCO_ITEMS}).c('items', {node: nodeName}).c('item', {id: itemId});
		CommunityManager.connection.sendIQ(iq, on_success, on_error);
	},
	
	// CREATE
	create: function(communityManagerEndpoint, managerJid, communityJid, on_success, on_error) {
		var iq = $iq({type: 'set', to: managerJid}).c('community', {xmlns: communityManagerEndpoint}).c('create');
		CommunityManager.connection.sendIQ(iq, on_success, on_error);
	},
	
	// DELETE
	// Notice: sorry but "delete" is a Javascript keyword
	del: function(communityManagerEndpoint, managerJid, communityJid, on_success, on_error) {
		var iq = $iq({type: 'set', to: managerJid}).c('community', {xmlns: communityManagerEndpoint}).c('delete', {'jid': communityJid});
		CommunityManager.connection.sendIQ(iq, on_success, on_error);
	}
}
