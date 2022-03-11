exports.handler = function(context, event, callback) {
    const response = new Twilio.Response();
    response.appendHeader("content-type", "text/xml");

    const twiml = new Twilio.twiml.VoiceResponse();
    twiml.say("Congratulations! You have received your first inbound call! Good bye.");

    response.setBody(twiml.toString());

    callback(null, response);
};