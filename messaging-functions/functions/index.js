const functions = require("firebase-functions");
const admin = require('firebase-admin');
admin.initializeApp();

// Create and Deploy Your First Cloud Functions
// https://firebase.google.com/docs/functions/write-firebase-functions

// exports.helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

exports.sendMessageNotification = functions
                                    .firestore
                                    .document('chats/{chatId}/messages/{messageId}')
                                    .onCreate(async (snapshot, context) => {
                                        const { receiverUID: toUid, senderUID: fromUid, body } = snapshot.data();
                                        if (fromUid && toUid) {
                                            functions.logger.log({fromUid, toUid, body});
                                        }

                                        const notificationId = Math.floor(Math.random() * 100).toString();

                                        const payload = {
                                            data: { fromUid, body, notificationId }
                                        }

                                        const topic = `/topics/${toUid}`;

                                        const response = await admin.messaging().sendToTopic(topic, payload);
                                        const fcmMessageId = response.messageId;

                                        functions.logger.log({fcmMessageId});
                                    });