// const { getMessaging } = require('firebase-admin/messaging');
// const { initializeApp } = require('firebase-admin/app');
var admin = require("firebase-admin");
var serviceAccount = require("../firebase_admin.json");

const app = admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

var express = require('express');
var router = express.Router();

// // Your web app's Firebase configuration
// // For Firebase JS SDK v7.20.0 and later, measurementId is optional
// const firebaseConfig = {
//     apiKey: "AIzaSyAF7p-BqV4LQIyDhHhFUMVFtpIBYRqKMi4",
//     authDomain: "cpen321-recipe-roulette-401802.firebaseapp.com",
//     projectId: "cpen321-recipe-roulette-401802",
//     storageBucket: "cpen321-recipe-roulette-401802.appspot.com",
//     messagingSenderId: "217153905955",
//     appId: "1:217153905955:web:def5ae4f0059d5763d9896",
//     measurementId: "G-MLT9C3GYJ1"
//   };

// // Initialize Firebase
// const app = initializeApp(firebaseConfig);


// Initialize Firebase Cloud Messaging and get a reference to the service
const messaging = admin.messaging(app);

// Send a message to the device corresponding to the provided
// registration token.
  
sendMessage = (req) => {
    // This registration token comes from the client FCM SDKs.
    console.log(req.body.tok);
    let registrationToken = req.body.tok;
    const message = {
    data: {
        score: '850',
        time: '2:45'
    },
    token: 'f54vS09YT6GnAzD4Bh-Q-v:APA91bHWZT5rvKn5HV5By2mAUj9q2MPjkyl34QizZQ1N1IP0CN3BD6xkj32MWfD1GPs-Qd48ZICPaZoMrOycCEh96Y0Y5ks0R7BBJ6Xfg2Q97mH8xAVj7igVLn6Ybyzjo2Q-KHwGTgQU'
    };

    messaging.send(message).then((response) => {
        // Response is a message ID string.
        console.log('Successfully sent message:', response);
    })
    .catch((error) => {
        console.log('Error sending message:', error);
    });
}
router.post('/new', async function(req, res, next) {
    sendMessage(req);
    res.send('Response with msg');
});
  
module.exports = router;
  
