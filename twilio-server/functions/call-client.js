const callerNumber = '+6597664983';
const callerId = 'client:alice';

exports.handler = function(context, event, callback) {
    const url = 'https://' + context.DOMAIN_NAME + '/incoming';

    const client = context.getTwilioClient();

    const { to, from } = event;
    const twiml = new Twilio.twiml.VoiceResponse();
    // TWIML VERSION
    if (!to) {
        twiml.say('Congratulations! You have made your first call! Good bye.');
    } else if (isE164Number(to)) {
        const dial = twiml.dial({callerId : callerNumber});
        dial.number(to);
    } else {
        const dial = twiml.dial({callerId : 'client:' + from});
        dial.client(to);
    }
    callback(null, twiml);

    // CALL RESOURCE VERSION
    // if (!to) {
    //     client.calls.create({
    //         url,
    //         to: '+6597664983',
    //         from: callerNumber
    //     });
    //     callback(null, "Valid to number/client was not provided, calling the default number");
    // } else if (isE164Number(to)) {
    //     console.log("Calling number:" + to);
    //     client.calls.create({
    //         url,
    //         to,
    //         from: callerNumber,
    //     }, function(err, result) {
    //         // End our function
    //         if (err) {
    //             callback(err, null);
    //         } else {
    //             callback(null, result);
    //         }
    //     });
    // } else {
    //     // here calls a valid client
    //     client.calls.create({
    //         url,
    //         to: 'client:' + to,
    //         from: 'client:' + from,
    //     }, function(err, result) {
    //         // End our function
    //         if (err) {
    //             callback(err, null);
    //         } else {
    //             callback(null, result);
    //         }
    //     });
    // }
}

function isE164Number(to) {
    if(to.length == 1) {
        if(!isNaN(to)) {
            console.log("It is a 1 digit long number" + to);
            return true;
        }
    } else if(String(to).charAt(0) == '+') {
        number = to.substring(1);
        if(!isNaN(number)) {
            console.log("It is a number " + to);
            return true;
        };
    } else {
        if(!isNaN(to)) {
            console.log("It is a number " + to);
            return true;
        }
    }
    console.log("not a number");
    return false;
}