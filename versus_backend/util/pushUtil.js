var pubnub = require("pubnub")({
    ssl           : true,  // <- enable TLS Tunneling over TCP
    publish_key   : "pub-c-8028947a-380c-420e-be14-cc9216642edd",
    subscribe_key : "sub-c-78d1252e-e658-11e5-a4f2-0619f8945a4f"
});

var send = (fbid, data) => {
	pubnub.publish({                                    
		channel : "f" + fbid,
		message : data,
		callback : function(m){console.log("push sent to user " + fbid)}
	});
};

module.exports = {
	send : send
};