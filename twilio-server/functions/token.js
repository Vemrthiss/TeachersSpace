exports.handler = function(context, event, callback) {
    const twilioAccountSid = context.ACCOUNT_SID;
    const twilioApiKey = context.API_KEY_SID;
    const twilioApiSecret = context.API_SECRET;
    // any URL parameters passed on an API call to a Twilio Function are available on the event object
    // Think of the identity as a sort of username,
    // and consider carefully how you verify this identity before issuing an Access Token to the client.
    const identity = event.identity;

    const AccessToken = Twilio.jwt.AccessToken;
    const token = new AccessToken(
        twilioAccountSid,
        twilioApiKey,
        twilioApiSecret,
        {
            identity,
            ttl: 86400, // 24 hours, max lifespan
        }
    );
    
    const VoiceGrant = AccessToken.VoiceGrant;
    const voiceGrant = new VoiceGrant({
        outgoingApplicationSid: context.APP_SID,
        incomingAllow: true, // allows your client-side device to receive calls as well as make them
        pushCredentialSid: context.PUSH_CREDENTIAL_SID
    });
    token.addGrant(voiceGrant);

    const response = new Twilio.Response();
    const headers = {
        "Access-Control-Allow-Origin": "*", // change this to your client-side URL
        "Access-Control-Allow-Methods": "GET,PUT,POST,DELETE,OPTIONS",
        "Access-Control-Allow-Headers": "Content-Type",
        "Content-Type": "application/json"
    };
    response.setHeaders(headers);
    response.setBody(token.toJwt());

    return callback(null, response);
}