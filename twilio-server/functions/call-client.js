const callerNumber = '1234567890';
const callerId = 'client:alice';

exports.handler = function(context, event, callback) {
    const url = 'https://' + context.DOMAIN_NAME + '/incoming';

    const client = context.getTwilioClient();

    const { to } = event;
    if (!to) {
        callback("Valid to number/client was not provided", null);
    } else if (isE164Number(to)) {
        console.log("Calling number:" + to);
        client.calls.create({
            url,
            to,
            from: callerNumber,
        }, function(err, result) {    
            // End our function
            if (err) {
                callback(err, null);
            } else {
                callback(null, result);
            }
        });
    } else {
        // here calls a valid client
        client.calls.create({
            url,
            to: 'client:' + to,
            from: callerId,
        }, function(err, result) {    
            // End our function
            if (err) {
                callback(err, null);
            } else {
                callback(null, result);
            }
        });
    }
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